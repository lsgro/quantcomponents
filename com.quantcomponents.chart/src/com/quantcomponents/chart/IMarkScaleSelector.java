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

/**
 * 
 * Calculates the scale of the marks (e.g. seconds, minutes, hours) based on the required
 * number of marks and the span along the corresponding axis
 * @param <T>
 */
public interface IMarkScaleSelector<T> {
	/**
	 * Calculate the mark scale according to the input values
	 * @param valueSpan the maximum span of the axis, in cash amoung, milliseconds, etc.
	 * @param maxMarkNumber the maximum number of marks accepted on the axis
	 * @return a suitable mark scale
	 */
	IMarkScale<T> markScale(double valueSpan, int maxMarkNumber);
}