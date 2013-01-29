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

/**
 * Provides a measure of time in terms of trading hours.
 * Implementors of this interface can be used to calculate the number
 * of trading time seconds between two any dates, and to find the
 * previous and following instant of trading time when the input
 * is not trading time.
 *
 */
public interface ITradingSchedule {
	/**
	 * @param from period start date
	 * @param to period end date
	 * @return the number of millisecond of trading time between the two input parameters
	 */
	long intervalBeetwen(Date from, Date to);
	/**
	 * @param from any instant
	 * @return the same instant, if it is trading time; otherwise the first trading time after that
	 */
	Date firstTradingTime(Date from);
	/**
	 * @param to any instant
	 * @return the same instant, if it is trading time; otherwise the last trading time before that
	 */
	Date lastTradingTime(Date to);
	/**
	 * @param time any instant
	 * @return true if the input is trading time, false otherwise
	 */
	boolean isTradingTime(Date time);
}
