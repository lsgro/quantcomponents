/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.osgi.host;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.quantcomponents.algo.ExecutionCreationException;
import com.quantcomponents.algo.ExecutionType;
import com.quantcomponents.algo.IStockDatabaseTradingManager;
import com.quantcomponents.algo.TradingAgentBindingHandle;
import com.quantcomponents.algo.TradingAgentConfigurationHandle;
import com.quantcomponents.algo.TradingAgentExecutionHandle;
import com.quantcomponents.algo.TradingAgentFactoryHandle;
import com.quantcomponents.algo.IManagedRunnable.RunningStatus;
import com.quantcomponents.algo.osgi.IStockDatabaseTradingManagerHost;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.osgi.ISeriesHost;
import com.quantcomponents.core.osgi.ISeriesHostLocal;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHost;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHostLocal;

public class StockDatabaseTradingManagerHost implements IStockDatabaseTradingManagerHost {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(StockDatabaseTradingManagerHost.class.getName());
	private final Map<TradingAgentExecutionHandle, ServiceHandle<? extends ISeriesHost<Date, Double, ISeriesPoint<Date, Double>>>> outputSeriesByExecution = new ConcurrentHashMap<TradingAgentExecutionHandle, ServiceHandle<? extends ISeriesHost<Date, Double, ISeriesPoint<Date, Double>>>>();
	private final Map<TradingAgentBindingHandle, Map<String, ServiceHandle<IStockDatabaseHost>>> stockDbHandlesByBindingHandle = new ConcurrentHashMap<TradingAgentBindingHandle, Map<String, ServiceHandle<IStockDatabaseHost>>>();
	private volatile IStockDatabaseTradingManager tradingManager;
	private volatile ISeriesHostLocal<Date, Double, ISeriesPoint<Date, Double>> outputSeriesHost;
	private volatile IStockDatabaseHostLocal stockDatabaseHost;
	
	public synchronized void deactivate() {
		outputSeriesByExecution.clear();
		stockDbHandlesByBindingHandle.clear();
	}
	
	public void setTradingManager(IStockDatabaseTradingManager manager) {
		this.tradingManager = manager;
	}

	public void setStockDatabaseHost(IStockDatabaseHostLocal stockDatabaseHost) {
		this.stockDatabaseHost = stockDatabaseHost;
	}
	
	public void setOutputSeriesHost(ISeriesHostLocal<Date, Double, ISeriesPoint<Date, Double>> outputSeriesHost) {
		this.outputSeriesHost = outputSeriesHost;
	}

	@Override
	public synchronized TradingAgentBindingHandle createBinding(TradingAgentConfigurationHandle tradingAgentConfigurationHandle, Map<String, ServiceHandle<IStockDatabaseHost>> inputSeriesHandles, String name) {
		Map<String, IStockDatabase> inputStockDatabases = new HashMap<String, IStockDatabase>();
		for (Map.Entry<String, ServiceHandle<IStockDatabaseHost>> entry : inputSeriesHandles.entrySet()) {
			IStockDatabase stockDb = stockDatabaseHost.getStockDatabase(entry.getValue());
			inputStockDatabases.put(entry.getKey(), stockDb);
		} 
		TradingAgentBindingHandle bindingHandle = tradingManager.createBinding(tradingAgentConfigurationHandle, inputStockDatabases, name);
		stockDbHandlesByBindingHandle.put(bindingHandle, inputSeriesHandles);
		return bindingHandle;
	}

	@Override
	public synchronized Map<String, ServiceHandle<IStockDatabaseHost>> getBindingInputStockDatabases(TradingAgentBindingHandle bindingHandle) {
		Map<String, ServiceHandle<IStockDatabaseHost>> inputStockDatabaseHandles = stockDbHandlesByBindingHandle.get(bindingHandle);
		if (inputStockDatabaseHandles == null) {
			inputStockDatabaseHandles = new HashMap<String, ServiceHandle<IStockDatabaseHost>>();
			Map<String, IStockDatabase> inputStockDatabases = tradingManager.getBindingInputStockDatabases(bindingHandle);
			for (Map.Entry<String, IStockDatabase> entry : inputStockDatabases.entrySet()){
				inputStockDatabaseHandles.put(entry.getKey(), stockDatabaseHost.getStockDatabaseHandle(entry.getValue()));
			}
			stockDbHandlesByBindingHandle.put(bindingHandle, inputStockDatabaseHandles);
		}
		return inputStockDatabaseHandles;
	}
	
	@Override
	public synchronized void removeBinding(TradingAgentBindingHandle bindingHandle) {
		stockDbHandlesByBindingHandle.remove(bindingHandle);
		tradingManager.removeBinding(bindingHandle);
	}

