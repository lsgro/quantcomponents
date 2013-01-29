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

import java.util.List;

/**
 * Chart axis
 * @param <T> the type of the data-points along the axis dimension
 */
public interface IAxis<T> {
	/**
	 * The lowest point in the axis range, in terms of T values
	 */
	T getRangeLow();
	/**
	 * The highest point in the axis range, in terms of T values
	 */
	T getRangeHigh();
	/**
	 * The lowest point in the axis range, in terms of pixels
	 */
	int getPixelLow();
	/**
	 * The highest point in the axis range, in terms of pixels
	 */
	int getPixelHigh();
	/**
	 * Returns true if the value can be displayed according to this axis.
	 * A value can be within the axis boundaries, but it could be outside of trading hours, for example.
	 * @param value
	 */
	boolean isValid(T value);
	/**
	 * Returns the pixel value corresponding to the T value
	 */
	int calculatePixel(T value);
	/**
	 * Returns the main scale used for the axis marks
	 */
	IMarkScale<T> baseMarkScale();
	/**
	 * Returns all the base marks to be displayed on the axis
	 */
	List<IMark<T>> baseMarks();
	/**
	 * Returns all the lower granularity marks to be displayed on the axis
	 */
	List<IMark<T>> parentMarks();
	/**
	 * Returns the number of pixel in a discrete point, or zero if the axis represents continuous values
	 */
	int getPointSize();
}
