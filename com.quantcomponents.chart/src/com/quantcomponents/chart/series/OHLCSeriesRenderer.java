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

import org.eclipse.swt.graphics.GC;

import com.quantcomponents.chart.IAxis;
import com.quantcomponents.chart.IChartMetrics;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.marketdata.IOHLC;

/**
 * 
 * Time series renderer that uses OHLC data point renderers (like candlestick renderers)
 */
public class OHLCSeriesRenderer implements ITimeSeriesRenderer {
	private final IOHLCRenderer pointRenderer;
	private volatile ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> timeSeries;
	private volatile IOHLC lastPointRendered;

	/**	
	 * Renderer constructor
	 * @param pointRenderer the OHLC data point renderer to be used for the data points of the series
	 */
	public OHLCSeriesRenderer(IOHLCRenderer pointRenderer) {
		this.pointRenderer = pointRenderer;
	}

	@Override
	public void draw(IChartMetrics<Date, Double> metrics, GC gc) {
		if (timeSeries != null && !timeSeries.isEmpty() && pointRenderer != null) {
			IAxis<Date> xAxis = metrics.xAxis();
			for (ISeriesPoint<Date, Double> point : timeSeries) {
				if (point instanceof IOHLC) {
					IOHLC ohlc = (IOHLC) point;
					if (xAxis.isValid(point.getIndex())) {
						pointRenderer.setPointValues(point.getIndex(), ohlc.getOpen(), ohlc.getHigh(), ohlc.getLow(), ohlc.getClose());
						pointRenderer.draw(metrics, gc);
						lastPointRendered = ohlc;
					}
				} else {
					throw new IllegalArgumentException("Only series of " + IOHLC.class.getName() + " points accepted");
				}
			}
		}
	}

	@Override
	public void setSeries(ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> timeSeries) {
		this.timeSeries = timeSeries;
	}

	@Override
	public Double getCurrentValue() {
		if (lastPointRendered != null) { 
			return lastPointRendered.getClose();
		} else {
			return null;
		}
	}
}