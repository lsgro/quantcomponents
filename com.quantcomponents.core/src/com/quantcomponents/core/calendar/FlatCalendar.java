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
 * Trivial trading calendar, with a continuous trading period every day, weekend included.
 */
public class FlatCalendar implements ITradingCalendar {
	public static final String NAME = "Flat Trading Calendar";
	public static final String DESC = "Always open for trading";
	public static final Date START_DATE = new Date(0L);
	public static final Date END_DATE = new Date(Long.MAX_VALUE);
	public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT");
	private static final ITradingPeriod FLAT_TRADING_PERIOD = new ITradingPeriod() {
		@Override
		public int getStartHour() {
			return 0;
		}
		@Override
		public int getStartMinute() {
			return 0;
		}
		@Override
		public int getEndHour() {
			return 24;
		}
		@Override
		public int getEndMinute() {
			return 0;
		}
		@Override
		public boolean contains(int hour, int minute) {
			return true;
		}};
	private static final ITradingPeriod[] FLAT_TRADING_PERIOD_ARRAY = new ITradingPeriod[] { FLAT_TRADING_PERIOD };
	private static final ITradingDay FLAT_TRADING_DAY = new ITradingDay() {
		@Override
		public ITradingPeriod[] getTradingPeriods() {
			return FLAT_TRADING_PERIOD_ARRAY;
		}};

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESC;
	}
	
	@Override
	public Date getStartDate() {
		return START_DATE;
	}

	@Override
	public Date getEndDate() {
		return END_DATE;
	}

	@Override
	public TimeZone getTimeZone() {
		return TIME_ZONE;
	}

	@Override
	public ITradingDay tradingDay(Date date) {
		return FLAT_TRADING_DAY;
	}

	@Override
	public Boolean isIncludeAfterHours() {
		return null;
	}

	@Override
	public String[] getExchanges() {
		return null;
	}
}
