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
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.algo.ICommissionCalculator;
import com.quantcomponents.algo.IOrder;
import com.quantcomponents.algo.IOrderStatusListener;
import com.quantcomponents.algo.IPosition;
import com.quantcomponents.algo.IPositionListener;
import com.quantcomponents.algo.ISimulatedExecutionService;
import com.quantcomponents.algo.ITrade;
import com.quantcomponents.algo.OrderBean;
import com.quantcomponents.algo.PositionBean;
import com.quantcomponents.algo.TradeBean;
import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.model.OrderSide;
import com.quantcomponents.core.model.OrderType;
import com.quantcomponents.core.model.beans.ContractBean;
import com.quantcomponents.marketdata.IStockDataCollection;

public class SimulatedExecutionService implements ISimulatedExecutionService {
	private static final Logger logger = Logger.getLogger(SimulatedExecutionService.class.getName());
	private final ConcurrentLinkedQueue<ITrade> trades = new ConcurrentLinkedQueue<ITrade>();
	private final Map<IContract, SeriesListener> priceListeners = new HashMap<IContract, SeriesListener>();
	private final Map<ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>, SeriesListener> listenersBySeries = new HashMap<ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>, SeriesListener>();
	private final Set<IOrderStatusListener> orderStatusListeners = new HashSet<IOrderStatusListener>();
	private final Set<IPositionListener> orderPositionListeners = new HashSet<IPositionListener>();
	private final LinkedList<OrderInfo> currentOrders = new LinkedList<OrderInfo>();
	private final LinkedList<OrderInfo> nextOrders = new LinkedList<OrderInfo>();
	private final PositionCalculator positionCalculator = new PositionCalculator();
	private final ICommissionCalculator commissionCalculator;
	private double slippage = 0.0;
	private double tradeCommission = 0.0;
	private double defaultBidAskSpread = 0.0;
	private int nextInternalId;
	private int nextOcaGroupId;
	
	private static class OrderInfo {
		public OrderInfo(IOrder order, int internalId, Integer ocaGroupId, Integer parentOrderId) {
			this.order = order;
			this.internalId = internalId;
			this.ocaGroupId = ocaGroupId;
			this.parentOrderId = parentOrderId;
		}
		public OrderInfo clone() {
			OrderInfo cloned = new OrderInfo(order, internalId, ocaGroupId, parentOrderId);
			cloned.toRemove = toRemove;
			return cloned;
		}
		int internalId;
		IOrder order;
		Integer ocaGroupId;
		Integer parentOrderId;
		boolean toRemove;
	}
	
	private class SeriesListener implements ISeriesListener<Date, Double> {
		private final IContract contract;
		private volatile Double lastPrice;
		private volatile Date lastPriceTimestamp;
		
		public SeriesListener(IContract contract) {
			this.contract = contract;
		}

		@Override
		public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) {
			double newPrice = updatedItem.getValue();
			lastPriceTimestamp = updatedItem.getIndex();
			if (lastPrice == null || newPrice != lastPrice) {
				lastPrice = newPrice;
				if (contract != null) {
					positionCalculator.onPriceUpdate(contract, updatedItem);
				}
				processOrders();
				notifyPositionListeners(contract);
			}
		}

