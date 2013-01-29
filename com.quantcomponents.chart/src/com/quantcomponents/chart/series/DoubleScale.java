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

import com.quantcomponents.chart.IMarkScale;

/**
 * Mark scale for double axis
 */
public class DoubleScale implements IMarkScale<Double> {
	private static final double EPSILON = 1e-10;
	private final int magnitude;
	private final double step;
	
	public DoubleScale(int magnitude, double factor) {
		this.magnitude = magnitude;
		step = Math.pow(10.0, magnitude) * factor;
	}
	
	@Override
	public double getStepSize() {
		return step;
	}
	
	@Override
	public DoubleMark previousMark(Double value) {
		double pValue;
		double units = value / step;
		if (Math.abs(units - Math.round(units)) < EPSILON) {
			pValue = value;
		} else {
			pValue = Math.floor(units) * step;
		}
		return new DoubleMark(this, pValue);
	}

	@Override
	public DoubleMark followingMark(Double value) {
		return new DoubleMark(this, previousMark(value).getValue() + step);
	}

	@Override
	public DoubleMark nearestMark(Double value) {
		DoubleMark p = previousMark(value);
		if (value - p.getValue() <= step / 2) {
			return p;
		} else {
			return followingMark(value);
		}
	}

	@Override
	public int compareTo(IMarkScale<Double> o) {
		if (o instanceof DoubleScale) {
			DoubleScale tg = (DoubleScale) o;
			return Double.compare(step, tg.step);
		} else {
			throw new IllegalArgumentException("Can only compare to " + DoubleScale.class.getName() + " instances");
		}
	}

	@Override
	public IMarkScale<Double> parent() {
		return new DoubleScale(magnitude + 1, 1);
	}

}
