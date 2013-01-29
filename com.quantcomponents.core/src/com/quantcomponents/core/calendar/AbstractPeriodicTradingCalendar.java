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

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.quantcomponents.core.model.BareDate;

/**
 * Base class for periodic trading calendars.
 * Implementors must specify a set of:
 * <ul>
 * <li>closing days</li>
 * <li>special opening days</li>
 * <li>normal week days</li>
 * </li>
 * that will be used to calculate the trading periods
 * for any date.
 */
public abstract class AbstractPeriodicTradingCalendar implements ITradingCalendar {
	private static final ITradingDay BANK_HOLIDAY = new BankHoliday();
	private final ThreadLocal<Calendar> calendars = new ThreadLocal<Calendar>() {
		@Override
		protected Calendar initialValue() {
			return Calendar.getInstance();
		}
	};
	
	@Override
	public ITradingDay tradingDay(Date date) {
		if (date.before(getStartDate()) || !date.before(getEndDate())) {
			throw new IllegalArgumentException();
		}
		Calendar calendar = calendars.get();
		calendar.setTimeZone(getTimeZone());
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		BareDate bareDate = new BareDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
		ITradingDay specialDay = getSpecialTradingDays().get(bareDate.getDateRepr());
		if (specialDay != null) {
			return specialDay;
		}
		if (getClosingDays().contains(bareDate)) {
			return BANK_HOLIDAY;
		}
		ITradingDay tradingDay = getWeekTradingDays().get(calendar.get(Calendar.DAY_OF_WEEK));
		if (tradingDay == null) {
			tradingDay = BANK_HOLIDAY;
		}
		return tradingDay;
	}

	protected abstract Set<BareDate> getClosingDays();
	
	protected abstract Map<String, ? extends ITradingDay> getSpecialTradingDays();
	
	protected abstract Map<Integer, ? extends ITradingDay> getWeekTradingDays();
}
