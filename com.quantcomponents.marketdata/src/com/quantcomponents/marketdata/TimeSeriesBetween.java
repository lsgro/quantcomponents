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

import com.quantcomponents.core.calendar.ITradingSchedule;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesOperator;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Operator that extracts a snapshot subset of a time series, specified by start and end indexes
 *
 * @param <P> the type of the data-points
 */
public class TimeSeriesBetween<P extends ISeriesPoint<Date, Double>> implements ISeriesOperator<Date, Double, P> {
	private final ITradingSchedule tradingSchedule;
	private final Date startDate;
	private final Date endDate;
	
	public TimeSeriesBetween(ITradingSchedule tradingSchedule, Date startDate, Date endDate) {
		this.tradingSchedule = tradingSchedule;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	@Override
	public ISeries<Date, Double, P> transform(ISeries<Date, Double, P> series) {
		if (series instanceof ITimeSeries<?>) {
			ITimeSeries<P> timeSeries = (ITimeSeries<P>) series;
			TimeSeries<P> result = new TimeSeries<P>(series.getPersistentID(), timeSeries.getTimeZone(), timeSeries.getInterval(), timeSeries.isEnforceStrictSequence());
			for (P point : series) {
				Date date = point.getIndex();
				if (date.before(startDate)) {
					continue;
				}
				if (!date.before(endDate)) {
					break;
				}
				if (tradingSchedule.isTradingTime(date)) {
					result.addLast(point);
				}
			}
			return result;
		} else {
			throw new IllegalArgumentException("Only instances of " + ITimeSeries.class.getName() + " can be used");
		}
	}
}
