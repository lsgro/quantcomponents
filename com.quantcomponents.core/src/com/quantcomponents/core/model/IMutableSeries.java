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
 * Mutable series interface
 *
 * @param <A> The Abscissa type
 * @param <O> The Ordinate type
 * @param <P> The type of the data point
 */
public interface IMutableSeries<A extends Comparable<A>, O extends Comparable<O>, P extends ISeriesPoint<A, O>> extends ISeries<A, O, P>, ISeriesAugmentable<A, O, P> {
	/**
	 * Empty the series
	 */
	void clear();
}
