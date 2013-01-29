/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ui.core;

import org.eclipse.core.runtime.IProgressMonitor;

import com.quantcomponents.core.model.ITaskMonitor;

public class TaskMonitorAdapter implements ITaskMonitor {
	private final String name;
	private final IProgressMonitor internalMonitor;

	public TaskMonitorAdapter(IProgressMonitor internalMonitor, String name) {
		this.internalMonitor = internalMonitor;
		this.name = name;
	}

	@Override
	public void beginTask(int totalWork) {
		internalMonitor.beginTask(name, totalWork);
	}

	@Override
	public boolean isCancelled() {
		return internalMonitor.isCanceled();
	}

	@Override
	public void setCancelled(boolean value) {
		internalMonitor.setCanceled(value);
	}

	@Override
	public void worked(int work) {
		internalMonitor.worked(work);
	}

	@Override
	public void done() {
		internalMonitor.done();
	}

}
