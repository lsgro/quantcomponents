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


/**
 * Remote host version of {@link com.quantcomponents.core.model.ITaskMonitor}.
 * This interface is typically implemented on the client side in a distributed
 * configuration.
 * The server sends progress message, that in turn are passed to an internal
 * {@link com.quantcomponents.core.model.ITaskMonitor}, visible on the client.
 *
 */
public interface ITaskMonitorHost {
	void beginTask(ServiceHandle<ITaskMonitorHost> taskMonitorHandle, int totalWork);
	boolean isCancelled(ServiceHandle<ITaskMonitorHost> taskMonitorHandle);
	void setCancelled(ServiceHandle<ITaskMonitorHost> taskMonitorHandle, boolean value);
	void worked(ServiceHandle<ITaskMonitorHost> taskMonitorHandle, int work);
	void done(ServiceHandle<ITaskMonitorHost> taskMonitorHandle);
}
