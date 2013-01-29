/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.model;


/**
 * This is the ancestor of all data-points that can be carried by {@link ISeries}
 *
 * @param <A> The Abscissa type
 * @param <O> The Ordinate type
 */
public interface ISeriesPoint<A extends Comparable<A>, O extends Comparable<O>> {
	/**
	 * The start index of this data-point.
	 * In case the index is a discrete point, and not an interval, the same value as
	 * getIndex() is returned
	 * @return an abscissa
	 */
	A getStartIndex();
	/**
	 * The end index of this data-point.
	 * In case the index is a discrete point, and not an interval, the same value as
	 * getIndex() is returned
	 * @return an abscissa
	 */
	A getEndIndex();
	/**
	 * The index of this data-point, or the most "important" (e.g. the start of the period)
	 * index in case the point spans over an interval
	 * @return an abscissa
	 */
	A getIndex();
	/**
	 * The minimum value of this data-point during the interval.
	 * @return an ordinate
	 */
	O getBottomValue();
	/**
	 * The maximum value of this  data-point during the interval.
	 * @return an ordinate
	 */
	O getTopValue();
	/**
	 * The value of the data-point.
	 * In case of interval with several values, this is the most "important" value, e.g. the close value
	 * @return an ordinate
	 */
	O getValue();
}
