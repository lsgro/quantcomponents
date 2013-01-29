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

import com.quantcomponents.core.model.BarSize;

/**
 * OHLC bar interface.
 * The OHLC bar is a typical way of displaying in a single point the range of prices
 * for a time series during a given period.
 */
public interface IOHLC {
	/**
	 * Bar size
	 */
	BarSize getBarSize();
	/**
	 * Open value
	 */
	Double getOpen();
	/**
	 * Highest value during the time period
	 */
	Double getHigh();
	/**
	 * Lowest value during the time period
	 */
	Double getLow();
	/**
	 * Close value
	 */
	Double getClose();
	/**
	 * Total traded volume during the time period
	 */
	Long getVolume();
	/**
	 * Weighted average traded price during the time period
	 */
	Double getWAP();
	/**
	 * Number of trades during the time period
	 */
	Integer getCount();
	/**
	 * Timestamp of the last update
	 * This information is useful when the period has not expired yet, or when the bar has been stored while incomplete
	 */
	Date getLastUpdate();
}
