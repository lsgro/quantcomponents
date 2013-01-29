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
 * This interface groups the modification methods that can grow a series.
 * A method to shrink (empty) a series is provided by {@link IMutableSeries}
 * Typical implementors are double-linked queues.
 *
 * @param <A> The Abscissa type
 * @param <O> The Ordinate type
 * @param <P> The type of the data point
 */
public interface ISeriesAugmentable<A extends Comparable<A>, O extends Comparable<O>, P extends ISeriesPoint<A, O>> extends ISeries<A, O, P> {
	/**
	 * Add the data-point to the tail of the series
	 * @throws IllegalArgumentException when the position is equal or before the current tail
	 */
	void addLast(P point);
	/**
	 * Add the data-point to the head of the series
	 * @throws IllegalArgumentException when the position is equal of after the current head
	 */
	void addFirst(P item);
	/**
	 * Add the data-point to the tail of the series if no point exists at the specified position
	 * @throws IllegalArgumentException when the position is before the current tail
	 */
	void addLastIfNotExists(P item);
	/**
	 * Add the data-point to the head of the series if no point exists at the specified position
	 * @throws IllegalArgumentException when the position is after the current head
	 */
	void addFirstIfNotExists(P item);
	/**
	 * Insert a data-point in any position, seeking the target position from the tail towards the head
	 * The direction of the search is relevant since the series can be huge and the execution time
	 * of this method is likely to increase when the target position is not near the tail
	 * @throws IllegalArgumentException when the position is occupied, and the series enforces strict sequence
	 * @see ISeries#isEnforceStrictSequence
	 */
	void insertFromTail(P item);
	/**
	 * Update the the tail item.
	 * @throws IllegalArgumentException when no data-point is present at the target position
	 */
	void updateTail(P item);
}
