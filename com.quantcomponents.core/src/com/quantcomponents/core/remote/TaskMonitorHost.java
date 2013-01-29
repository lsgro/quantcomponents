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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.quantcomponents.core.model.ITaskMonitor;

public class TaskMonitorHost implements ITaskMonitorHost, ITaskMonitorHostLocal {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(TaskMonitorHost.class.getName());
	private final Map<ServiceHandle<ITaskMonitorHost>, ITaskMonitor> allTaskMonitors = new ConcurrentHashMap<ServiceHandle<ITaskMonitorHost>, ITaskMonitor>();
	private volatile IUIDGenerator uidGenerator;
	
	public TaskMonitorHost() {}
	
	public TaskMonitorHost(IUIDGenerator uidGenerator) {
		this.uidGenerator = uidGenerator;
	}

	public void deactivate() {
		allTaskMonitors.clear();
	}
	
	public void setUidGenerator(IUIDGenerator uidGenerator) {
		this.uidGenerator = uidGenerator;
	}

	@Override
	public ServiceHandle<ITaskMonitorHost> addTaskMonitor(ITaskMonitor taskMonitor) {
		ServiceHandle<ITaskMonitorHost> handle = new ServiceHandle<ITaskMonitorHost>(uidGenerator.nextUID());
		allTaskMonitors.put(handle, taskMonitor);
		return handle;
	}

	@Override
	public void deleteTaskMonitor(ServiceHandle<ITaskMonitorHost> taskMonitorHandle) {
		allTaskMonitors.remove(taskMonitorHandle);
	}

	@Override
	public void beginTask(ServiceHandle<ITaskMonitorHost> taskMonitorHandle, int totalWork) {
		retrieveTaskMonitor(taskMonitorHandle).beginTask(totalWork);
	}

	@Override
	public boolean isCancelled(ServiceHandle<ITaskMonitorHost> taskMonitorHandle) {
		return retrieveTaskMonitor(taskMonitorHandle).isCancelled();
	}

	@Override
	public void setCancelled(ServiceHandle<ITaskMonitorHost> taskMonitorHandle, boolean value) {
		retrieveTaskMonitor(taskMonitorHandle).setCancelled(value);
	}

	@Override
	public void worked(ServiceHandle<ITaskMonitorHost> taskMonitorHandle, int work) {
		retrieveTaskMonitor(taskMonitorHandle).worked(work);
	}

	@Override
	public void done(ServiceHandle<ITaskMonitorHost> taskMonitorHandle) {
		retrieveTaskMonitor(taskMonitorHandle).done();
	}
	
	private ITaskMonitor retrieveTaskMonitor(ServiceHandle<ITaskMonitorHost> taskMonitorHandle) {
		ITaskMonitor taskMonitor = allTaskMonitors.get(taskMonitorHandle);
		if (taskMonitor == null) {
			throw new IllegalArgumentException("Task monitor for handle: " + taskMonitorHandle + " not found");
		}
		return taskMonitor;
	}

}
