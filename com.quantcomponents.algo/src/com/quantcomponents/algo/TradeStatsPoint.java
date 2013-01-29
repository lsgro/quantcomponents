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

import java.io.Serializable;
import java.util.Date;


/**
 * Trade statistics series data-point
 */
public class TradeStatsPoint implements ITradeStatsPoint, Serializable {
	private static final long serialVersionUID = 436162090571203199L;
	private final ITrade trade;
	private final Date tradeStart;
	private Date tradeEnd;
	private double maxFavorableExcursion;
	private double maxAdverseExcursion;
	private double tradePnl;

	public TradeStatsPoint(ITrade trade, Date tradeStart) {
		this.trade = trade;
		this.tradeStart = tradeStart;
	}
	
	@Override
	public Date getTradeEnd() {
		return tradeEnd;
	}
	public void setTradeEnd(Date tradeEnd) {
		this.tradeEnd = tradeEnd;
	}
	@Override
	public double getMaxFavorableExcursion() {
		return maxFavorableExcursion;
	}
	public void setMaxFavorableExcursion(double maxFavorableExcursion) {
		this.maxFavorableExcursion = maxFavorableExcursion;
	}
	@Override
	public double getMaxAdverseExcursion() {
		return maxAdverseExcursion;
	}
	public void setMaxAdverseExcursion(double maxAdverseExcursion) {
		this.maxAdverseExcursion = maxAdverseExcursion;
	}
	@Override
	public double getTradePnl() {
		return tradePnl;
	}
	public void setTradePnl(double tradePnl) {
		this.tradePnl = tradePnl;
	}
	@Override
	public ITrade getTrade() {
		return trade;
	}
	@Override
	public Date getTradeStart() {
		return tradeStart;
	}
	@Override
	public Date getIndex() {
		return getTradeStart();
	}
	@Override
	public Double getBottomValue() {
		return getValue();
	}
	@Override
	public Double getTopValue() {
		return getValue();
	}
	@Override
	public Double getValue() {
		return getTradePnl();
	}

	@Override
	public Date getStartIndex() {
		return getTradeStart();
	}

	@Override
	public Date getEndIndex() {
		return getTradeEnd();
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(" stats [");
		buffer.append(trade.toString());
		buffer.append("; pnl: ");
		buffer.append(Double.toString(getTradePnl()));
		buffer.append("; mfe: ");
		buffer.append(Double.toString(getMaxFavorableExcursion()));
		buffer.append("; mae: ");
		buffer.append(Double.toString(getMaxAdverseExcursion()));
		buffer.append("; end: ");
		buffer.append(getTradeEnd());
		buffer.append("]");
		return buffer.toString();
	}
}