		@Override
		public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
			onItemUpdated(null, newItem);
		}
		
		public Double getLastPrice() {
			return lastPrice;
		}
		
		public Date getLastPriceTimestamp() {
			return lastPriceTimestamp;
		}
	}
	
	/**
	 * Backtest execution service 
	 * @param commissionCalculator An {@link ICommissionCalculator}
	 * @param autoReset Set to true if the position must be reset when a stale price is received: useful when repeating tests
	 * @throws ConnectException
	 * @throws RequestFailedException
	 */
	public SimulatedExecutionService(ICommissionCalculator commissionCalculator) {
		this.commissionCalculator = commissionCalculator;
	}
	
	@Override
	public synchronized void addOrderStatusListener(IOrderStatusListener listener) {
		orderStatusListeners.add(listener);
	}

	@Override
	public synchronized void removeOrderStatusListener(IOrderStatusListener listener) {
		orderStatusListeners.remove(listener);
	}

	@Override
	public synchronized String sendOrder(IOrder order) {
		int internalId = nextInternalId();
		OrderBean orderBean = OrderBean.copyOf(order);
		orderBean.setId(Integer.toString(internalId));
		processOrder(new OrderInfo(orderBean, internalId, null, null));
		removeDeletedOrders();
		return Integer.toString(internalId);
	}

	@Override
	public synchronized String[] sendBracketOrders(IOrder parent, IOrder[] children) {
		String[] ids = new String[children.length + 1];
		int parentInternalId = nextInternalId();
		ids[0] = Integer.toString(parentInternalId);
		int ocaGroupId = nextOcaGroupId();
		OrderBean parentOrderBean = OrderBean.copyOf(parent);
		parentOrderBean.setId(Integer.toString(parentInternalId));
		currentOrders.add(new OrderInfo(parentOrderBean, parentInternalId, null, null));
		for (int i = 0; i < children.length; i++) {
			IOrder child = children[i];
			OrderBean childOrderBean = OrderBean.copyOf(child);
			int childId = nextInternalId();
			childOrderBean.setId(Integer.toString(childId));
			ids[i + 1] = Integer.toString(childId);
			currentOrders.add(new OrderInfo(childOrderBean, childId, ocaGroupId, parentInternalId));
		}
		processOrders();
		return ids;
	}

	protected synchronized double getSlippage() {
		return slippage;
	}

	protected synchronized void setSlippage(double slippage) {
		this.slippage = slippage;
	}

	protected synchronized double getTradeCommission() {
		return tradeCommission;
	}

	protected synchronized void setTradeCommission(double tradeCommission) {
		this.tradeCommission = tradeCommission;
	}

	public synchronized double getDefaultBidAskSpread() {
		return defaultBidAskSpread;
	}

	public synchronized void setDefaultBidAskSpread(double defaultBidAskSpread) {
		this.defaultBidAskSpread = defaultBidAskSpread;
	}

	@Override
	public synchronized void addPositionListener(IPositionListener listener) throws ConnectException {
		orderPositionListeners.add(listener);
	}

	@Override
	public synchronized void removePositionListener(IPositionListener listener) throws ConnectException {
		orderPositionListeners.remove(listener);
	}

	@Override
	public Deque<ITrade> getTrades() {
		return new LinkedList<ITrade>(trades);
	}

	private boolean processOrder(OrderInfo orderInfo) {
		if (!OrderType.LIMIT.equals(orderInfo.order.getType()) && !OrderType.MARKET.equals(orderInfo.order.getType()) && !OrderType.STOP.equals(orderInfo.order.getType())) {
			throw new UnsupportedOperationException("Only LIMIT, MARKET and STOP orders are supported");
		}
		for (IOrderStatusListener listener : orderStatusListeners) {
			listener.onOrderSubmitted(Integer.toString(orderInfo.internalId), orderInfo.parentOrderId == null);
		}
		Double executionPrice = null;
		boolean executed = false;
		if (OrderSide.BUY.equals(orderInfo.order.getSide())) {
			executionPrice = getActualBuyPrice(orderInfo.order.getContract());
			if (OrderType.MARKET.equals(orderInfo.order.getType())
					|| OrderType.LIMIT.equals(orderInfo.order.getType()) && executionPrice <= orderInfo.order.getLimitPrice()
					|| OrderType.STOP.equals(orderInfo.order.getType()) && executionPrice >= orderInfo.order.getAuxPrice()) {
				executeTrade(orderInfo, orderInfo.order.getAmount(), executionPrice);
				executed = true;
			}
		} else if (OrderSide.SELL.equals(orderInfo.order.getSide())) {
			executionPrice = getActualSellPrice(orderInfo.order.getContract());
			if (OrderType.MARKET.equals(orderInfo.order.getType())
					|| OrderType.LIMIT.equals(orderInfo.order.getType()) && executionPrice >= orderInfo.order.getLimitPrice()
					|| OrderType.STOP.equals(orderInfo.order.getType()) && executionPrice <= orderInfo.order.getAuxPrice()) {
				executeTrade(orderInfo, orderInfo.order.getAmount(), executionPrice);
				executed = true;
			}
		}
		if (executed) {
			processChildren(orderInfo.internalId);
			if (orderInfo.ocaGroupId != null) {
				cancelOcaGroup(orderInfo.ocaGroupId);
			}
		} else {
			OrderInfo cloned = orderInfo.clone();
			cloned.toRemove = false;
			nextOrders.add(cloned); // leave it in the next orders queue
		}
		return executed;
	}
	
	private void processOrders() {
		consolidateOrderQueue();
		for (OrderInfo orderInfo : currentOrders) {
			if (!orderInfo.toRemove && orderInfo.parentOrderId == null) {
				orderInfo.toRemove = true;
				processOrder(orderInfo);
			}
		}
		removeDeletedOrders();
	}
	
	private void processChildren(int parentId) {
		for (OrderInfo orderInfo : currentOrders) {
			if (!orderInfo.toRemove && orderInfo.parentOrderId != null && orderInfo.parentOrderId == parentId) {
				orderInfo.toRemove = true;
				orderInfo.parentOrderId = null; // next time don't wait for parent execution
				processOrder(orderInfo);
			}
		}
	}
	
	private void cancelOcaGroup(int ocaGroupId) {
		for (OrderInfo orderInfo : currentOrders) {
			if (!orderInfo.toRemove && orderInfo.ocaGroupId != null && orderInfo.ocaGroupId == ocaGroupId) {
				orderInfo.toRemove = true;
			}
		}
		for (OrderInfo orderInfo : nextOrders) {
			if (!orderInfo.toRemove && orderInfo.ocaGroupId != null && orderInfo.ocaGroupId == ocaGroupId) {
				orderInfo.toRemove = true;
			}
		}
	}
	
	private Double getLastPrice(IContract contract) {
		SeriesListener listener = priceListeners.get(contract);
		if (listener != null) {
			return listener.getLastPrice();
		} else {
			logger.log(Level.WARNING, "Price listener not found for contract: " + contract);
		}
		return null;
	}
	
	private Date getLastPriceTimestamp(IContract contract) {
		SeriesListener listener = priceListeners.get(contract);
		if (listener != null) {
			return listener.getLastPriceTimestamp();
		} else {
			logger.log(Level.WARNING, "Price listener not found for contract: " + contract);
		}
		return null;
	}
	
	private Double getActualBuyPrice(IContract contract) {
		Double lastPrice = getLastPrice(contract);
		if (lastPrice != null) {
			return lastPrice + defaultBidAskSpread / 2.0 + slippage;
		} else {
			return null;
		}
	}
	
	private Double getActualSellPrice(IContract contract) {
		Double lastPrice = getLastPrice(contract);
		if (lastPrice != null) {
			return lastPrice - defaultBidAskSpread / 2.0 - slippage;
		} else {
			return null;
		}
	}
	
	private int nextOcaGroupId() {
		return nextOcaGroupId++;
	}
	
	private int nextInternalId() {
		return nextInternalId++;
	}
	
	private void consolidateOrderQueue() {
		currentOrders.addAll(nextOrders);
		nextOrders.clear();
	}
	
	private void removeDeletedOrders() {
		Iterator<OrderInfo> iteratorCurrent = currentOrders.iterator();
		while (iteratorCurrent.hasNext()) {
			OrderInfo orderInfo = iteratorCurrent.next();
			if (orderInfo.toRemove) {
				iteratorCurrent.remove();
				for (IOrderStatusListener listener : orderStatusListeners) {
					listener.onOrderCancelled(Integer.toString(orderInfo.internalId));
				}
			}
		}
		Iterator<OrderInfo> iteratorNext = nextOrders.iterator();
		while (iteratorNext.hasNext()) {
			OrderInfo orderInfo = iteratorNext.next();
			if (orderInfo.toRemove) {
				iteratorNext.remove();
				for (IOrderStatusListener listener : orderStatusListeners) {
					listener.onOrderCancelled(Integer.toString(orderInfo.internalId));
				}
			}
		}
	}
	
	private void executeTrade(OrderInfo orderInfo, int tradeAmount, double executionPrice) {
		double multiplier = 1.0;
		IContract contract = orderInfo.order.getContract();
		if (contract.getMultiplier() != null && contract.getMultiplier() != 0) {
			multiplier = contract.getMultiplier();
		}
		int signedTradeAmount = orderInfo.order.getSide() == OrderSide.BUY ? tradeAmount : -tradeAmount;
		double tradeCommissionAmount = commissionCalculator.calculateCommission(orderInfo.order, tradeAmount, executionPrice);
		double tradeCashFlow = -(signedTradeAmount * multiplier * executionPrice) - tradeCommissionAmount;
		double tradeAveragePrice = Math.abs(tradeCashFlow / multiplier / tradeAmount);
		TradeBean trade = new TradeBean(orderInfo.order, "Simulated", getLastPriceTimestamp(contract), tradeAmount, executionPrice, tradeAveragePrice);
		trades.add(trade);
		positionCalculator.onTrade(trade);
		for (IOrderStatusListener listener : orderStatusListeners) {
			listener.onOrderFilled(Integer.toString(orderInfo.internalId), tradeAmount, orderInfo.order.getAmount() == tradeAmount, executionPrice);
		}
		notifyPositionListeners(contract);
		notifyPositionListeners(ContractBean.cash(contract.getCurrency()));
	}
	
	private void notifyPositionListeners(IContract contract) {
		IPosition position = positionCalculator.getPositions().get(contract);
		if (position != null) {
			for (IPositionListener listener : orderPositionListeners) {
				listener.onPositionUpdate(contract, new PositionBean(position));
			}
		}
	}

	public synchronized void stop() {
		for (Map.Entry<ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>, SeriesListener> entry : listenersBySeries.entrySet()) {
			entry.getKey().removeSeriesListener(entry.getValue());
		}
	}

	@Override
	public synchronized void setInputSeries(Collection<ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> inputSeries) {
		for (ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> input : inputSeries) {
			IContract contract = null;
			if (input instanceof IStockDataCollection) {
				contract = ((IStockDataCollection) input).getContract();
			}
			SeriesListener seriesListener = new SeriesListener(contract);
			input.addSeriesListener(seriesListener);
			if (contract != null) {
				priceListeners.put(contract, seriesListener);
			} 
			listenersBySeries.put(input, seriesListener);
		}
	}
}
