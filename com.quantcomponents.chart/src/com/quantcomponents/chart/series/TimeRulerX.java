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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.quantcomponents.chart.HorizontalLabelContainer;
import com.quantcomponents.chart.IAxis;
import com.quantcomponents.chart.IChartMetrics;
import com.quantcomponents.chart.IDrawable;
import com.quantcomponents.chart.IMark;

/**
 * Horizontal chart ruler for double charts indexed by {@link java.util.Date}
 *
 */
public class TimeRulerX implements IDrawable<Date, Double> {
	private static final Color DEFAULT_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
	private static final Color DEFAULT_GRID_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	private static final Color DEFAULT_EMPH_GRID_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	private static final int DEFAULT_GRID_STYLE = SWT.LINE_SOLID;
	private static final int DEFAULT_PRIMARY_MARK_LENGTH = 5;
	private static final int DEFAULT_SECONDARY_MARK_LENGTH = 8;

	private final DateFormat hourMinuteFormat = new SimpleDateFormat("HH:mm");
	private final DateFormat dateFormat = new SimpleDateFormat("dd");
	private final DateFormat dayMonthFormat = new SimpleDateFormat("dd/MM");
	private final DateFormat monthYearFormat = new SimpleDateFormat("MMM yyyy");
	private final DateFormat yearFormat = new SimpleDateFormat("yyyy");
	
	private Color color = DEFAULT_COLOR;
	private Color gridColor = DEFAULT_GRID_COLOR;
	private Color emphGridColor = DEFAULT_EMPH_GRID_COLOR;
	private int gridStyle = DEFAULT_GRID_STYLE;
	private volatile int primaryMarkLength = DEFAULT_PRIMARY_MARK_LENGTH;
	private volatile int secondaryMarkLength = DEFAULT_SECONDARY_MARK_LENGTH;

	public void setTimeZone(TimeZone timeZone) {
		hourMinuteFormat.setTimeZone(timeZone);
		dateFormat.setTimeZone(timeZone);
		dayMonthFormat.setTimeZone(timeZone);
		monthYearFormat.setTimeZone(timeZone);
		yearFormat.setTimeZone(timeZone);
	}

	public void setPrimaryMarkLength(int primaryMarkLength) {
		this.primaryMarkLength = primaryMarkLength;
	}

	public void setSecondaryMarkLength(int secondaryMarkLength) {
		this.secondaryMarkLength = secondaryMarkLength;
	}

	@Override
	public void draw(IChartMetrics<Date, Double> metrics, GC gc) {
		IAxis<Date> xAxis = metrics.xAxis();
		int yPixelHigh = metrics.yAxis().getPixelHigh();
		int yPixelLow = metrics.yAxis().getPixelLow();
		int xPixelLow = xAxis.getPixelLow();
		int xPixelHigh = xAxis.getPixelHigh();
		int avgCharWidth = gc.getFontMetrics().getAverageCharWidth();
		HorizontalLabelContainer<Date> xLabelContainer = new HorizontalLabelContainer<Date>();
		addMarks(xLabelContainer, xAxis, xAxis.baseMarks(), avgCharWidth);
		addMarks(xLabelContainer, xAxis, xAxis.parentMarks(), avgCharWidth);
		for (HorizontalLabelContainer.LabelInfo<Date> labelInfo : xLabelContainer) {
			gc.setForeground(color);
			gc.drawString(labelInfo.label, labelInfo.leftPixel, yPixelHigh);
			int markLength;
			if (labelInfo.markScale.equals(xAxis.baseMarkScale())) {
				markLength = primaryMarkLength;
				gc.setForeground(gridColor);
			} else {
				markLength = secondaryMarkLength;
				gc.setForeground(emphGridColor);
			}
			gc.setLineStyle(gridStyle);
			gc.drawLine(labelInfo.centerPixel, yPixelHigh, labelInfo.centerPixel, yPixelLow);
			gc.setLineStyle(SWT.LINE_SOLID);
			gc.drawLine(labelInfo.centerPixel, yPixelHigh, labelInfo.centerPixel, yPixelHigh - markLength);
		}
		gc.setLineStyle(gridStyle);
		gc.drawLine(xPixelLow, yPixelHigh, xPixelHigh, yPixelHigh);
	}
	
	private void addMarks(HorizontalLabelContainer<Date> container, IAxis<Date> axis, List<IMark<Date>> markList, int avgCharWidth) {
		for (IMark<Date> mark : markList) {
			Date date = mark.getValue();
			String label = generateLabel(date, ((TimeScale) mark.getScale()).getCalendarUnit(), true);
			int x = axis.calculatePixel(date);
			int leftPixel = x - label.length() * avgCharWidth / 2;
			int rightPixel = leftPixel + label.length() * avgCharWidth;
			container.addLabel(date, label, leftPixel, x, rightPixel, mark.getScale());
		}
	}
	
	private String generateLabel(Date date, int uom, boolean parentScale) {
		String label;
		switch (uom) {
		case Calendar.MINUTE:
		case Calendar.HOUR_OF_DAY:
			label = hourMinuteFormat.format(date);
			break;
		case Calendar.DATE:
			if (parentScale) {
				label = dayMonthFormat.format(date);
			} else {
				label = dateFormat.format(date);
			}
			break;
		case Calendar.WEEK_OF_YEAR:
			label = dayMonthFormat.format(date);
			break;
		case Calendar.MONTH:
			label = monthYearFormat.format(date);
			break;
		case Calendar.YEAR:
			label = yearFormat.format(date);
			break;
		default:
			label = date.toString();
		}
		return label;
	}
}
