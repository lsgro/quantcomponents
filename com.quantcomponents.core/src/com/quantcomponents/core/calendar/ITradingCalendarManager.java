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

import java.util.List;

import com.quantcomponents.core.model.IContract;

/**
 * Service that provides trading calendars by name
 */
public interface ITradingCalendarManager {
	/**
	 * Retrieve trading calendar by name.
	 * @param name a calendar's name
	 * @return an {@link ITradingCalendar} instance with that name if it exists, null otherwise
	 */
	ITradingCalendar tradingCalendarByName(String name);
	/**
	 * Search of trading calendar by contract.
	 * @param contract a contract specification
	 * @param includeAfterHours true if the calendars must include after hours trading, false otherwise
	 * @return a list of calendars 
	 */
	List<ITradingCalendar> findTradingCalendars(IContract contract, Boolean includeAfterHours);
}
