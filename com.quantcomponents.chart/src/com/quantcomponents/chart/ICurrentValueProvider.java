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
 * Chart objects that can provide a 'current value' to be highlighted
 *
 * @param <T> type of the data-points
 */
public interface ICurrentValueProvider<T> {
	T getCurrentValue();
}
