/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.ta;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.model.OrderSide;
import com.quantcomponents.core.series.SimplePoint;

/**
 * Pattern to show a trade onto a chart
 */
public class TradePattern implements IPattern<Date, Double> {
	private final Date executionTime;
	private final OrderSide orderSide;
	private final double executionPrice;
	private final int amount;
	private final List<ITrendLine<Date, Double>> trendLines = Collections.emptyList();
	private final List<ISeriesPoint<Date, Double>> points;

	public TradePattern(Date executionTime, OrderSide orderSide, double executionPrice, int amount) {
		this.executionTime = executionTime;
		this.orderSide = orderSide;
		this.executionPrice = executionPrice;
		this.amount = amount;
		points = Collections.singletonList((ISeriesPoint<Date, Double>) new SimplePoint(executionTime, executionPrice));
	}

	@Override
	public Date getBeginIndex() {
		return executionTime;
	}

	@Override
	public Date getEndIndex() {
		return executionTime;
	}

	@Override
	public List<ISeriesPoint<Date, Double>> getPoints() {
		return points;
	}

	@Override
	public List<ITrendLine<Date, Double>> getTrendLines() {
		return trendLines;
	}

	public Date getExecutionTime() {
		return executionTime;
	}

	public OrderSide getOrderSide() {
		return orderSide;
	}

	public double getExecutionPrice() {
		return executionPrice;
	}

	public int getAmount() {
		return amount;
	}

}
