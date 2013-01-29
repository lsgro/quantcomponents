/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.model;

import java.util.Calendar;

/**
 * Unit of time
 */
public enum UnitOfTime {
	SECOND(Calendar.SECOND, 1, 1000L),DAY(Calendar.DATE, 1, 86400000L),WEEK(Calendar.WEEK_OF_YEAR, 1, 604800000L),MONTH(Calendar.MONTH, 1, 2592000000L),YEAR(Calendar.YEAR, 1, 31104000000L);

	private int calendarConstant;
	private int amount;
	private long durationInMs;

	private UnitOfTime(int calendarConstant, int amount, long durationInMs) {
		this.calendarConstant = calendarConstant;
		this.amount = amount;
		this.durationInMs = durationInMs;
	}

	/**
	 * @return the int constant corresponding to the {@link java.util.Calendar} unit of time, e.g. {@link java.util.Calendar#MONTH}
	 * @see java.util.Calendar
	 */
	public int getCalendarConstant() {
		return calendarConstant;
	}

	/**
	 * @return the number of units corresponding to this period
	 */
	public int getCalendarAmount() {
		return amount;
	}

	/**
	 * @return the duration of this period in milliseconds
	 */
	public long getDurationInMs() {
		return durationInMs;
	}
}
