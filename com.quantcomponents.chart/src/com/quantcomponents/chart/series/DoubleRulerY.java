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
import com.quantcomponents.chart.IDrawable;
import com.quantcomponents.chart.IMark;

/**
 * Vertical ruler for double charts indexed by {@link java.util.Date}
 */
public class DoubleRulerY implements IDrawable<Date, Double> {
	private static final Color DEFAULT_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
	private static final Color DEFAULT_GRID_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	private static final int DEFAULT_GRID_STYLE = SWT.LINE_SOLID;
	private static final int DEFAULT_LABEL_PADDING = 5;
	private final NumberFormat nf = new DecimalFormat(); 
	private volatile Color color = DEFAULT_COLOR;
	private volatile Color gridColor = DEFAULT_GRID_COLOR;
	private volatile int gridStyle = DEFAULT_GRID_STYLE;
	private volatile int labelLeftPadding = DEFAULT_LABEL_PADDING;
	
	@Override
	public void draw(IChartMetrics<Date, Double> metrics, GC gc) {
		if (metrics == null) {
			return;
		}
		IAxis<Date> xAxis = metrics.xAxis();
		IAxis<Double> yAxis = metrics.yAxis();
		int yPixelLow = yAxis.getPixelLow();
		int yPixelHigh = yAxis.getPixelHigh();
		int xPixelLow = xAxis.getPixelLow();
		int xPixelHigh = xAxis.getPixelHigh();
		int halfHeight = gc.getFontMetrics().getHeight() / 2;
		nf.setMinimumFractionDigits((int) Math.ceil(-Math.log10(yAxis.baseMarkScale().getStepSize())));
		for (IMark<Double> mark : yAxis.baseMarks()) {
			gc.setForeground(color);
			double value = mark.getValue();
			int y = yAxis.calculatePixel(value);
			gc.drawString(nf.format(value), xPixelHigh + labelLeftPadding, y - halfHeight);
			gc.setForeground(gridColor);
			gc.setLineStyle(gridStyle);
			gc.drawLine(xPixelLow, y, xPixelHigh, y);
		}
		gc.drawLine(xPixelHigh, yPixelLow, xPixelHigh, yPixelHigh);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setGridColor(Color gridColor) {
		this.gridColor = gridColor;
	}

	public int getGridStyle() {
		return gridStyle;
	}

	public void setGridStyle(int gridStyle) {
		this.gridStyle = gridStyle;
	}

	public int getLabelLeftPadding() {
		return labelLeftPadding;
	}

	public void setLabelLeftPadding(int labelLeftPadding) {
		this.labelLeftPadding = labelLeftPadding;
	}
}
