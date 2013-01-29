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
 * Scale of the marks to be displayed on an axis
 *
 * @param <T> type of the axis values
 */
public interface IMarkScale<T> extends Comparable<IMarkScale<T>> {
	/**
	 * Value of the steps in generalized scalar value (money amount, time milliseconds, etc) 
	 */
	double getStepSize();
	/**
	 * Calculates the nearest mark lower than the argument
	 */
	IMark<T> previousMark(T value);
	/**
	 * Calculates the nearest mark higher than the argument
	 */
	IMark<T> followingMark(T value);
	/**
	 * Calculates the mark nearest to the argument 
	 */
	IMark<T> nearestMark(T value);
	/**
	 * Marks must satisfy equality/hashCode contract since they are used as key in collections
	 */
	boolean equals(Object o);
	/**
	 * Marks must satisfy equality/hashCode contract since they are used as key in collections
	 */
	int hashCode();
	/**
	 * Returns the parent scale
	 * The scale with a lower granularity to be displayed when the original scale overloads: e.g. days vs hours, etc.
	 */
	IMarkScale<T> parent();
}
