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
 * Generic trend line based on two points and a label
 */
public class TimeSeriesTrendLine implements ITrendLine<Date, Double> {
	private final Date bottomIndex;
	private final Date topIndex;
	private final Double bottomIndexValue;
	private final Double topIndexValue;
	private final boolean segment;
	private final ISeriesPoint<Date, Double>[] points;
	private final String label;

	public TimeSeriesTrendLine(Date bottomIndex, Date topIndex, Double bottomIndexValue, Double topIndexValue, boolean segment, String label) {
		this.bottomIndex = bottomIndex;
		this.topIndex = topIndex;
		this.bottomIndexValue = bottomIndexValue;
		this.topIndexValue = topIndexValue;
		this.segment = segment;
		this.points = new SimplePoint[] { new SimplePoint(bottomIndex, bottomIndexValue), new SimplePoint(topIndex, topIndexValue) };
		this.label = label;
	}

	@Override
	public Double getValue(Date index) {
		if (isSegment() && (index.before(bottomIndex) || index.after(topIndex))) {
			return null;
		} else {
			return (index.getTime() - bottomIndex.getTime()) * (topIndexValue - bottomIndexValue) / (topIndex.getTime() - bottomIndex.getTime()) + bottomIndexValue;
		}
	}

	@Override
	public boolean isSegment() {
		return segment;
	}

	@Override
	public ISeriesPoint<Date, Double>[] getPoints() {
		return points;
	}

	@Override
	public String getLabel() {
		return label;
	}

}
