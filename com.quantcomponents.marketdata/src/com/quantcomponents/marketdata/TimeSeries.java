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
import java.util.TimeZone;

import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.series.LinkedListSeries;

/**
 * Specialized mutable time series containing double values, indexed by {@link java.util.Date}
 * @param <P> the type of the data-points
 */
public class TimeSeries<P extends ISeriesPoint<Date, Double>> extends LinkedListSeries<Date, Double, P> implements IMutableTimeSeries<P> {
	private static final long serialVersionUID = 2073320325650196527L;
	private final TimeZone timeZone;
	private final long interval;

	public TimeSeries(String ID, TimeZone timeZone, long interval, boolean enforceStrictSequence) {
		super(ID, enforceStrictSequence);
		this.timeZone = timeZone;
		this.interval = interval;
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}

	@Override
	public long getInterval() {
		if (interval != 0) {
			return interval;
		}
		if (isEmpty()) {
			return 1;
		}
		long averageInterval = (getLast().getIndex().getTime() - getFirst().getIndex().getTime()) / size();
		return averageInterval;
	}
	
	@Override
	public TimeSeries<P> createEmptyMutableSeries(String ID) {
		return new TimeSeries<P>(ID, getTimeZone(), getInterval(), isEnforceStrictSequence());
	}
}
