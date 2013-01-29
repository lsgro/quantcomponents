/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.remote;

import com.quantcomponents.core.model.ITaskMonitor;

/**
 * Local interface for {@link ITaskMonitorHost}
 * This interface is used on the client side in a distributed configuration,
 * to make the UI task monitors visible to the server.
 *
 */
public interface ITaskMonitorHostLocal {
	/**
	 * Add a real task monitor to the host, and receive a handle to it
	 * in exchange.
	 * The handle will then be used by the server side to communicate to
	 * this object, which in turn will operate the original task monitor.
	 * @param taskMonitor a task monitor from the UI
	 * @return a handle for this task monitor, to be used in the communications
	 * with the server
	 */
	ServiceHandle<ITaskMonitorHost> addTaskMonitor(ITaskMonitor taskMonitor);
	/**
	 * Deletes a task monitor corresponding to the handle.
	 * This method is important to detach listeners and free remote resources.
	 * @param taskMonitorHandle the handle of the task monitor to delete
	 */
	void deleteTaskMonitor(ServiceHandle<ITaskMonitorHost> taskMonitorHandle);
}
