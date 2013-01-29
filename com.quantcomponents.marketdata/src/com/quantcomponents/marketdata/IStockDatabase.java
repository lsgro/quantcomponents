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

import java.util.TimeZone;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IPersistentIdentifiable;

/**
 * High level object to collect historical and realtime data about a specific contract.
 * This is the preferred object to deal with stock time series into the framework.
 * It contains three different time series:
 * <ul>
 * <li>A OHLC time series, that can be updated with realtime and historical bars</li>
 * <li>A tick time series, that can be fed with historical and realtime ticks</li>
 * <li>A consolidated OHLC time series, that presents a unified view of the OHLC and tick
 * time series, reacting to each update in real time</li>
 * </ul>
 * Callers typically use the consolidated "virtual" OHLC series, since it conveys the
 * information in a convenient OHLC format, but it reacts to new data, allowing tick-based
 * algorithms.
 * 
 * @see IMarketDataManager
 *
 */
public interface IStockDatabase extends IStockDataCollection, IPersistentIdentifiable {
	/**
	 * Returns the mutable OHLC time series
	 */
	IMutableOHLCTimeSeries getOHLCTimeSeries();
	/**
	 * Returns the mutable tick time series
	 */
	IMutableTickTimeSeries getTickTimeSeries();
	/**
	 * Returns a consolidated OHLC time series, non mutable, that is updated in real time from
	 * the mutable series
	 */
	IOHLCTimeSeries getVirtualTimeSeries();
	long getTimestamp();
	TimeZone getTimeZone();
	BarSize getBarSize();
	DataType getDataType();
	boolean isIncludeAfterHours();
}
