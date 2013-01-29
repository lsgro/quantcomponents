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

import com.quantcomponents.chart.ICurrentValueProvider;
import com.quantcomponents.chart.IDrawable;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Interface for chart renderers of time series
 * Implementors of this interface generally do not listen for changes in the series.
 * For each change that must be reflected in the chart, the series must be set with the method provided.
 */
public interface ITimeSeriesRenderer extends IDrawable<Date, Double>, ICurrentValueProvider<Double> {
	/**
	 * Set a snapshot time series to be rendered.
	 * This method must be called each time the series changes.
	 */
	void setSeries(ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> series);
}
