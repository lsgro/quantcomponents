/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo;

import java.util.Date;
import java.util.Map;

import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.model.ISeriesProcessor;

/**
 * 
 * This interface represents a single execution of a configured, bound, trading algorithm
 * @see ITradingHierarchyManager
 * @see ITradingAgentExecutionManager
 * @see com.quantcomponents.core.model.ISeriesProcessor
 */
public interface ITradingAgentExecution extends ISeriesProcessor<Date, Double>, IManagedRunnable, IOrderReceiver, IPositionListener {
	/**
	 * Returns the input series of the execution 
	 */
	Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> getInput();
	/**
	 * Returns the output series of the execution
	 */
	ISeries<Date, Double, ISeriesPoint<Date, Double>> getOutput();
}
