/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesAugmentable;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.model.ISeriesProcessor;
import com.quantcomponents.core.model.OrderSide;
import com.quantcomponents.core.series.SimplePoint;

/**
 * Processor that listens to trades and prices and generates a series of realtime trade statistics
 */
public class TradeStatsProcessor implements ISeriesProcessor<Date, Double>, IPositionProvider, ITradingStatsProvider, ISeriesListener<Date, Double> {
	public static final String INPUT_SERIES_NAME = "TRADES_AND_POSITIONS";
	private final Map<IContract, IPosition> positions = new ConcurrentHashMap<IContract, IPosition>();
	private volatile ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> inputSeries;
	private volatile ISeriesAugmentable<Date, Double, ISeriesPoint<Date, Double>> outputSeries;
	private volatile TradeStatsPoint currentTradePoint;
	private volatile SimplePoint highestEquityPoint;
	private volatile SimplePoint lowestEquityPoint;
	private volatile ITradeStatsPoint worstTrade;
	private volatile ITradeStatsPoint bestTrade;
	private volatile SimplePoint startOfMaxDrawdown;
	private volatile SimplePoint endOfMaxDrawdown;
	private volatile SimplePoint startOfMaxRunup;
	private volatile SimplePoint endOfMaxRunup;

	@Override
	public void wire(Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> inputSeriesMap, ISeriesAugmentable<Date, Double, ISeriesPoint<Date, Double>> outputSeries) {
		this.inputSeries = inputSeriesMap.get(INPUT_SERIES_NAME);
		this.outputSeries = outputSeries;
		if (!inputSeries.isEmpty()) {
			for (ISeriesPoint<Date, Double> point : this.inputSeries) {
				onItemAdded(point);
			}
		}
		this.inputSeries.addSeriesListener(this);
	}

	@Override
	public void unwire() {
		if (inputSeries != null) {
			inputSeries.removeSeriesListener(this);
			inputSeries = null;
		}
		outputSeries = null;
	}
	
	@Override
	public Map<IContract, IPosition> getPositions() {
		return new HashMap<IContract, IPosition>(positions);
	}
	
	@Override
	public SimplePoint getHighestEquityPoint() {
		return highestEquityPoint;
	}

	@Override
	public SimplePoint getLowestEquityPoint() {
		return lowestEquityPoint;
	}

	@Override
	public ITradeStatsPoint getWorstTrade() {
		return worstTrade;
	}

	@Override
	public ITradeStatsPoint getBestTrade() {
		return bestTrade;
	}

	@Override
	public SimplePoint getStartOfMaxDrawdown() {
		return startOfMaxDrawdown;
	}

	@Override
	public SimplePoint getEndOfMaxDrawdown() {
		return endOfMaxDrawdown;
	}

	@Override
	public SimplePoint getStartOfMaxRunup() {
		return startOfMaxRunup;
	}

	@Override
	public SimplePoint getEndOfMaxRunup() {
		return endOfMaxRunup;
	}

	@Override
	public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) { }

	@Override
	public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
		if (newItem instanceof IPositionPoint) {
			IPositionPoint positionPoint = (IPositionPoint) newItem;
			onPositionUpdate(positionPoint.getContract(), positionPoint.getPosition());
		} else if (newItem instanceof ITradePoint) {
			ITradePoint tradePoint = (ITradePoint) newItem;
			onTrade(tradePoint.getTrade());
		}
	}

	private void onTrade(ITrade trade) {
		changeCurrentTrade(trade);
		updatePriceStats(trade.getExecutionTime());
	}

	private void onPositionUpdate(IContract contract, IPosition position) {
		positions.put(contract, position);
		if (currentTradePoint != null) {
			ITrade currentTrade = currentTradePoint.getTrade();
			if (currentTrade.getOrder().getContract().equals(contract)) {
				currentTradePoint.setTradePnl((position.getMarketPrice() - currentTrade.getAveragePrice()) * currentTrade.getAmount() * (currentTrade.getOrder().getSide() == OrderSide.BUY ? 1.0 : -1.0));
			}
			updateCurrentTradeStats();
			updatePriceStats(position.getTimestamp());
		}
	}
	
	private void changeCurrentTrade(ITrade nextTrade) {
		if (currentTradePoint != null) {
			ITrade currentTrade = currentTradePoint.getTrade();
			currentTradePoint.setTradePnl(currentTrade.getAmount() * (nextTrade.getAveragePrice() - currentTrade.getAveragePrice()) * (currentTrade.getOrder().getSide() == OrderSide.BUY ? 1.0 : -1.0));
			currentTradePoint.setTradeEnd(nextTrade.getExecutionTime());
			updateCurrentTradeStats();
			double tradePnl = currentTradePoint.getTradePnl();
			if (worstTrade == null || tradePnl < worstTrade.getTradePnl()) {
				worstTrade = currentTradePoint;
			}
			if (bestTrade == null || tradePnl > bestTrade.getTradePnl()) {
				bestTrade = currentTradePoint;
			}
			if (outputSeries != null) {
				outputSeries.updateTail(currentTradePoint);
			}
		}
		
		currentTradePoint = new TradeStatsPoint(nextTrade, nextTrade.getExecutionTime());
		if (outputSeries != null) {
			outputSeries.insertFromTail(currentTradePoint);
		}
	}
	
	private void updateCurrentTradeStats() {
		double tradePnl = currentTradePoint.getTradePnl();
		if (tradePnl < currentTradePoint.getMaxAdverseExcursion()) {
			currentTradePoint.setMaxAdverseExcursion(tradePnl);
		}
		if (tradePnl > currentTradePoint.getMaxFavorableExcursion()) {
			currentTradePoint.setMaxFavorableExcursion(tradePnl);
		}
		if (outputSeries != null) {
			outputSeries.updateTail(currentTradePoint);
		}
	}

	private void updatePriceStats(Date timestamp) {
		double pnl = calculateTotalPnl();
		SimplePoint point = new SimplePoint(timestamp, pnl);
		if (lowestEquityPoint == null || pnl < lowestEquityPoint.getValue()) {
			lowestEquityPoint = point;
		}
		if (highestEquityPoint == null || pnl > highestEquityPoint.getValue()) {
			highestEquityPoint = point;
		}
		double currentRunup = pnl - lowestEquityPoint.getValue();
		if (startOfMaxRunup == null || currentRunup > endOfMaxRunup.getValue() - startOfMaxRunup.getValue()) {
			startOfMaxRunup = lowestEquityPoint;
			endOfMaxRunup = point;
		}
		double currentDrawdown = pnl - highestEquityPoint.getValue();
		if (startOfMaxDrawdown == null || currentDrawdown < endOfMaxDrawdown.getValue() - startOfMaxDrawdown.getValue()) {
			startOfMaxDrawdown = highestEquityPoint;
			endOfMaxDrawdown = point;
		}
	}
	
	private double calculateTotalPnl() {
		double pnl = 0.0;
		for (IPosition position : positions.values()) {
			pnl += position.getUnrealizedPnl();
			pnl += position.getRealizedPnl();
		}
		return pnl;
	}

}
