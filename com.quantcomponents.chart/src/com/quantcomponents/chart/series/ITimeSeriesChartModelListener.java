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

import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * 
 * Listener to a {@link ITimeSeriesChartModel}, to be notified for each update in the data series
 * @param <P>
 */
public interface ITimeSeriesChartModelListener<P extends ISeriesPoint<Date, Double>> {
	/**
	 * Called whenever the data included in the model changes. It passes a full snapshot of the data.
	 */
	void onModelUpdated(ISeries<Date, Double, P> series);
}
