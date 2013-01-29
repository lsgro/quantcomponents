/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata;

import java.util.Date;

import com.quantcomponents.core.model.IMutableSeries;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * A mutable series based on {@link java.util.Date} and {@link java.lang.Double}
 * @param <P> The type of the series data-point
 */
public interface IMutableTimeSeries<P extends ISeriesPoint<Date, Double>> extends ITimeSeries<P>, IMutableSeries<Date, Double, P> {
}