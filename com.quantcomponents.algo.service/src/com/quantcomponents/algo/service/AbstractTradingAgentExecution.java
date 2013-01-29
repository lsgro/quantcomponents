/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.service;

import java.net.ConnectException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.algo.IExecutionService;
import com.quantcomponents.algo.IOrder;
import com.quantcomponents.algo.IOrderStatusListener;
import com.quantcomponents.algo.IPosition;
import com.quantcomponents.algo.ITrade;
import com.quantcomponents.algo.ITradingAgent;
import com.quantcomponents.algo.ITradingAgentExecution;
import com.quantcomponents.algo.OrderPoint;
import com.quantcomponents.algo.PositionPoint;
import com.quantcomponents.algo.TradePoint;
import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesAugmentable;
import com.quantcomponents.core.model.ISeriesPoint;

public abstract class AbstractTradingAgentExecution implements ITradingAgentExecution, IOrderStatusListener {
	protected static final Logger logger = Logger.getLogger(SimulatedTradingAgentExecution.class.getName());
	private volatile Set<String> ordersInProcess;
	private volatile Set<String> ordersExecuted;
	protected volatile Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> inputSeriesMap;
	protected volatile ISeriesAugmentable<Date, Double, ISeriesPoint<Date, Double>> outputSeries;
	protected volatile ITradingAgent tradingAgent;
	protected volatile IExecutionService executionService;

	public AbstractTradingAgentExecution(ITradingAgent tradingAgent, IExecutionService executionService) {
		this.tradingAgent = tradingAgent;
		this.executionService = executionService;
		init();
	}
	
	private void init() {
		ordersInProcess = new CopyOnWriteArraySet<String>();
		ordersExecuted = new CopyOnWriteArraySet<String>();
	}

	protected abstract Date getCurrentTime();

	@Override
	public void pause() {
		tradingAgent.pause();
	}

	@Override
	public void resume() {
		tradingAgent.resume();
	}

	@Override
	public void kill() {
		tradingAgent.kill();
	}

	@Override
	public RunningStatus getRunningStatus() {
		return tradingAgent.getRunningStatus();
	}
	
	@Override
	public void wire(Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> inputSeriesMap, ISeriesAugmentable<Date, Double, ISeriesPoint<Date, Double>> outputSeries) {
		this.inputSeriesMap = inputSeriesMap;
		this.outputSeries = outputSeries;
	}

	@Override
	public void unwire() {
		this.inputSeriesMap = null;
		this.outputSeries = null;
	}

	@Override
	public Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> getInput() {
		return inputSeriesMap;
	}

	@Override
	public ISeries<Date, Double, ISeriesPoint<Date, Double>> getOutput() {
		return outputSeries;
	}
	
	@Override
	public String sendOrder(IOrder order) throws ConnectException, RequestFailedException {
		String orderId = executionService.sendOrder(order);
		orderSent(orderId);
		if (outputSeries != null) {
			outputSeries.insertFromTail(new OrderPoint(getCurrentTime(), order));
		}
		return orderId;
	}

	@Override
	public String[] sendBracketOrders(IOrder parent, IOrder[] children) throws ConnectException, RequestFailedException {
		String[] returnValue = executionService.sendBracketOrders(parent, children);
		if (outputSeries != null) {
			Date orderDate = getCurrentTime();
			outputSeries.insertFromTail(new OrderPoint(orderDate, parent));
			for (IOrder child : children) {
				outputSeries.insertFromTail(new OrderPoint(orderDate, child));
			}
		}
		for (String orderId : returnValue) {
			orderSent(orderId);
		}
		return returnValue;
	}

	@Override
	public void onPositionUpdate(IContract contract, IPosition position) {
		tradingAgent.onPositionUpdate(contract, position);
		if (outputSeries != null) {
			outputSeries.insertFromTail(new PositionPoint(contract, position));
		}
	}

	@Override
	public void onOrderSubmitted(String orderId, boolean active) {
		tradingAgent.onOrderSubmitted(orderId, active);
	}

	@Override
	public void onOrderFilled(String orderId, int filled, boolean full, double averagePrice) {
		tradingAgent.onOrderFilled(orderId, filled, full, averagePrice);
		orderExecuted(orderId);
	}

	@Override
	public void onOrderCancelled(String orderId) {
		tradingAgent.onOrderCancelled(orderId);
	}

	@Override
	public void onOrderStatus(String orderId, String status) {
		tradingAgent.onOrderStatus(orderId, status);
	}

	protected void processTrade(String orderId) {
		try {
			Iterator<ITrade> tradeIterator = executionService.getTrades().descendingIterator();
			while (tradeIterator.hasNext()) {
				ITrade trade = tradeIterator.next();
				if (trade.getOrder() != null && trade.getOrder().getId().equals(orderId)) {
					TradePoint tradePoint = new TradePoint(trade.getExecutionTime(), trade);
					if (outputSeries != null) {
						outputSeries.insertFromTail(tradePoint);
					}
					break;
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Trade processing failed", e);
		} 
	}
	
	private void orderSent(String orderId) {
		String orderIdToProcess = null;
		synchronized (this) {
			if (ordersExecuted.remove(orderId)) {
				orderIdToProcess = orderId;
			} else {
				ordersInProcess.add(orderId);
			}
		}
		if (orderIdToProcess != null) {
			processTrade(orderIdToProcess);
		}
	}
	
	private void orderExecuted(String orderId) {
		String orderIdToProcess = null;
		synchronized (this) {
			if (ordersInProcess.remove(orderId)) {
				orderIdToProcess = orderId;
			} else {
				ordersExecuted.add(orderId);
			}
		}
		if (orderIdToProcess != null) {
			processTrade(orderIdToProcess);
		}
		
	}
		
}