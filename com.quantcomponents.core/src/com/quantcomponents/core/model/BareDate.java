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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.quantcomponents.core.xml.XmlBareDateAdapter;

/**
 * Simpler date class used throughout the framework in place of {@link java.util.Date}
 * or {@link java.sql.Date}.
 * This class is used whenever there is a need for a bare date, with no time or timezone
 * information: e.g. contract expiration date.
 *
 */
@XmlJavaTypeAdapter(XmlBareDateAdapter.class)
public class BareDate implements Serializable, Comparable<BareDate> {
	private static final long serialVersionUID = 8285942560986575420L;
	private final String dateRepr;
	private NumberFormat twoDigits = new DecimalFormat("00");

	/**
	 * String constructor.
	 * @param dateRepr A date with format "yyyyMMdd"
	 */
	public BareDate(String dateRepr) {
		if (dateRepr.length() != 8) {
			throw new IllegalArgumentException("format is yyyymmdd");
		}
		this.dateRepr = dateRepr;
	}
	
	/**
	 * Year-month-date constructor.
	 * @param year The full year value (e.g. 1987)
	 * @param month1_12 The month value (January=1, December=12)
	 * @param day The day of the month (1..31)
	 */
	public BareDate(int year, int month1_12, int day) {
		if (year < 1000 || year > 9999) {
			throw new IllegalArgumentException("year must be between 1000 and 9999");
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(Integer.toString(year));
		buffer.append(twoDigits.format(month1_12));
		buffer.append(twoDigits.format(day));
		this.dateRepr = buffer.toString();
	}
	
	/**
	 * Factory method from {@link java.util.Date}
	 * @param date The original date
	 * @param timeZone The corresponding {@link java.util.TimeZone}
	 * @return A BareDate object
	 */
	public static BareDate fromDate(Date date, TimeZone timeZone) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(timeZone);
		cal.set(Calendar.MILLISECOND, 0);
		cal.setTime(date);
		return new BareDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
	}
	
	/**
	 * Conversion to {@link java.util.Date}
	 * @param bareDate The BareDate instance
	 * @param timeZone The {@link java.util.TimeZone} to be used in the conversion
	 * @return A {@link java.util.Date} object
	 */
	public static Date toDate(BareDate bareDate, TimeZone timeZone) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(timeZone);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(bareDate.getYear(), bareDate.getMonth(), bareDate.getDay(), 0, 0, 0);
		return cal.getTime();
	}
	
	/**
	 * Returns the internal representation of this class
	 * @return A date representation as String with format "yyyyMMdd"
	 */
	public String getDateRepr() {
		return dateRepr;
	}
	
	public int getYear() {
		return Integer.valueOf(dateRepr.substring(0, 4));
	}
	
	public int getMonth() {
		return Integer.valueOf(dateRepr.substring(4, 6)) - 1;
	}
	
	public int getDay() {
		return Integer.valueOf(dateRepr.substring(6, 8));
	}
	
	public Date getDate(TimeZone timeZone) {
		return toDate(this, timeZone);
	}
	
	@Override
	public String toString() {
		return getDateRepr();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BareDate) {
			return ((BareDate) o).dateRepr.equals(dateRepr);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return dateRepr.hashCode();
	}

	@Override
	public int compareTo(BareDate o) {
		return getDateRepr().compareTo(o.getDateRepr());
	}
}
