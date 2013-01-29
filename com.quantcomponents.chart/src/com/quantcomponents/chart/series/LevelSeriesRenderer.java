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
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * This renderer paints a horizontal line for each level in the series, that continues until the next value in the series
 */
public class LevelSeriesRenderer implements ITimeSeriesRenderer {
	private static final Color DEFAULT_POINT_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
	private static final int DEFAULT_HEIGHT_PIXEL_DISPLACEMENT = 1;
	private volatile int heightPixelDisplacement = DEFAULT_HEIGHT_PIXEL_DISPLACEMENT;
	private volatile ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> timeSeries;
	private volatile ISeriesPoint<Date, Double> lastPointRendered;
	private volatile Color pointColor = DEFAULT_POINT_COLOR;
	private volatile Integer lastPointIndexPixel;
	private volatile Integer lastPointValuePixel;

	@Override
	public void draw(IChartMetrics<Date, Double> metrics, GC gc) {
		lastPointIndexPixel = null;
		lastPointValuePixel = null;
		ISeriesPoint<Date, Double> tmpLastPoint = null;
		if (timeSeries != null && !timeSeries.isEmpty()) {
			IAxis<Date> xAxis = metrics.xAxis();
			for (ISeriesPoint<Date, Double> point : timeSeries) {
				if (xAxis.isValid(point.getIndex())) {
					renderLevel(point.getIndex(), point.getValue(), metrics, gc);
					tmpLastPoint = point;
				}
			}
			lastPointRendered = tmpLastPoint;
		}
	}
	
	private void renderLevel(Date index, double value, IChartMetrics<Date, Double> metrics, GC gc) {
		IAxis<Date> xAxis = metrics.xAxis();
		IAxis<Double> yAxis = metrics.yAxis();
		int pixelIndex = xAxis.calculatePixel(index);
		int pixelValue = yAxis.calculatePixel(value);
		if (lastPointIndexPixel != null) {
			gc.setForeground(pointColor);
			gc.setBackground(pointColor);
			int pixelLeft = lastPointIndexPixel;
			int segmentWidth;
			if (pixelIndex == pixelLeft) {
				segmentWidth = 1;
			} else {
				segmentWidth = pixelIndex - pixelLeft;
			}
			gc.fillRectangle(pixelLeft, lastPointValuePixel - heightPixelDisplacement, segmentWidth, heightPixelDisplacement);
		}
		lastPointIndexPixel = pixelIndex;
		lastPointValuePixel = pixelValue;
	}

	@Override
	public void setSeries(ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> timeSeries) {
		this.timeSeries = timeSeries;
	}

	@Override
	public Double getCurrentValue() {
		if (lastPointRendered != null) { 
			return lastPointRendered.getValue();
		} else {
			return null;
		}
	}
	public Color getPointColor() {
		return pointColor;
	}

	public void setPointColor(Color pointColor) {
		this.pointColor = pointColor;
	}

	public int getHeightPixelDisplacement() {
		return heightPixelDisplacement;
	}

	public void setHeightPixelDisplacement(int heightPixelDisplacement) {
		this.heightPixelDisplacement = heightPixelDisplacement;
	}
}