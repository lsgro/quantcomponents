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
 * Operator to extract a subsets from a series.
 * Implementors should do a snapshot, and return a "frozen" object.
 *
 * @param <A> The Abscissa type
 * @param <O> The Ordinate type
 * @param <P> The type of the data point
 */
public interface ISeriesOperator<A extends Comparable<A>, O extends Comparable<O>, P extends ISeriesPoint<A, O>> {
	/**
	 * Returns a series of the same kind
	 * @param series the source series
	 * @return the transformed series
	 */
	ISeries<A, O, P> transform(ISeries<A, O, P> series);
}
