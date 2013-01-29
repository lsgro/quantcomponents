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
import java.util.TimeZone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.quantcomponents.chart.ArrayScaleSelector;
import com.quantcomponents.chart.Chart;
import com.quantcomponents.chart.DataRange;
import com.quantcomponents.chart.IChartMetrics;
import com.quantcomponents.chart.IMarkScale;
import com.quantcomponents.chart.series.scale.DayScale;
import com.quantcomponents.chart.series.scale.FiveMinutesScale;
import com.quantcomponents.chart.series.scale.FourHoursScale;
import com.quantcomponents.chart.series.scale.HourScale;
import com.quantcomponents.chart.series.scale.MinuteScale;
import com.quantcomponents.chart.series.scale.MonthScale;
import com.quantcomponents.chart.series.scale.QuarterScale;
import com.quantcomponents.chart.series.scale.TenMinutesScale;
import com.quantcomponents.chart.series.scale.ThirtyMinutesScale;
import com.quantcomponents.chart.series.scale.TwoHoursScale;
import com.quantcomponents.chart.series.scale.TwoMinutesScale;
import com.quantcomponents.chart.series.scale.WeekScale;
import com.quantcomponents.chart.series.scale.YearScale;
import com.quantcomponents.core.calendar.FlatCalendar;
import com.quantcomponents.core.calendar.ITradingCalendar;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * SWT component displaying a time series chart
 */
public class TimeSeriesChart extends Composite {
	private static final long DEFAULT_POINT_INTERVAL = 5L * 60 * 60 * 1000;
	private static final int DEFAULT_MARGIN_LEFT = 10;
	private static final int DEFAULT_MARGIN_TOP = 10;
	private static final int DEFAULT_MARGIN_RIGHT = 50;
	private static final int DEFAULT_MARGIN_BOTTOM = 30;
	private static final int AVERAGE_Y_LABEL_CHAR_WIDTH = 12;
	private final Chart<Date, Double> chart;
	private final TimeRulerX timeRuler;
	private final DoubleRulerY priceRuler;
	private final DoubleCurrentValueY curValueMark;
	private ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> series;
	private ITimeSeriesRenderer timeSeriesRenderer;
	private DataRange<Date, Double> seriesRange;
	private ITradingCalendar tradingCalendar = new FlatCalendar();
	private int marginLeft = DEFAULT_MARGIN_LEFT;
	private int marginTop = DEFAULT_MARGIN_TOP;
	private int marginRight = DEFAULT_MARGIN_RIGHT;
	private int marginBottom = DEFAULT_MARGIN_BOTTOM;
	private long pointInterval = DEFAULT_POINT_INTERVAL;
	private boolean recalcNeeded;
	
	public TimeSeriesChart(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		chart = new Chart<Date, Double>(this, SWT.NONE) {
			@Override
			public void updateMetrics(IChartMetrics<Date, Double> metrics) {
				metrics.setDataRange(seriesRange);
			}};
		chart.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		timeRuler = new TimeRulerX();
		chart.getDrawables().add(timeRuler);
		priceRuler = new DoubleRulerY();
		chart.getDrawables().add(priceRuler);
		chart.getDrawables().add(null); // placeholder for the time-series renderer
		curValueMark = new DoubleCurrentValueY();
		chart.getDrawables().add(curValueMark);
	}

	public synchronized void setSeriesRenderer(ITimeSeriesRenderer timeSeriesRenderer) {
		this.timeSeriesRenderer = timeSeriesRenderer;
		curValueMark.setCurrentValueProvider(timeSeriesRenderer);
		chart.getDrawables().set(2, this.timeSeriesRenderer);
		recalcNeeded = true;
	}

	public synchronized void setTradingCalendar(ITradingCalendar tradingCalendar) {
		this.tradingCalendar = tradingCalendar;
		timeRuler.setTimeZone(tradingCalendar.getTimeZone());
		recalcNeeded = true;
	}
	
	public synchronized void setMargins(int marginLeft, int marginTop, int marginRight, int marginBottom) {
		this.marginLeft = marginLeft;
		this.marginTop = marginTop;
		this.marginRight = marginRight;
		this.marginBottom = marginBottom;
		recalcNeeded = true;
	}	
		
	public synchronized void setPointInterval(long pointInterval) {
		this.pointInterval = pointInterval;
		recalcNeeded = true;
	}

	public synchronized void setSeries(ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> series) {
		this.series = series;
		recalcNeeded = true;
	}
	
	public synchronized void refresh() {
		recalculateIfNeeded();
		chart.refresh();
	}
	
	public synchronized Control getControl() {
		return chart.getControl();
	}
	
	private void recalculateIfNeeded() {
		if (recalcNeeded) {
			if (series == null || series.isEmpty()) {
				chart.setMetrics(null);
			} else {
				TimeChartMetrics metrics = new TimeChartMetrics(tradingCalendar, new ArrayScaleSelector<Date>(buildTimeGranularities(tradingCalendar.getTimeZone())), new DoubleScaleSelector(new double[] { 1.0, 2.0, 5.0 }));
				seriesRange = new DataRange<Date, Double>(series.getFirst().getIndex(), series.getLast().getIndex(), series.getMinimum().getBottomValue(), series.getMaximum().getTopValue());
				metrics.setDataRange(seriesRange);
				int minMarginRight = (curValueMark.integerDigits(seriesRange.getHighY()) + curValueMark.fractionDigits(seriesRange.getLowY(), seriesRange.getHighY()) + 1) * AVERAGE_Y_LABEL_CHAR_WIDTH;
				marginRight = Math.max(marginRight, minMarginRight);
				metrics.setMargins(marginLeft, marginTop, marginRight, marginBottom);
				metrics.setPointInterval(pointInterval);
				chart.setMetrics(metrics);
				if (timeSeriesRenderer != null) {
					timeSeriesRenderer.setSeries(series);
				}
			}
			recalcNeeded = false;
		}
	}
	
	@SuppressWarnings("unchecked")
	private IMarkScale<Date>[] buildTimeGranularities(TimeZone timeZone) {
		IMarkScale<Date> MINUTE_G = new MinuteScale(timeZone);
		IMarkScale<Date> TWO_MINUTES_G = new TwoMinutesScale(timeZone);
		IMarkScale<Date> FIVE_MINUTES_G = new FiveMinutesScale(timeZone);
		IMarkScale<Date> TEN_MINUTES_G = new TenMinutesScale(timeZone);
		IMarkScale<Date> THIRTY_MINUTES_G = new ThirtyMinutesScale(timeZone);
		IMarkScale<Date> HOUR_G = new HourScale(timeZone);
		IMarkScale<Date> TWO_HOURS_G = new TwoHoursScale(timeZone);
		IMarkScale<Date> FOUR_HOURS_G = new FourHoursScale(timeZone);
		IMarkScale<Date> DAY_G = new DayScale(timeZone);
		IMarkScale<Date> WEEK_G = new WeekScale(timeZone);
		IMarkScale<Date> MONTH_G = new MonthScale(timeZone);
		IMarkScale<Date> QUARTER_G = new QuarterScale(timeZone);
		IMarkScale<Date> YEAR_G = new YearScale(timeZone);
		return new IMarkScale[] { DAY_G, FIVE_MINUTES_G, FOUR_HOURS_G, HOUR_G, MINUTE_G,
				MONTH_G, QUARTER_G, TEN_MINUTES_G, THIRTY_MINUTES_G, TWO_HOURS_G, TWO_MINUTES_G, WEEK_G, YEAR_G
		};
	}
}
