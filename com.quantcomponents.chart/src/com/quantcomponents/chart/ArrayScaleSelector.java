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

import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Implementation of {@link IMarkScaleSelector}
 *
 * @param <T> type of the axis values
 */
public class ArrayScaleSelector<T> implements IMarkScaleSelector<T> {
	private final IMarkScale<T>[] orderedG;
	
	@SuppressWarnings("unchecked")
	public ArrayScaleSelector(IMarkScale<T>[] granularities) {
		SortedMap<Double, IMarkScale<T>> sortedG = new TreeMap<Double, IMarkScale<T>>();
		for (IMarkScale<T> g : granularities) {
			sortedG.put(g.getStepSize(), g);
		}
		orderedG = sortedG.values().toArray(new IMarkScale[sortedG.size()]);
	}
	
	@Override
	public IMarkScale<T> markScale(double valueSpan, int maxMarkNumber) {
		for (int i = 0; i < orderedG.length; i++) {
			IMarkScale<T> g = orderedG[i];
			if (valueSpan / g.getStepSize() <= maxMarkNumber) {
				return orderedG[i];
			}
		}
		return orderedG[orderedG.length - 1];
	}

}
