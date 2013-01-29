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
 * Abstraction for UI task monitors.
 * To insulate the API from third party APIs, like Eclipse UI API, any task monitor
 * coming from the user interface can be wrapped in an object implementing this
 * interface, to provide the users with a way to interact with long-running tasks.
 *
 */
public interface ITaskMonitor {
	/**
	 * To be called when the task begins
	 * @param totalWork the total number of atomic sub-task in which the task can be
	 * subdivided
	 */
	void beginTask(int totalWork);
	/**
	 * Check the monitor if the task has been cancelled.
	 * It should be called as often as possible during the execution.
	 * @return true if the task has been cancelled, false otherwise
	 */
	boolean isCancelled();
	/**
	 * Set the task to cancelled.
	 * This can be called by the user to cancel the task.
	 * @param value true to cancel the task
	 */
	void setCancelled(boolean value);
	/**
	 * Task process: enter the number of sub-task completed from the last call
	 * of this method. E.g. if beginTask(10) has been called, then this method
	 * can be called ten times with the value of 1, or 5 times with the value of
	 * 2, etc.
	 * @param work the number of sub-task completed
	 */
	void worked(int work);
	/**
	 * Call when the task is 100% complete.
	 */
	void done();
}
