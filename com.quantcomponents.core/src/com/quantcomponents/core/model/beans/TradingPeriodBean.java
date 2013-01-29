/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.model.beans;

import java.io.Serializable;

import com.quantcomponents.core.calendar.ITradingPeriod;

/**
 * 
 * Bean for {@link com.quantcomponents.core.calendar.ITradingPeriod}
 */
public class TradingPeriodBean implements ITradingPeriod, Serializable {
	private static final long serialVersionUID = -3189739983931136613L;
	private int startHour;
	private int startMinute;
	private int endHour;
	private int endMinute;
	
	public TradingPeriodBean() {}
	
	public TradingPeriodBean(int startHour, int startMinute, int endHour, int endMinute) {
		this.startHour = startHour;
		this.startMinute = startMinute;
		this.endHour = endHour;
		this.endMinute = endMinute;
	}

	@Override
	public int getStartHour() {
		return startHour;
	}

	@Override
	public int getStartMinute() {
		return startMinute;
	}

	@Override
	public int getEndHour() {
		return endHour;
	}

	@Override
	public int getEndMinute() {
		return endMinute;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public void setEndMinute(int endMinute) {
		this.endMinute = endMinute;
	}

	@Override
	public boolean contains(int hour, int minute) {
		return (hour > startHour || hour == startHour && minute >= startMinute) && (hour < endHour || hour == endHour && minute < endMinute);
	}
}
