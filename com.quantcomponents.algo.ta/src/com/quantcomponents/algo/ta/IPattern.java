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
 * Generic price-chart pattern
 * @param <A> type of the abscissa in the chart
 * @param <O> type of the ordinate in the chart
 */
public interface IPattern<A extends Comparable<A>, O extends Comparable<O>> {
	A getBeginIndex();
	A getEndIndex();
	List<ISeriesPoint<A, O>> getPoints();
	List<ITrendLine<A, O>> getTrendLines();
}
