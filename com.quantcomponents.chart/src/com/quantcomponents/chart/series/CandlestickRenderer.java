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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.quantcomponents.chart.IAxis;
import com.quantcomponents.chart.IChartMetrics;
import com.quantcomponents.chart.IDrawable;

/**
 * 
 * Candlestick renderer for OHLC data points
 *
 */
public class CandlestickRenderer implements IDrawable<Date, Double>, IOHLCRenderer {
	private static final Color DEFAULT_COLOR_UNCHANGED = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
	private static final Color DEFAULT_COLOR_DOWN = Display.getDefault().getSystemColor(SWT.COLOR_RED);
	private static final Color DEFAULT_COLOR_UP = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
	private static final int MIN_THICKNESS = 3;
	private volatile Color colorUnchanged = DEFAULT_COLOR_UNCHANGED;
	private volatile Color colorDown = DEFAULT_COLOR_DOWN;
	private volatile Color colorUp = DEFAULT_COLOR_UP;
	private volatile Date index;
	private volatile double open;
	private volatile double high;
	private volatile double low;
	private volatile double close;

	@Override
	public void setPointValues(Date index, double open, double high, double low, double close) {
		this.index = index;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
	}
	
	@Override
	public void draw(IChartMetrics<Date, Double> metrics, GC gc) {
		IAxis<Date> xAxis = metrics.xAxis();
		IAxis<Double> yAxis = metrics.yAxis();
		int pixelIndex = xAxis.calculatePixel(index);
		int pixelOpen = yAxis.calculatePixel(open);
		int pixelHigh = yAxis.calculatePixel(high);
		int pixelLow = yAxis.calculatePixel(low);
		int pixelClose = yAxis.calculatePixel(close);
		int pixelBase = Math.min(pixelOpen, pixelClose);
		Color candleColor = null;
		if (open > close) {
			candleColor = colorDown;
		} else if (open < close) {
			candleColor = colorUp;
		} else {
			candleColor = colorUnchanged;
		}
		gc.setForeground(candleColor);
		gc.setBackground(candleColor);
		gc.drawLine(pixelIndex, pixelLow, pixelIndex, pixelHigh);
		int candleWidth = xAxis.getPointSize();
		int pixelLeft = pixelIndex - candleWidth / 2;
		int candleHeight = Math.abs(pixelOpen - pixelClose);
		if (candleHeight != 0) {
			gc.fillRectangle(pixelLeft, pixelBase, candleWidth, candleHeight);
		} else {
			gc.fillRectangle(pixelLeft, pixelBase - MIN_THICKNESS / 2, candleWidth, MIN_THICKNESS);
		}
	}

	public Color getColorUnchanged() {
		return colorUnchanged;
	}

	public void setColorUnchanged(Color colorUnchanged) {
		this.colorUnchanged = colorUnchanged;
	}

	public Color getColorDown() {
		return colorDown;
	}

	public void setColorDown(Color colorDown) {
		this.colorDown = colorDown;
	}

	public Color getColorUp() {
		return colorUp;
	}

	public void setColorUp(Color colorUp) {
		this.colorUp = colorUp;
	}

}
