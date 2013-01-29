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
import com.quantcomponents.chart.IMark;

/**
 * Axis marks for double axis
 */
public class DoubleMark implements IMark<Double> {
	private IMarkScale<Double> markScale;
	private double value;

	public DoubleMark(IMarkScale<Double> scale, double value) {
		this.markScale = scale;
		this.value = value;
	}

	@Override
	public IMarkScale<Double> getScale() {
		return markScale;
	}

	@Override
	public Double getValue() {
		return value;
	}

}
