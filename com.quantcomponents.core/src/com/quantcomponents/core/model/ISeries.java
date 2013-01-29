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

import java.util.Iterator;

/**
 * Read-only time series, the main collection type in the framework.
 * Implementors must be thread-safe. Size and iterators must be snapshots.
 * Listeners are notified on a best-effort basis.
 * Depending on the value returned by {@link ISeries#isEnforceStrictSequence}, a series will allow more than
 * one data-point for the same abscissa, or not.
 *
 * @param <A> The Abscissa type
 * @param <O> The Ordinate type
 * @param <P> The type of the data point
 */
public interface ISeries<A extends Comparable<A>, O extends Comparable<O>, P extends ISeriesPoint<A, O>> extends Iterable<P>, IPersistentIdentifiable {
	/**
	 * @return the number of data points currently contained
	 */
	int size();
	/**
	 * @return true if empty, false otherwise
	 */
	boolean isEmpty();
	/**
	 * Snapshot iterator
	 */
	Iterator<P> iterator();
	/**
	 * Snapshot descending iterator
	 * @return a descending {@link java.util.Iterator} instance
	 */
	Iterator<P> descendingIterator();
	/**
	 * @return the first data-point, or null if the series is empty
	 */
	P getFirst();
	/**
	 * @return the last data-point, or null if the series is empty
	 */
	P getLast();
	/**
	 * @return the data point with the minimum value, or null if the series is empty
	 */
	P getMinimum();
	/**
	 * @return the data point with the maximum value, or null if the series is empty
	 */
	P getMaximum();
	/**
	 * Adds a listener to the series. Duplicate listeners are allowed. Listeners are notified in the order in which they have been added
	 */
	void addSeriesListener(ISeriesListener<A, O> listener);
	/**
	 * Remove the first instance of a listener from the series. If the listener is not present does nothing
	 */
	void removeSeriesListener(ISeriesListener<A, O> listener);
	/**
	 * @return The timestamp of the last change, in milliseconds from {@link System#currentTimeMillis}
	 */
	long getTimestamp();
	/**
	 * A series can enforce the strict sequence of data-points, in which case it will never have more than
	 * one data-point for a specific abscissa; or be more lenient, depending on the value of this method
	 * @return true if the series does not allow more than one data-point for each abscissa, false if it does
	 */
	boolean isEnforceStrictSequence();
	/**
	 * Factory method for mutable version of the implementors type
	 * @param ID A persistent ID for the new series.
	 * @return A mutable series, with the same parameters of the current one.
	 */
	IMutableSeries<A, O, P> createEmptyMutableSeries(String ID);
}
