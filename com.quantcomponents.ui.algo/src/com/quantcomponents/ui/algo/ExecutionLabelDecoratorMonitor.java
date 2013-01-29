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

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.ui.PlatformUI;

import com.quantcomponents.algo.ITradingAgentExecutionManager;
import com.quantcomponents.algo.TradingAgentExecutionHandle;
import com.quantcomponents.algo.IManagedRunnable.RunningStatus;

public class ExecutionLabelDecoratorMonitor implements Runnable {
	private final ITradingAgentExecutionManager tradingExecutionManager;
	private final TradingAgentExecutionHandle executionHandle;
	private final TradingAgentExecutionWrapper wrapper;
	private final long checkInterval;
	
	public ExecutionLabelDecoratorMonitor(ITradingAgentExecutionManager tradingExecutionManager, TradingAgentExecutionHandle executionHandle, TradingAgentExecutionWrapper wrapper, long checkInterval) {
		this.tradingExecutionManager = tradingExecutionManager;
		this.executionHandle = executionHandle;
		this.wrapper = wrapper;
		this.checkInterval = checkInterval;
	}

	@Override
	public void run() {
		IBaseLabelProvider labelDecorator = PlatformUI.getWorkbench().getDecoratorManager().getBaseLabelProvider(ExecutionRunningDecorator.DECORATOR_ID);
		if (labelDecorator != null) { // it is enabled
			ExecutionRunningDecorator executionRunningIconDecorator = (ExecutionRunningDecorator) labelDecorator;
			while (tradingExecutionManager.getRunningStatus(executionHandle) == RunningStatus.NEW) {
				try {
					Thread.sleep(checkInterval);
				} catch (InterruptedException e) {
					// ignore it
				}
			}
			executionRunningIconDecorator.fireLabelProviderChanged(wrapper);
			while (tradingExecutionManager.getRunningStatus(executionHandle) != RunningStatus.TERMINATED) {
				try {
					Thread.sleep(checkInterval);
				} catch (InterruptedException e) {
					// ignore it
				}
			}
			executionRunningIconDecorator.fireLabelProviderChanged(wrapper);
		}
	}
}
