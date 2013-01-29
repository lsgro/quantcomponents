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
 * 
 * A single mark to be displayed along an axis
 *
 * @param <T> the type of values along the axis
 */
public interface IMark<T> {
	/**
	 * Mark scale
	 */
	IMarkScale<T> getScale();
	/**
	 * Value on the axis
	 */
	T getValue();
}
