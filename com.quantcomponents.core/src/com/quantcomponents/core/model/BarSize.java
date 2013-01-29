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
 * Length of interval of historical data.
 *
 */
public enum BarSize {
	ONE_SEC(1000L),
	FIVE_SECS(5L * 1000L),
	TEN_SECS(10L * 1000L),
	FIFTEEN_SECS(15L * 1000L),
	THIRTY_SECS(30L * 1000L),
	ONE_MIN(60L * 1000L),
	TWO_MINS(2L * 60L * 1000L),
	THREE_MINS(3L * 60L * 1000L),
	FIVE_MINS(5L * 60L * 1000L),
	TEN_MINS(10L * 60L * 1000L),
	FIFTEEN_MINS(15L * 60L * 1000L),
	TWENTY_MINS(20L * 60L * 1000L),
	THIRTY_MINS(30L * 60L * 1000L),
	ONE_HOUR(60L * 60L * 1000L),
	FOUR_HOURS(4L * 60L * 60L * 1000L),
	ONE_DAY(24L * 60L * 60L * 1000L);
	
	private long durationInMs;
	
	private BarSize(long durationInMs) {
		this.durationInMs = durationInMs;
	}
	
	/**
	 * Interval duration in ms.
	 * @return A long corresponding to the duration in millisecond of this interval
	 */
	public long getDurationInMs() {
		return durationInMs;
	}
	
	private void subtractRemainder(Calendar cal, int calendarConstant, int remainder) {
		cal.add(calendarConstant, -(cal.get(calendarConstant) % remainder));
	}
	
	/**
	 * Rounding routine to adjust a specific date-time to the beginning of a period,
	 * assuming that periods start with zero offset (e.g. FIVE_SECS periods start at
	 * 0 seconds, 5 seconds, and so on).
	 * @param cal Input-output parameter
	 */
	@SuppressWarnings("incomplete-switch")
	public void adjustCalendarToBarBeginning(Calendar cal) {
		cal.set(Calendar.MILLISECOND, 0);
		if (this.compareTo(ONE_MIN) < 0) {
			switch(this) {
			case FIVE_SECS:
				subtractRemainder(cal, Calendar.SECOND, 5);
				break;
			case TEN_SECS:
				subtractRemainder(cal, Calendar.SECOND, 10);
				break;
			case FIFTEEN_SECS:
				subtractRemainder(cal, Calendar.SECOND, 15);
				break;
			case THIRTY_SECS:
				subtractRemainder(cal, Calendar.SECOND, 30);
			}
		} else {
			cal.set(Calendar.SECOND, 0);
			if (this.compareTo(ONE_HOUR) < 0) {
				switch(this) {
				case TWO_MINS:
					subtractRemainder(cal, Calendar.MINUTE, 2);
					break;
				case THREE_MINS:
					subtractRemainder(cal, Calendar.MINUTE, 3);
					break;
				case FIVE_MINS:
					subtractRemainder(cal, Calendar.MINUTE, 5);
					break;
				case TEN_MINS:
					subtractRemainder(cal, Calendar.MINUTE, 10);
					break;
				case FIFTEEN_MINS:
					subtractRemainder(cal, Calendar.MINUTE, 15);
					break;
				case TWENTY_MINS:
					subtractRemainder(cal, Calendar.MINUTE, 20);
					break;
				case THIRTY_MINS:
					subtractRemainder(cal, Calendar.MINUTE, 30);
				}
			} else {
				cal.set(Calendar.MINUTE, 0);
				if (this.compareTo(ONE_DAY) < 0) {
					switch (this) {
					case FOUR_HOURS:
						subtractRemainder(cal, Calendar.HOUR_OF_DAY, 4);
					}
				} else {
					cal.set(Calendar.HOUR_OF_DAY, 0);
				}
			} 
		} 
	}
}
