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

import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Interface to manage the execution of an algorithm
 * All operations to create, execute, manage the execution of a trading algorithm can be performed from
 * this interface.
 * @see ITradingHierarchyManager
 */
public interface ITradingAgentExecutionManager {
	/**
	 * Query availability of execution type
	 * @param type type of execution: LIVE, PAPER or BACKTEST
	 * @return true if the requested execution type is available, false otherwise
	 */
	boolean isExecutionTypeAvailable(ExecutionType type);
	/**
	 * Create an execution from a bound trading algorithm
	 * @param bindingHandle handle to the bound trading algorithm
	 * @param type type of execution: LIVE, PAPER or BACKTEST
	 * @return a handle to the created execution
	 * @throws ExecutionCreationException
	 */
	TradingAgentExecutionHandle createExecution(TradingAgentBindingHandle bindingHandle, ExecutionType type) throws ExecutionCreationException;
	/**
	 * Returns the execution output series
	 * @param executionHandle hadle of the execution
	 * @return a series containing all the output data from the algorithm execution
	 */
	ISeries<Date, Double, ISeriesPoint<Date, Double>> getExecutionOutput(TradingAgentExecutionHandle executionHandle);
	/**
	 * Remove an execution
	 * @param executionHandle handle of the execution to be removed
	 */
	void removeExecution(TradingAgentExecutionHandle executionHandle);
	/**
	 * Start the trading algorithm execution
	 * @param executionHandle the handle of the execution to be started
	 */
	void startExecution(TradingAgentExecutionHandle executionHandle);
	/**
	 * Pause the trading algorithm execution. If the execution is not running, or if it cannot be paused, nothing happens
	 * @param executionHandle the handle of the execution to be paused
	 */
	void pauseExecution(TradingAgentExecutionHandle executionHandle);
	/**
	 * Resume the trading algorithm execution. If the execution is not paused, nothing happens
	 * @param executionHandle the handle of the execution to be resumed
	 */
	void resumeExecution(TradingAgentExecutionHandle executionHandle);
	/**
	 * Kill the trading algorithm execution. If the execution is not running or paused, nothing happens
	 * @param executionHandle the handle of the execution to be killed
	 */
	void killExecution(TradingAgentExecutionHandle executionHandle);
	/**
	 * Returns the running status of the execution
	 * @param executionHandle an execution handle
	 */
	IManagedRunnable.RunningStatus getRunningStatus(TradingAgentExecutionHandle executionHandle);
}
