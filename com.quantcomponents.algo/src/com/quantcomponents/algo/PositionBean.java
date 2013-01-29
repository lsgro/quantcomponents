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
 * 
 * Position bean
 */
public class PositionBean implements IPosition, Serializable {
	private static final long serialVersionUID = -3539675201897400237L;
	private Date timestamp;
	private double signedAmount;
	private double marketPrice;
	private double marketValue;
	private double averagePrice;
	private double unrealizedPnl;
	private double realizedPnl;
	
	public PositionBean() {}

	public PositionBean(Date timestamp, double signedAmount, double marketPrice, double marketValue, double averagePrice, double unrealizedPnl, double realizedPnl) {
		this.timestamp = timestamp;
		this.signedAmount = signedAmount;
		this.marketPrice = marketPrice;
		this.marketValue = marketValue;
		this.averagePrice = averagePrice;
		this.unrealizedPnl = unrealizedPnl;
		this.realizedPnl = realizedPnl;
	}
	
	public PositionBean(IPosition sourcePosition) {
		if (sourcePosition != null) {
			this.timestamp = sourcePosition.getTimestamp();
			this.signedAmount = sourcePosition.getSignedAmount();
			this.marketPrice = sourcePosition.getMarketPrice();
			this.marketValue = sourcePosition.getMarketValue();
			this.averagePrice = sourcePosition.getAveragePrice();
			this.unrealizedPnl = sourcePosition.getUnrealizedPnl();
			this.realizedPnl = sourcePosition.getRealizedPnl();
		}
	}

	public static PositionBean copyOf(IPosition p) {
		return new PositionBean(p.getTimestamp(), p.getSignedAmount(), p.getMarketPrice(), p.getMarketValue(), p.getAveragePrice(), p.getUnrealizedPnl(), p.getRealizedPnl());
	}
	
	@Override
	public double getSignedAmount() {
		return signedAmount;
	}

	public void setSignedAmount(double signedAmount) {
		this.signedAmount = signedAmount;
	}

	@Override
	public double getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(double marketPrice) {
		this.marketPrice = marketPrice;
	}

	@Override
	public double getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}

	@Override
	public double getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(double averagePrice) {
		this.averagePrice = averagePrice;
	}

	@Override
	public double getUnrealizedPnl() {
		return unrealizedPnl;
	}

	public void setUnrealizedPnl(double unrealizedPnl) {
		this.unrealizedPnl = unrealizedPnl;
	}

	@Override
	public double getRealizedPnl() {
		return realizedPnl;
	}

	public void setRealizedPnl(double realizedPnl) {
		this.realizedPnl = realizedPnl;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public String toString() {
		return stringRepr(this);
	}
	
	public static String stringRepr(IPosition position) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("pos [");
		buffer.append("#: ");
		buffer.append(position.getSignedAmount());
		buffer.append("; mkt prc: ");
		buffer.append(position.getMarketPrice());
		buffer.append("; avg prc: ");
		buffer.append(position.getAveragePrice());
		buffer.append("; mkt val: ");
		buffer.append(position.getMarketValue());
		buffer.append("; u pnl: ");
		buffer.append(position.getUnrealizedPnl());
		buffer.append("; r pnl: ");
		buffer.append(position.getRealizedPnl());
		buffer.append("]");
		return buffer.toString();
	}

}
