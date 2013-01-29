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
import java.util.TimeZone;

import com.quantcomponents.core.model.BareDate;

/**
 * Configurable implementation of {@link AbstractPeriodicTradingCalendar}
 */
public class PeriodicTradingCalendar extends AbstractPeriodicTradingCalendar implements ITradingCalendar {
	private final String name;
	private final String description;
	private final TimeZone timeZone;
	private final String[] exchanges;
	private final Boolean includeAfterHours;
	private final Set<BareDate> bankHolidays; 
	private final Map<String, ITradingDay> specialTradingDays;
	private final Map<Integer, ITradingDay> weekTradingDays;
	private final Calendar calendar = Calendar.getInstance();
	private final Date startDate;
	private final Date endDate;
	
	public PeriodicTradingCalendar(String name, String description, TimeZone timeZone, String[] exchanges, Boolean includeAfterHours, Set<BareDate> bankHolidays, Map<Integer, ITradingDay> weekTradingDays, Map<String, ITradingDay> specialTradingDays, Date startDate, Date endDate) {
		this.name = name;
		this.description = description;
		this.timeZone = timeZone;
		this.exchanges = exchanges;
		this.includeAfterHours = includeAfterHours;
		this.bankHolidays = bankHolidays;
		this.weekTradingDays = weekTradingDays;
		this.specialTradingDays = specialTradingDays;
		this.startDate = startDate;
		this.endDate = endDate;
		calendar.setTimeZone(timeZone);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}

	@Override
	public Boolean isIncludeAfterHours() {
		return includeAfterHours;
	}

	@Override
	public String[] getExchanges() {
		return exchanges;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	protected Set<BareDate> getClosingDays() {
		return bankHolidays;
	}

	@Override
	protected Map<Integer, ITradingDay> getWeekTradingDays() {
		return weekTradingDays;
	}

	@Override
	protected Map<String, ? extends ITradingDay> getSpecialTradingDays() {
		return specialTradingDays;
	}
}
