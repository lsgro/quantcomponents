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

/**
 * 
 * Specification of an uninterrupted trading period, in terms of hour
 * and minute based in the relevant calendar's time zone
 *
 */
public interface ITradingPeriod {
	/**
	 * @return the starting hour of this trading period
	 */
	int getStartHour();
	/**
	 * @return the starting minute of this trading period
	 */
	int getStartMinute();
	/**
	 * @return the end hour of this trading period: i.e. the hour when the period ends
	 */
	int getEndHour();
	/**
	 * @return the end minute of this trading period: i.e. the first minute after the period ends
	 */
	int getEndMinute();
	/**
	 * Utility method to check if a specific hour-minute instant is included in the period
	 * @param hour an hour value
	 * @param minute a minute value
	 * @return true if the instant defined by the input values is included in this trading period, false otherwise
	 */
	boolean contains(int hour, int minute);
}
