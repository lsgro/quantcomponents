/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.calendar;

import java.util.Date;
import java.util.TimeZone;

/**
 * Trading calendar entity.
 * It maps any date with an instance of {@link ITradingDay}, 
 * and provides period of validity, time zone, and a
 * readable name and description.
 * Implementors of this interface are opaque, i.e. they don't
 * expose the rules used to calculate the trading hours
 * for each date.
 * @see AbstractPeriodicTradingCalendar for a transparent implementation of this interface
 *
 */
public interface ITradingCalendar {
	/**
	 * @return a readable compact name for the calendar (e.g. NASDAQ_AH)
	 */
	String getName();
	/**
	 * @return a longer description of the calendar
	 */
	String getDescription();
	/**
	 * @return true if this calendar includes after hours trading
	 */
	Boolean isIncludeAfterHours();
	/**
	 * @return the exchanges where it can be applied
	 */
	String[] getExchanges();
	/**
	 * @return the time zone of the calendar
	 */
	TimeZone getTimeZone();
	/**
	 * @return the first applicable date
	 */
	Date getStartDate();
	/**
	 * @return the first date when this calendar is expired
	 */
	Date getEndDate();
	/**
	 * This method returns an instance of {@link ITradingDay} for each date.
	 * @param date a {@link java.util.Date} object
	 * @return an object representing the trading hours of the input date
	 */
	ITradingDay tradingDay(Date date);
}
