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

import java.util.Date;
import java.util.Map;

/**
 * A processor with one or several inputs series, and one output series.
 * In general, processors will be able to process realtime event from the input series.
 * Several processors can be stacked together by feeding a processor's output
 * to another as input.
 *
 * @param <A> The Abscissa type
 * @param <O> The Ordinate type
 */
public interface ISeriesProcessor<A extends Comparable<A>, O extends Comparable<O>> {
	/**
	 * Wire the processor with its input and output series.
	 * Input and output are connected with the same methods, to signal to the processor that
	 * it is completely wired, and it can start processing.
	 * @param input a map of inputs, mapped by their tags
	 * @param output a writable series
	 */
	void wire(Map<String, ? extends ISeries<A, O, ? extends ISeriesPoint<A, O>>> input, ISeriesAugmentable<Date, Double, ISeriesPoint<Date, Double>> output);
	/**
	 * Unwire the processor.
	 * After this method, the processor should be detached from the inputs and it should stop
	 * writing to the output series.
	 */
	void unwire();
}
