/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.chart;

import org.eclipse.swt.graphics.Rectangle;

/**
 * The metrics for a chart at a specific point in time.
 * Metrics objects are used to generate the chart axis, based on X/Y data range and drawing are dimensions.
 * The chart axis in turn are used to calculate the pixel values of every object in the chart.
 *
 * @param <A> type of the chart abscissa
 * @param <O> type of the chart ordinate
 */
public interface IChartMetrics<A, O> {
	/**
	 * Set the data range
	 */
	void setDataRange(IDataRange<A, O> dataRange);
	/**
	 * Set the drawing are dimensions
	 */
	void setDrawingArea(Rectangle drawingArea);
	/**
	 * Get the X axis
	 */
	IAxis<A> xAxis();
	/**
	 * Get the Y axis
	 */
	IAxis<O> yAxis();
}
