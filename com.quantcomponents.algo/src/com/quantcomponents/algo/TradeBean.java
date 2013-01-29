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
 * Trade bean
 */
public class TradeBean implements ITrade, Serializable {
	private static final long serialVersionUID = -2084156663404127910L;
	private final IOrder order;
	private final String exchange;
	private final Date executionTime;
	private final int amount;
	private final double executionPrice;
	private final double averagePrice;
	
	public static TradeBean copyOf(ITrade trade) {
		return new TradeBean(trade.getOrder() == null ? null : OrderBean.copyOf(trade.getOrder()), trade.getExchange(), trade.getExecutionTime(), trade.getAmount(), trade.getExecutionPrice(), trade.getAveragePrice());
	}
	
	public TradeBean(IOrder order, String exchange, Date executionTime, int amount, double executionPrice, double averagePrice) {
		this.order = order;
		this.exchange = exchange;
		this.executionTime = executionTime;
		this.amount = amount;
		this.executionPrice = executionPrice;
		this.averagePrice = averagePrice;
	}

	@Override
	public IOrder getOrder() {
		return order;
	}

	@Override
	public String getExchange() {
		return exchange;
	}

	@Override
	public Date getExecutionTime() {
		return executionTime;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public double getExecutionPrice() {
		return executionPrice;
	}

	@Override
	public double getAveragePrice() {
		return averagePrice;
	}
	
	@Override
	public String toString() {
		return stringRepr(this);
	}
	
	public static String stringRepr(ITrade trade) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("trade [");
		buffer.append(trade.getOrder());
		buffer.append("; time: ");
		buffer.append(trade.getExecutionTime());
		buffer.append("; amnt: ");
		buffer.append(trade.getAmount());
		buffer.append("; prc: ");
		buffer.append(trade.getExecutionPrice());
		buffer.append("; avg prc: ");
		buffer.append(trade.getAveragePrice());
		buffer.append("]");
		return buffer.toString();
	}
	
}