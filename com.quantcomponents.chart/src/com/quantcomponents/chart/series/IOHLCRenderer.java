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

import com.quantcomponents.chart.IDrawable;

/**
 * Interface for OHLC data points renderers
 */
public interface IOHLCRenderer extends IDrawable<Date, Double>{
	void setPointValues(Date index, double open, double high, double low, double close);
}
