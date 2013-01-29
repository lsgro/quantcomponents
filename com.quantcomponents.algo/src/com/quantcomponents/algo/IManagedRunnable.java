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

/**
 * 
 * Parent interface of processors that run in another thread.
 * This interface gives the caller the ability to interact with
 * the execution of the processor.
 * Since this interface extends {@link java.lang.Runnable}, it
 * gives the caller the choice of synchronous or asynchronous
 * execution.
 * The other interface methods are useful in case of asynchronous
 * execution.
 * All implementors must be thread safe.
 */
public interface IManagedRunnable extends Runnable {
	/**
	 * Running status
	 * The status diagram is:
	 * <ul>
	 * <li>NEW -> RUNNING</li>
	 * <li>RUNNING -> PAUSED [optional]</li>
	 * <li>PAUSED -> RUNNING [optional]</li>
	 * <li>RUNNING -> TERMINATED</li>
	 * </ul>
	 */
	public enum RunningStatus {
		/**
		 * New processor, not started
		 */
		NEW,
		/**
		 * Running processor
		 */
		RUNNING,
		/**
		 * Paused processor
		 */
		PAUSED, 
		/**
		 * Processor terminated
		 * After reaching this status, the processor can not be used anymore
		 */
		TERMINATED };
	/**
	 * Transition from RUNNING to PAUSED
	 * Optional method. If not running or method not implemented does nothing.
	 */
	void pause();
	/**
	 * Transition from PAUSED to RUNNING
	 * Optional method. If not paused or method not implemented does nothing.
	 */
	void resume();
	/**
	 * Transition to TERMINATED
	 * If not running does nothing.
	 */
	void kill();
	/**
	 * Current status of the runnable instance
	 * @return A RunningStatus instance
	 */
	RunningStatus getRunningStatus();
}
