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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.quantcomponents.chart.IAxis;
import com.quantcomponents.chart.IChartMetrics;
import com.quantcomponents.chart.ICurrentValueProvider;
import com.quantcomponents.chart.IDrawable;

/**
 * Renderer for the current value in a chart.
 * It display the current value of a data-series provided by a supplied {@link com.quantcomponents.chart.ICurrentValueProvider} beside the right edge of the chart
 */
public class DoubleCurrentValueY implements IDrawable<Date, Double> {
	private static final Color DEFAULT_FG_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	private static final Color DEFAULT_BG_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
	private static final int DEFAULT_LABEL_PADDING = 5;
	private final NumberFormat nf = new DecimalFormat();
	private volatile Color fgColor = DEFAULT_FG_COLOR;
	private volatile Color bgColor = DEFAULT_BG_COLOR;
	private volatile int labelLeftPadding = DEFAULT_LABEL_PADDING;
	private volatile ICurrentValueProvider<Double> currentValueProvider;
	private volatile Double value;

	public void setCurrentValueProvider(ICurrentValueProvider<Double> currentValueProvider) {
		this.currentValueProvider = currentValueProvider;
	}
	
	@Override
	public void draw(IChartMetrics<Date, Double> metrics, GC gc) {
		if (currentValueProvider != null) {
			value = currentValueProvider.getCurrentValue();
		}
		if (value != null) {
			IAxis<Date> xAxis = metrics.xAxis();
			IAxis<Double> yAxis = metrics.yAxis();
			int xPixelHigh = xAxis.getPixelHigh();
			int halfHeight = gc.getFontMetrics().getHeight() / 2;
			gc.setForeground(fgColor);
			gc.setBackground(bgColor);
			int y = yAxis.calculatePixel(value);
			int fractionDigits = fractionDigits(yAxis.getRangeLow(), yAxis.getRangeHigh());
			nf.setMinimumFractionDigits(fractionDigits);
			nf.setMaximumFractionDigits(fractionDigits);
			nf.setMinimumIntegerDigits(integerDigits(yAxis.getRangeHigh()));
			gc.drawString(nf.format(value), xPixelHigh + labelLeftPadding, y - halfHeight);
		}
	}

	public int fractionDigits(double rangeLow, double rangeHigh) {
		return 2 + (int) (Math.ceil(-Math.log10(rangeHigh - rangeLow)));
	}
	
	public int integerDigits(double rangeHigh) {
		return (int) Math.log10(rangeHigh);
	}
	
	public Color getFgColor() {
		return fgColor;
	}

	public void setFgColor(Color fgColor) {
		this.fgColor = fgColor;
	}

	public Color getBgColor() {
		return bgColor;
	}

	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getLabelLeftPadding() {
		return labelLeftPadding;
	}

	public void setLabelLeftPadding(int labelLeftPadding) {
		this.labelLeftPadding = labelLeftPadding;
	}
}
