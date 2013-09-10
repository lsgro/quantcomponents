/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.osgi;

import java.util.Date;

import com.quantcomponents.algo.ExecutionCreationException;
import com.quantcomponents.algo.ExecutionType;
import com.quantcomponents.algo.IManagedRunnable;
import com.quantcomponents.algo.TradingAgentBindingHandle;
import com.quantcomponents.algo.TradingAgentExecutionHandle;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.osgi.ISeriesHost;
import com.quantcomponents.core.remote.ServiceHandle;

public interface ITradingAgentExecutionManagerHost {
	boolean isExecutionTypeAvailable(ExecutionType type);
	TradingAgentExecutionHandle createExecution(TradingAgentBindingHandle bindingHandle, ExecutionType type) throws ExecutionCreationException;
	ServiceHandle<? extends ISeriesHost<Date, Double, ISeriesPoint<Date, Double>>> getExecutionOutput(TradingAgentExecutionHandle executionHandle);
	void removeExecution(TradingAgentExecutionHandle executionHandle);
	void startExecution(TradingAgentExecutionHandle executionHandle);
	void pauseExecution(TradingAgentExecutionHandle executionHandle);
	void resumeExecution(TradingAgentExecutionHandle executionHandle);
	void killExecution(TradingAgentExecutionHandle executionHandle);
	IManagedRunnable.RunningStatus getRunningStatus(TradingAgentExecutionHandle executionHandle);
}
