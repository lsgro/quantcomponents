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
 * Series listener.
 * Most interaction within the framework is achieved by listening to series updates.
 * The methods in this interface are called synchronously in the same thread, in the
 * same order with which the listeners have been added.
 * This is to maintain the minimum latency when reacting to new data.
 * Execution of this method should therefore be very short and should never block.
 * Any long or potentially blocking job must be carried out in a separate thread.
 * Any exception should be catched inside the methods.
 *
 * @param <A> The Abscissa type
 * @param <O> The Ordinate type
 */
public interface ISeriesListener<A extends Comparable<A>, O extends Comparable<O>> {
	/**
	 * This mehod is called whenever an existing data-point is updated.
	 * @param existingItem the original data-point
	 * @param updatedItem the updated data-point
	 */
	void onItemUpdated(ISeriesPoint<A, O> existingItem, ISeriesPoint<A, O> updatedItem);
	/**
	 * This mehod is called whenever a new data-point is added to the series
	 * @param newItem the new data-point
	 */
	void onItemAdded(ISeriesPoint<A, O> newItem);
}
