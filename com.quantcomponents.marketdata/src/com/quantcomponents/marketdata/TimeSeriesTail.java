/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata;

import java.util.Date;
import java.util.Iterator;

import com.quantcomponents.core.calendar.ITradingSchedule;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesOperator;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Operator that extracts a snapshot subset of a time series, specified by a number of points from the tail of the series
 *
 * @param <P> the type of the data-points
 */
public class TimeSeriesTail<P extends ISeriesPoint<Date, Double>> implements ISeriesOperator<Date, Double, P> {
	private final ITradingSchedule tradingSchedule;
	private final int numPoints;
	
	public TimeSeriesTail(ITradingSchedule tradingSchedule, int numPoints) {
		this.tradingSchedule = tradingSchedule;
		this.numPoints = numPoints;
	}
	
	@Override
	public ISeries<Date, Double, P> transform(ISeries<Date, Double, P> series) {
		if (series instanceof ITimeSeries<?>) {
			ITimeSeries<P> timeSeries = (ITimeSeries<P>) series;
			TimeSeries<P> result = new TimeSeries<P>(timeSeries.getPersistentID(), timeSeries.getTimeZone(), timeSeries.getInterval(), timeSeries.isEnforceStrictSequence());
			Iterator<P> it = series.descendingIterator();
			int count = 0;
			while (it.hasNext() && count < numPoints) {
				P point = it.next();
				if (tradingSchedule.isTradingTime(point.getIndex())) {
					result.addFirst(point);
					count++;
				} 
			}
			return result;
		} else {
			throw new IllegalArgumentException("Only instances of " + ITimeSeries.class.getName() + " can be used");
		}
	}
}
