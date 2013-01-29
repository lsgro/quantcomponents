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

import java.util.Date;

import org.eclipse.swt.graphics.Rectangle;

import com.quantcomponents.chart.IAxis;
import com.quantcomponents.chart.IChartMetrics;
import com.quantcomponents.chart.IDataRange;
import com.quantcomponents.chart.IMarkScaleSelector;
import com.quantcomponents.core.calendar.ITradingCalendar;

/**
 * Implementation of {@link com.quantcomponents.chart.IChartMetrics} for double series indexed by {@link java.util.Date}
 *
 */
public class TimeChartMetrics implements IChartMetrics<Date, Double> {
	private static final int AVG_PIXEL_PER_LABEL_X = 50;
	private static final int AVG_PIXEL_PER_LABEL_Y = 40;
	private static final int DEFAULT_MAX_MARK_NUM_X = 10;
	private static final int DEFAULT_MAX_MARK_NUM_Y = 20;
	private static final long DEFAULT_POINT_INTERVAL = 5L * 60 * 1000;
	
	private final ITradingCalendar tradingCalendar;
	private final IMarkScaleSelector<Date> scaleSelectorX;
	private final IMarkScaleSelector<Double> scaleSelectorY;
	private TimeAxis xAxis;
	private DoubleAxis yAxis;
	private Rectangle drawingArea;
	private IDataRange<Date, Double> dataRange;
	private long pointInterval = DEFAULT_POINT_INTERVAL;
	private int maxMarkNumberX = DEFAULT_MAX_MARK_NUM_X;
	private int maxMarkNumberY = DEFAULT_MAX_MARK_NUM_Y;
	private int marginLeft;
	private int marginTop;
	private int marginRight;
	private int marginBottom;
	
	private boolean recalcNeeded;

	public TimeChartMetrics(ITradingCalendar tradingCalendar, IMarkScaleSelector<Date> scaleSelectorX, IMarkScaleSelector<Double> scaleSelectorY) {
		this.tradingCalendar = tradingCalendar;
		this.scaleSelectorX = scaleSelectorX;
		this.scaleSelectorY = scaleSelectorY;
	}
	
	public synchronized void setMargins(int marginLeft, int marginTop, int marginRight, int marginBottom) {
		this.marginLeft = marginLeft;
		this.marginTop = marginTop;
		this.marginRight = marginRight;
		this.marginBottom = marginBottom;
		recalcNeeded = true;
	}
	
	@Override
	public synchronized void setDataRange(IDataRange<Date, Double> dataRange) {
		this.dataRange = dataRange;
		recalcNeeded = true;
	}
	
	@Override
	public synchronized void setDrawingArea(Rectangle drawingArea) {
		this.drawingArea = drawingArea;
		recalcNeeded = true;
	}
	
	public synchronized void setPointInterval(long pointInterval) {
		this.pointInterval = pointInterval;
		recalcNeeded = true;
	}

	public synchronized void setMaxMarkNumberX(int maxMarkNumberX) {
		this.maxMarkNumberX = maxMarkNumberX;
		recalcNeeded = true;
	}

	public synchronized void setMaxMarkNumberY(int maxMarkNumberY) {
		this.maxMarkNumberY = maxMarkNumberY;
		recalcNeeded = true;
	}

	@Override
	public synchronized IAxis<Date> xAxis() {
		recalculateIfNeeded();
		return xAxis;
	}

	@Override
	public synchronized IAxis<Double> yAxis() {
		recalculateIfNeeded();
		return yAxis;
	}

	private void recalculateIfNeeded() {
		if (recalcNeeded) {
			if (dataRange != null && drawingArea != null) {
				maxMarkNumberX = (int) (drawingArea.width / AVG_PIXEL_PER_LABEL_X);
				xAxis = new TimeAxis(tradingCalendar, scaleSelectorX, dataRange.getLowX(), 
						dataRange.getHighX(), drawingArea.x + marginLeft, drawingArea.x + drawingArea.width - marginRight,
						pointInterval, maxMarkNumberX);
				maxMarkNumberY = (int) (drawingArea.height / AVG_PIXEL_PER_LABEL_Y);
				yAxis = new DoubleAxis(true, scaleSelectorY, dataRange.getLowY(), dataRange.getHighY(), drawingArea.y + marginTop, 
						drawingArea.y + drawingArea.height - marginBottom, maxMarkNumberY);
			}
			recalcNeeded = false;
		}
	}
}