	@Override
	public synchronized void removeExecution(TradingAgentExecutionHandle executionHandle) {
		ServiceHandle<? extends ISeriesHost<Date, Double, ISeriesPoint<Date, Double>>> outputSeriesHandle = outputSeriesByExecution.remove(executionHandle);
		outputSeriesHost.removeSeries(outputSeriesHandle);
		tradingManager.removeExecution(executionHandle);
	}

	@Override
	public synchronized ServiceHandle<? extends ISeriesHost<Date, Double, ISeriesPoint<Date, Double>>> getExecutionOutput(TradingAgentExecutionHandle executionHandle) {
		ServiceHandle<? extends ISeriesHost<Date, Double, ISeriesPoint<Date, Double>>> outputSeriesHandle = outputSeriesByExecution.get(executionHandle);
		if (outputSeriesHandle == null) {
			outputSeriesHandle = retrieveExecutionOutput(executionHandle);
		}
		return outputSeriesHandle;
	}
	
	private synchronized ServiceHandle<? extends ISeriesHost<Date, Double, ISeriesPoint<Date, Double>>> retrieveExecutionOutput(TradingAgentExecutionHandle executionHandle) {
		ServiceHandle<? extends ISeriesHost<Date, Double, ISeriesPoint<Date, Double>>> outputSeriesHandle = null;
		ISeries<Date, Double, ISeriesPoint<Date, Double>> outputSeries = tradingManager.getExecutionOutput(executionHandle);
		if (outputSeries != null) {
			outputSeriesHandle = outputSeriesHost.addSeries(outputSeries);
			outputSeriesByExecution.put(executionHandle, outputSeriesHandle);
		}
		return outputSeriesHandle;
	}

	// ----------------- delegate methods -----------------
	
	@Override
	public String getPrettyName() {
		return tradingManager.getPrettyName();
	}

	@Override
	public boolean isExecutionTypeAvailable(ExecutionType type) {
		return tradingManager.isExecutionTypeAvailable(type);
	}
	
	@Override
	public TradingAgentExecutionHandle createExecution(TradingAgentBindingHandle bindingHandle, ExecutionType type) throws ExecutionCreationException {
		return tradingManager.createExecution(bindingHandle, type);
	}

	@Override
	public Collection<TradingAgentFactoryHandle> getAllTradingAgentFactories() {
		return tradingManager.getAllTradingAgentFactories();
	}

	@Override
	public boolean isConfigurationValid(TradingAgentFactoryHandle factoryHandle, Properties configuration, Map<String, String> messages) {
		return tradingManager.isConfigurationValid(factoryHandle, configuration, messages);
	}
	
	@Override
	public TradingAgentConfigurationHandle createConfiguration(TradingAgentFactoryHandle factoryHandle, Properties configuration, String name) {
		return tradingManager.createConfiguration(factoryHandle, configuration, name);
	}

	@Override
	public Collection<TradingAgentConfigurationHandle> getChildren(TradingAgentFactoryHandle tradingAgentFactoryHandle) {
		return tradingManager.getChildren(tradingAgentFactoryHandle);
	}

	@Override
	public TradingAgentFactoryHandle getParent(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		return tradingManager.getParent(tradingAgentConfigurationHandle);
	}

	@Override
	public Collection<TradingAgentBindingHandle> getChildren(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		return tradingManager.getChildren(tradingAgentConfigurationHandle);
	}

	@Override
	public TradingAgentConfigurationHandle getParent(TradingAgentBindingHandle tradingAgentBindingHandle) {
		return tradingManager.getParent(tradingAgentBindingHandle);
	}

	@Override
	public Collection<TradingAgentExecutionHandle> getChildren(TradingAgentBindingHandle tradingAgentBindingHandle) {
		return tradingManager.getChildren(tradingAgentBindingHandle);
	}

	@Override
	public TradingAgentBindingHandle getParent(TradingAgentExecutionHandle TradingAgentExecutionHandle) {
		return tradingManager.getParent(TradingAgentExecutionHandle);
	}

	@Override
	public Properties getConfigurationProperties(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		return tradingManager.getConfigurationProperties(tradingAgentConfigurationHandle);
	}

	@Override
	public void removeConfiguration(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		tradingManager.removeConfiguration(tradingAgentConfigurationHandle);
	}

	@Override
	public void startExecution(TradingAgentExecutionHandle executionHandle) {
		tradingManager.startExecution(executionHandle);
	}

	@Override
	public void pauseExecution(TradingAgentExecutionHandle executionHandle) {
		tradingManager.pauseExecution(executionHandle);
	}

	@Override
	public void resumeExecution(TradingAgentExecutionHandle executionHandle) {
		tradingManager.resumeExecution(executionHandle);
	}
	
	@Override
	public void killExecution(TradingAgentExecutionHandle executionHandle) {
		tradingManager.killExecution(executionHandle);
	}

	@Override
	public RunningStatus getRunningStatus(TradingAgentExecutionHandle executionHandle) {
		return tradingManager.getRunningStatus(executionHandle);
	}
}
