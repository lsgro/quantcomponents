/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.chart.series;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.quantcomponents.chart.IMarkScale;
import com.quantcomponents.chart.IMark;

/**
 * A mark scale for chart axis with {@link java.util.Date} values
 * This class is not thread-safe. In a multi-threading environment a new instance must should be created for each use.
 */
public abstract class TimeScale implements IMarkScale<Date> {
	private final int calendarUnit;
	private final int calendarAmount;
	private final long stepDuration;
	private TimeScale parent;
	protected final Calendar cal;
	
	public TimeScale(int calendarUnit, int calendarAmount, TimeZone timeZone, long stepDuration) {
		this.calendarUnit = calendarUnit;
		this.calendarAmount = calendarAmount;
		this.stepDuration = stepDuration;
		cal = Calendar.getInstance(timeZone);
		parent = buildParent(timeZone);
	}
	
	protected abstract TimeScale buildParent(TimeZone timeZone);

	public int getCalendarUnit() {
		return calendarUnit;
	}
	
	public int getCalendarAmount() {
		return calendarAmount;
	}

	@Override
	public double getStepSize() {
		return stepDuration;
	}

	@Override
	public int compareTo(IMarkScale<Date> o) {
		if (o instanceof TimeScale) {
			TimeScale tg = (TimeScale) o;
			if (calendarUnit == tg.calendarUnit) {
				return Integer.valueOf(calendarAmount).compareTo(tg.calendarAmount);
			} else {
				return - Integer.valueOf(calendarUnit).compareTo(tg.calendarUnit); // Calendar units are ordered inversely of magnitude
			}
		} else {
			throw new IllegalArgumentException("Can only compare to a " + TimeScale.class.getName() + " instance");
		}
	}

	@Override
	public IMark<Date> followingMark(Date date) {
		cal.setTime(previousMark(date).getValue());
		cal.add(getCalendarUnit(), getCalendarAmount());
		return new TimeMark(this, cal.getTime());
	}

	@Override
	public IMark<Date> nearestMark(Date date) {
		IMark<Date> p = previousMark(date);
		IMark<Date> f = followingMark(date);
		if (date.getTime() - p.getValue().getTime() <= f.getValue().getTime() - date.getTime()) {
			return p;
		} else {
			return f;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TimeScale) {
			TimeScale tg = (TimeScale) o;
			return calendarUnit == tg.calendarUnit && calendarAmount == tg.calendarAmount;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Integer.valueOf(calendarUnit + calendarAmount).hashCode();
	}
	
	@Override
	public IMarkScale<Date> parent() {
		return parent;
	}
}

