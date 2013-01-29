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

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.quantcomponents.algo.IPosition;
import com.quantcomponents.algo.ITrade;
import com.quantcomponents.algo.ITradingListener;
import com.quantcomponents.algo.PositionBean;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.model.OrderSide;
import com.quantcomponents.core.model.beans.ContractBean;

public class PositionCalculator implements ITradingListener {
	private final Map<IContract, PositionBean> positions = new ConcurrentHashMap<IContract, PositionBean>();
	private final Map<IContract, PositionBean> readonlyPositions = Collections.unmodifiableMap(positions);
	
	public Map<IContract, ? extends IPosition> getPositions() {
		return readonlyPositions;
	}
	
	public synchronized void reset() {
		positions.clear();
	}
	
	@Override
	public synchronized void onTrade(ITrade trade) {
		IContract contract = trade.getOrder().getContract();
		IContract currency = ContractBean.cash(contract.getCurrency());
		PositionBean contractPosition = positions.get(contract);
		if (contractPosition == null) {
			contractPosition = new PositionBean();
			positions.put(contract, contractPosition);
		}
		PositionBean cashPosition = positions.get(currency);
		if (cashPosition == null) {
			cashPosition = new PositionBean();
			cashPosition.setAveragePrice(1.0);
			cashPosition.setMarketPrice(1.0);
			positions.put(currency, cashPosition);
		}
		Integer contractMultiplier = contract.getMultiplier();
		if (contractMultiplier == null || contractMultiplier == 0) {
			contractMultiplier = 1;
		}
		contractPosition.setMarketPrice(trade.getExecutionPrice());
		contractPosition.setTimestamp(trade.getExecutionTime());
		cashPosition.setTimestamp(trade.getExecutionTime());
		double residueSignedPositionAmount = contractPosition.getSignedAmount();
		int residueTradeAmount = trade.getAmount();
		// reducing position
		if (residueSignedPositionAmount != 0) {
			if (residueSignedPositionAmount > 0 && trade.getOrder().getSide() == OrderSide.SELL) { // reducing long position
				double positionReduction = Math.min(residueSignedPositionAmount, residueTradeAmount);
				contractPosition.setRealizedPnl(contractPosition.getRealizedPnl() + (trade.getAveragePrice() - contractPosition.getAveragePrice()) * contractMultiplier * positionReduction);
				residueSignedPositionAmount -= positionReduction;
				residueTradeAmount -= positionReduction;
			} else if (residueSignedPositionAmount < 0 && trade.getOrder().getSide() == OrderSide.BUY) { // reducing short position
				double positionReduction = Math.min(-residueSignedPositionAmount, residueTradeAmount);
				contractPosition.setRealizedPnl(contractPosition.getRealizedPnl() + (contractPosition.getAveragePrice() - trade.getAveragePrice()) * contractMultiplier * positionReduction);
				residueSignedPositionAmount += positionReduction;
				residueTradeAmount -= positionReduction;
			}
		}
		double newSignedPositionAmount = residueSignedPositionAmount;
		if (newSignedPositionAmount == 0) {
			contractPosition.setAveragePrice(0.0);
		}
		// building position
		if (residueTradeAmount > 0) {
			if (trade.getOrder().getSide() == OrderSide.BUY) { // building long position from 0 or more
				newSignedPositionAmount = residueSignedPositionAmount + residueTradeAmount;
				contractPosition.setAveragePrice((contractPosition.getAveragePrice() * residueSignedPositionAmount + trade.getAveragePrice() * residueTradeAmount) / newSignedPositionAmount);
			} else { // building long position from 0 or less
				newSignedPositionAmount = residueSignedPositionAmount - residueTradeAmount;
				contractPosition.setAveragePrice((contractPosition.getAveragePrice() * residueSignedPositionAmount - trade.getAveragePrice() * residueTradeAmount) / newSignedPositionAmount);
			}
		}
		contractPosition.setSignedAmount(newSignedPositionAmount);
		double cashChange = trade.getAveragePrice() * trade.getAmount() * contractMultiplier * (trade.getOrder().getSide() == OrderSide.BUY ? -1 : 1);;
		cashPosition.setSignedAmount(cashPosition.getSignedAmount() + cashChange);
		recalculateMarketValueAndUPnl();
	}

	@Override
	public void onPriceUpdate(IContract contract, ISeriesPoint<Date, Double> price) {
		updateMarketPrice(contract, price.getIndex(), price.getValue());
	}
	
	private void updateMarketPrice(IContract contract, Date timestamp, double price) {
		PositionBean position = positions.get(contract);
		if (position != null) {
			if (price != position.getMarketPrice()) {
				position.setMarketPrice(price);
				position.setTimestamp(timestamp);
				recalculateMarketValueAndUPnl();
			}
		}
	}

	private void recalculateMarketValueAndUPnl() {
		for (Map.Entry<IContract, PositionBean> entry : positions.entrySet()) {
			Integer contractMultiplier = entry.getKey().getMultiplier();
			if (contractMultiplier == null || contractMultiplier == 0) {
				contractMultiplier = 1;
			}
			PositionBean position = entry.getValue();
			position.setMarketValue(position.getMarketPrice() * position.getSignedAmount() * contractMultiplier);
			position.setUnrealizedPnl(position.getMarketValue() - position.getAveragePrice() * position.getSignedAmount() * contractMultiplier);
		}
	}
}
