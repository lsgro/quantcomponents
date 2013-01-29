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
import java.util.Date;

/**
 * Value type used to describe a period of time, in terms of time unit and time amount.
 */
public class TimePeriod {

	private final UnitOfTime unitOfTime;
	private final int amount;
	
	/**
	 * @param unitOfTime the unit in which this period is measured
	 * @param amount the amount of time units
	 */
	public TimePeriod(UnitOfTime unitOfTime, int amount) {
		super();
		this.unitOfTime = unitOfTime;
		this.amount = amount;
	}

	public UnitOfTime getUnitOfTime() {
		return unitOfTime;
	}

	public int getAmount() {
		return amount;
	}
	
	public String toString() {
		return amount + " " + unitOfTime.name();
	}
	
	/**
	 * Adds a time period to a specific date
	 * @param date original date
	 * @param period period to be added
	 * @return a {@link java.util.Date} result 
	 */
	public static Date addPeriodToDate(Date date, TimePeriod period) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		UnitOfTime unitOfTime = period.getUnitOfTime();
		cal.add(unitOfTime.getCalendarConstant(), period.getAmount() * unitOfTime.getCalendarAmount());
		return cal.getTime();
	}

	/**
	 * Subtracts a time period to a specific date
	 * @param date original date
	 * @param period period to be subtracted
	 * @return a {@link java.util.Date} result 
	 */
	public static Date subtractPeriodFromDate(Date date, TimePeriod period) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		UnitOfTime unitOfTime = period.getUnitOfTime();
		cal.add(unitOfTime.getCalendarConstant(), -period.getAmount() * unitOfTime.getCalendarAmount());
		return cal.getTime();
	}

	/**
	 * Utility method to find the best approximation of a time period expressed in milliseconds,
	 * in terms of TimePeriod
	 * @param periodInMs original time period expressed in milliseconds
	 * @return a TimePeriod instance that approximates the input parameter
	 */
	public static TimePeriod findApproxPeriod(long periodInMs) {
		TimePeriod timePeriod = null;
		for (int i = UnitOfTime.values().length - 1; i >= 0; i--) {
			UnitOfTime u = UnitOfTime.values()[i];
			long quot = periodInMs / u.getDurationInMs();
			if (quot > 0) {
				timePeriod = new TimePeriod(u, (int)quot);
				break;
			}
		}
		return timePeriod;
	}
}

