/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.osgi.internal;

import com.quantcomponents.core.model.ITaskMonitor;


public class DummyTaskMonitor implements ITaskMonitor {
	@Override
	public void beginTask(int totalWork) {
		System.out.println("begin: " + totalWork);
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public void setCancelled(boolean value) {
	}

	@Override
	public void worked(int work) {
		for (int i = 0; i < work; i++) {
			System.out.print("-");
		}
	}

	@Override
	public void done() {
		System.out.println("");
	}
}
