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

import org.eclipse.swt.graphics.GC;

/**
 * Elementary object which is drawable on a chart
 * 
 * @param <A> type of chart abscissa
 * @param <O> type of chart ordinate
 */
public interface IDrawable<A, O> {
	/**
	 * Draw the object on the chart
	 * @param metrics the current chart metrics
	 * @param gc a valid SWT graphic context
	 */
	void draw(IChartMetrics<A, O> metrics, GC gc);
}
