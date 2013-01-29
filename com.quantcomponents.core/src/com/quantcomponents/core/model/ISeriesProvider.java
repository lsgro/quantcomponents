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
 * 
 * Base interface for any class that contains a series
 * @param <A> The Abscissa type
 * @param <O> The Ordinate type
 */
public interface ISeriesProvider<A extends Comparable<A>, O extends Comparable<O>> {
	ISeries<A, O, ? extends ISeriesPoint<A, O>> getSeries(String ID);
}
