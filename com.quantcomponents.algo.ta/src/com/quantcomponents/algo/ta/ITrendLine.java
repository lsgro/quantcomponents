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

import com.quantcomponents.core.model.ISeriesPoint;

/**
 * A line in the chart, either infinite or in form of segment
 *
 * @param <A> type of the chart abscissa
 * @param <O> type of the chart ordinate
 */
public interface ITrendLine<A extends Comparable<A>, O extends Comparable<O>> {
	/**
	 * Returns the label of the line, or null if no label
	 */
	String getLabel();
	/**
	 * Returns an array of points (typically two points) to be used to trace the line
	 */
	ISeriesPoint<A, O>[] getPoints();
	/**
	 * Returns an ordinate value for each abscissa, or null if the line is not defined for an abscissa
	 */
	O getValue(A abscissa);
	/**
	 * Return true if this is a segment, false if it is an infinite line
	 */
	boolean isSegment();
}
