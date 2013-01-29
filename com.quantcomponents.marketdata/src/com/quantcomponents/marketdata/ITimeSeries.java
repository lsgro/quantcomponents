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

import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Specific time series containing double values, indexed by {@link java.util.Date}
 * @param <P> The type of data-point
 */
public interface ITimeSeries<P extends ISeriesPoint<Date, Double>> extends ISeries<Date, Double, P> {
	/**
	 * Return the interval between two consecutive data-points, in milliseconds.
	 * This value is defined only for those time-series with a fixed period.
	 * Other time-series can return 1.
	 */
	long getInterval();
	/**
	 * The time zone to be used to display the data in this series
	 */
	TimeZone getTimeZone();
}
