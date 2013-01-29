/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.osgi.internal;

import com.quantcomponents.algo.IExecutionService;
import com.quantcomponents.algo.IOrderReceiver;
import com.quantcomponents.algo.IStockDatabaseTradingManager;

public class ShellInterpreter {
	private IOrderReceiver executionService;
	private IStockDatabaseTradingManager tradingManager;
	
	public void setExecutionService(IExecutionService executionService) {
		this.executionService = new ExecutionServiceShellProxy(executionService); // necessary otherwise the gogo shell doesn't find the methods
	}
	public void setTradingManager(IStockDatabaseTradingManager tradingManager) {
		this.tradingManager = tradingManager;
	}
	public IOrderReceiver executionservice() {
		return executionService;
	}
	public IStockDatabaseTradingManager tradingmanager() {
		return tradingManager;
	}
}
