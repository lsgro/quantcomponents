/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ui.algo;

import com.quantcomponents.algo.HierarchyItemHandle;
import com.quantcomponents.algo.IStockDatabaseTradingManager;

public class BaseWrapper<T extends HierarchyItemHandle> {
	private final T handle;
	private final IStockDatabaseTradingManager manager;
	
	public BaseWrapper(T handle, IStockDatabaseTradingManager manager) {
		this.handle = handle;
		this.manager = manager;
	}
	
	public IStockDatabaseTradingManager getManager() {
		return manager;
	}
	
	public T getHandle() {
		return handle;
	}
}
