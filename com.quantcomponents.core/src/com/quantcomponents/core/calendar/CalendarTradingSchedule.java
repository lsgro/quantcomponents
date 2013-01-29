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

/**
 * Adapter from {@link ITradingCalendar} to {@link ITradingSchedule}
 */
public class CalendarTradingSchedule implements ITradingSchedule {
	private final ITradingCalendar tradingCalendar;
	private final ThreadLocal<Calendar> calendars = new ThreadLocal<Calendar>() {
		@Override
		protected Calendar initialValue() {
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(tradingCalendar.getTimeZone());
			return cal;
		}
	};
	private final ThreadLocal<Calendar> startCalendars = new ThreadLocal<Calendar>() {
		@Override
		protected Calendar initialValue() {
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(tradingCalendar.getTimeZone());
			return cal;
		}
	};
	private final ThreadLocal<Calendar> endCalendars = new ThreadLocal<Calendar>() {
		@Override
		protected Calendar initialValue() {
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(tradingCalendar.getTimeZone());
			return cal;
		}
	};

	public CalendarTradingSchedule(ITradingCalendar tradingCalendar) {
		this.tradingCalendar = tradingCalendar;
	}

	@Override
	public long intervalBeetwen(Date from, Date to) {
		long interval = 0L;
		Date firstDate = firstTradingTime(from);
		Date lastDate = lastTradingTime(to);
		Calendar cal = calendars.get();
		cal.setTime(firstDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		for (; !cal.getTime().after(lastDate); cal.add(Calendar.DATE,  1)) {
			Calendar calStart = startCalendars.get();
			calStart.setTime(cal.getTime());
			Calendar calEnd = endCalendars.get();
			calEnd.setTime(cal.getTime());
			ITradingDay tradingDay = tradingCalendar.tradingDay(cal.getTime());
			for (ITradingPeriod period : tradingDay.getTradingPeriods()) {
				calStart.set(Calendar.HOUR_OF_DAY, period.getStartHour());
				calStart.set(Calendar.MINUTE, period.getStartMinute());
				calEnd.set(Calendar.HOUR_OF_DAY, period.getEndHour());
				calEnd.set(Calendar.MINUTE, period.getEndMinute());
				if (calStart.getTime().before(lastDate) && calEnd.getTime().after(firstDate)) {
					long startTime = Math.max(calStart.getTime().getTime(), firstDate.getTime());
					long endTime = Math.min(calEnd.getTime().getTime(), lastDate.getTime());
					interval += endTime - startTime;
				}
			}
		}
		return interval;
	}

	@Override
	public Date firstTradingTime(Date from) {
		if (isTradingTime(from)) {
			return from;
		}
		Calendar cal = calendars.get();
		cal.setTime(from);
		// same day
		ITradingDay tradingDay = tradingCalendar.tradingDay(from);
		ITradingPeriod[] periods = tradingDay.getTradingPeriods();
		for (int i = 0; i < periods.length; i++) {
			ITradingPeriod period = periods[i];
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			// find start of first trading period after startCalendar
			if (period.getStartHour() > hour || period.getStartHour() == hour && period.getStartMinute() >= minute) {
				cal.set(Calendar.HOUR_OF_DAY, period.getStartHour());
				cal.set(Calendar.MINUTE, period.getStartMinute());
				return cal.getTime();
			}
		}
		// following days
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		while (!cal.after(tradingCalendar.getEndDate())) {
			cal.add(Calendar.DATE, 1);
			tradingDay = tradingCalendar.tradingDay(cal.getTime());
			periods = tradingDay.getTradingPeriods();
			if (periods.length > 0) {
				ITradingPeriod firstPeriod = periods[0];
				cal.set(Calendar.HOUR_OF_DAY, firstPeriod.getStartHour());
				cal.set(Calendar.MINUTE, firstPeriod.getStartMinute());
				return cal.getTime();
			}
		}
		
		return null;
	}

	@Override
	public Date lastTradingTime(Date to) {
		if (isTradingTime(to)) {
			return to;
		}
		Calendar cal = calendars.get();
		cal.setTime(to);
		// same day
		ITradingDay tradingDay = tradingCalendar.tradingDay(to);
		ITradingPeriod[] periods = tradingDay.getTradingPeriods();
		for (int i = periods.length - 1; i >= 0; i--) {
			ITradingPeriod period = periods[i];
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			// find end of last trading period before startCalendar
			if (period.getEndHour() < hour || period.getEndHour() == hour && period.getEndMinute() <= minute) {
				cal.set(Calendar.HOUR_OF_DAY, period.getEndHour());
				cal.set(Calendar.MINUTE, period.getEndMinute());
				return cal.getTime();
			}
		}
		// previous days
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		while (!cal.before(tradingCalendar.getStartDate())) {
			cal.add(Calendar.DATE, -1);
			tradingDay = tradingCalendar.tradingDay(cal.getTime());
			periods = tradingDay.getTradingPeriods();
			if (periods.length > 0) {
				ITradingPeriod lastPeriod = periods[periods.length - 1];
				cal.set(Calendar.HOUR_OF_DAY, lastPeriod.getEndHour());
				cal.set(Calendar.MINUTE, lastPeriod.getEndMinute());
				return cal.getTime();
			}
		}
		
		return null;
	}

	@Override
	public boolean isTradingTime(Date time) {
		ITradingDay tradingDay = tradingCalendar.tradingDay(time);
		Calendar cal = calendars.get();
		cal.setTime(time);
		for (ITradingPeriod period : tradingDay.getTradingPeriods()) {
			if (period.contains(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))) {
				return true;
			}
		}
		return false;
	}
}
