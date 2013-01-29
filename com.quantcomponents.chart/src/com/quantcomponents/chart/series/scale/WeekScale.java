/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.chart.series.scale;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.quantcomponents.chart.IMark;
import com.quantcomponents.chart.series.TimeScale;
import com.quantcomponents.chart.series.TimeMark;

/**
 * Generates one week marks on a time axis
 */
public class WeekScale extends TimeScale {

	public WeekScale(TimeZone timeZone) {
		super(Calendar.WEEK_OF_YEAR, 1, timeZone, 7L * 24L * 60L * 60L * 1000L);
	}

	@Override
	public IMark<Date> previousMark(Date date) {
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		int rem = (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7;
		if (rem > 0) {
			cal.add(Calendar.DATE, -rem);
		}
		return new TimeMark(this, cal.getTime());
	}

	@Override
	protected TimeScale buildParent(TimeZone timeZone) {
		return new MonthScale(timeZone);
	}

}
