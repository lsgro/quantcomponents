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
import com.quantcomponents.chart.IMarkScaleSelector;

/**
 * 
 * Selects the correct mark scale for double axis
 */
public class DoubleScaleSelector implements IMarkScaleSelector<Double> {
	private final double[] factors;
	
	public DoubleScaleSelector(double[] factors) {
		this.factors = factors;
	}

	@Override
	public IMarkScale<Double> markScale(double valueSpan, int maxMarkNumber) {
		DoubleScale scale = null;
		outer:
		for (int power = -2; true; power++) {
			double magnitude = Math.pow(10, power);
			for (int i = 0; i < factors.length; i++) {
				double span = magnitude * factors[i];
				if (valueSpan / span <= maxMarkNumber) {
					scale = new DoubleScale(power, factors[i]);
					break outer;
				}
			}
		}
		return scale;
	}
}
