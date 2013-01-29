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

public class TaskMonitorProxy implements ITaskMonitor {
	private final ITaskMonitorHost taskMonitorHost;
	private final ServiceHandle<ITaskMonitorHost> taskMonitorHandle;

	public TaskMonitorProxy(ITaskMonitorHost taskMonitorHost, ServiceHandle<ITaskMonitorHost> taskMonitorHandle) {
		this.taskMonitorHost = taskMonitorHost;
		this.taskMonitorHandle = taskMonitorHandle;
	}

	@Override
	public void beginTask(int totalWork) {
		taskMonitorHost.beginTask(taskMonitorHandle, totalWork);
	}

	@Override
	public boolean isCancelled() {
		return taskMonitorHost.isCancelled(taskMonitorHandle);
	}

	@Override
	public void setCancelled(boolean value) {
		taskMonitorHost.setCancelled(taskMonitorHandle, value);
	}

	@Override
	public void worked(int work) {
		taskMonitorHost.worked(taskMonitorHandle, work);
	}

	@Override
	public void done() {
		taskMonitorHost.done(taskMonitorHandle);
	}

}
