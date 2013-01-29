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

import java.util.List;

import com.quantcomponents.core.model.ISeriesPoint;

/**
 * 
 * Pattern finder interface.
 * Implementors are used to extract meaninful patterns from a time series
 * @param <A> type of the chart abscissa
 * @param <O> type of the chart ordinate
 */
public interface IPatternFinder<A extends Comparable<A>, O extends Comparable<O>> {
	/**
	 * Find the patterns in the time series from scratch
	 * @return a list of patterns
	 */
	List<IPattern<A, O>> findPatterns();
	/**
	 * Update the list of patterns based on the new point added.
	 * This method allows optimisations in the pattern search algorithm
	 * @param addedPoint the only point being added since the last pattern generation
	 * @return a list of pattern
	 */
	List<IPattern<A, O>> findPatterns(ISeriesPoint<A, O> addedPoint);
	/**
	 * Update the list of patterns based on a modified point.
	 * This method allows optimisations in the pattern search algorithm
	 * @param oldPoint the old point being modified since the last pattern generation
	 * @param updatedPoint the only point been modified since the last pattern generation
	 * @return a list of pattern
	 */
	List<IPattern<A, O>> findPatterns(ISeriesPoint<A, O> oldPoint, ISeriesPoint<A, O> updatedPoint);
}
