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

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.quantcomponents.chart.BaseAxis;
import com.quantcomponents.chart.IAxis;
import com.quantcomponents.chart.IMark;
import com.quantcomponents.chart.IMarkScale;
import com.quantcomponents.chart.IMarkScaleSelector;
import com.quantcomponents.chart.series.scale.DayScale;
import com.quantcomponents.core.calendar.CalendarTradingSchedule;
import com.quantcomponents.core.calendar.ITradingCalendar;
import com.quantcomponents.core.calendar.ITradingDay;
import com.quantcomponents.core.calendar.ITradingSchedule;

/**
 * 
 * Chart axis for time values
 */
public class TimeAxis extends BaseAxis<Date> implements IAxis<Date> {
	private static final long MAX_SPAN_TO_EVALUATE_TRADING_PERIODS = 7L * 24 * 60 * 60 * 1000;
	private static final long DAY_DURATION = 24L * 60 * 60 * 1000;
	private final DayScale dayScale;
	private final ITradingCalendar tradingCalendar;
	private final ITradingSchedule tradingSchedule;
	private final int padding;
	private final IMarkScale<Date> baseMarkScale;
	private final List<IMark<Date>> baseMarks;
	private final List<IMark<Date>> parentMarks;
	private final int pointWidth;

	public TimeAxis(ITradingCalendar tradingCalendar, IMarkScaleSelector<Date> scaleSelector, Date rangeLow, Date rangeHigh, int pixelLow, int pixelHigh, long pointInterval, int maxMarkNumber) {
		this.tradingCalendar = tradingCalendar;
		tradingSchedule = new CalendarTradingSchedule(tradingCalendar);
		dayScale = new DayScale(tradingCalendar.getTimeZone());
		setRangeLow(rangeLow);
		setRangeHigh(rangeHigh);
		setPixelLow(pixelLow);
		setPixelHigh(pixelHigh);
		long realValueSpan = getRangeHigh().getTime() - getRangeLow().getTime();
		double effectiveValueSpan;
		if (realValueSpan < MAX_SPAN_TO_EVALUATE_TRADING_PERIODS) { // if span is below 1 week we must take into consideration trading hours
			effectiveValueSpan = tradingSchedule.intervalBeetwen(getRangeLow(), getRangeHigh());
		} else {
			effectiveValueSpan = realValueSpan;
		}
		baseMarkScale = scaleSelector.markScale(effectiveValueSpan, maxMarkNumber);
		IMarkScale<Date> parentMarkScale = baseMarkScale.parent();
		baseMarks = new LinkedList<IMark<Date>>();
		for (IMark<Date> mark = baseMarkScale.followingMark(getRangeLow()); mark != null && mark.getValue().compareTo(getRangeHigh()) < 0; mark = baseMarkScale.followingMark(mark.getValue())) {
			Date date = mark.getValue();
			Date adjustedDate = tradingSchedule.firstTradingTime(date);
			if (isValid(adjustedDate)) {
				baseMarks.add(new TimeMark(mark.getScale(), adjustedDate));
			}
		}
		parentMarks = new LinkedList<IMark<Date>>();
		for (IMark<Date> mark = parentMarkScale.followingMark(getRangeLow()); mark != null && mark.getValue().compareTo(getRangeHigh()) < 0; mark = parentMarkScale.followingMark(mark.getValue())) {
			Date date = mark.getValue();
			Date adjustedDate = tradingSchedule.firstTradingTime(date);
			if (isValid(adjustedDate)) {
				parentMarks.add(new TimeMark(mark.getScale(), adjustedDate));
			}
		}
		long effectiveSpan;
		if (pointInterval < DAY_DURATION) {
			effectiveSpan = tradingSchedule.intervalBeetwen(getRangeLow(), getRangeHigh());
		} else {
			effectiveSpan = getRangeHigh().getTime() - getRangeLow().getTime();
		}
		int numberOfPoints = (int) Math.max((effectiveSpan / pointInterval), 1);
		pointWidth = (getPixelHigh() - getPixelLow()) / numberOfPoints;
		padding = pointWidth / 2 + 1;
	}

	@Override
	public int calculatePixel(Date value) {
		double displacement = tradingSchedule.intervalBeetwen(getRangeLow(), value) / (double) tradingSchedule.intervalBeetwen(getRangeLow(), getRangeHigh());
		return getPixelLow() + padding + (int) (displacement * (getPixelHigh() - getPixelLow() - padding * 2));
	}

	@Override
	public boolean isValid(Date abscissa) {
		ITradingDay tradingDay = tradingCalendar.tradingDay(abscissa);
		if (tradingDay.getTradingPeriods().length == 0) {
			return false;
		}
		if (baseMarkScale.compareTo(dayScale) >= 0) {
			return true;
		}
		return tradingSchedule.isTradingTime(abscissa);
	}

	@Override
	public List<IMark<Date>> baseMarks() {
		return Collections.unmodifiableList(baseMarks);
	}

	@Override
	public List<IMark<Date>> parentMarks() {
		return Collections.unmodifiableList(parentMarks);
	}

	@Override
	public IMarkScale<Date> baseMarkScale() {
		return baseMarkScale;
	}

	@Override
	public int getPointSize() {
		return pointWidth;
	}
}
