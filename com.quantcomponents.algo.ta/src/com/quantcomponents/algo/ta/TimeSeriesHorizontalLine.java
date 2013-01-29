/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.ta;

import java.util.Date;

import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.series.SimplePoint;

/**
 * Horizontal trend line
 */
public class TimeSeriesHorizontalLine implements ITrendLine<Date, Double> {
	private final Double value;
	private final ISeriesPoint<Date, Double>[] points;
	private final String label;

	public TimeSeriesHorizontalLine(Double value, String label) {
		this.value = value;
		this.points = new SimplePoint[0];
		this.label = label;
	}

	@Override
	public ISeriesPoint<Date, Double>[] getPoints() {
		return points;
	}

	@Override
	public Double getValue(Date abscissa) {
		return value;
	}

	@Override
	public boolean isSegment() {
		return false;
	}

	@Override
	public String getLabel() {
		return label;
	}

}
