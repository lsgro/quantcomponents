/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.quantcomponents.algo.ExecutionCreationException;
import com.quantcomponents.algo.ExecutionType;
import com.quantcomponents.algo.IManagedRunnable.RunningStatus;
import com.quantcomponents.algo.IStockDatabaseTradingManager;
import com.quantcomponents.algo.ITradingManager;
import com.quantcomponents.algo.TradingAgentBindingHandle;
import com.quantcomponents.algo.TradingAgentConfigurationHandle;
import com.quantcomponents.algo.TradingAgentExecutionHandle;
import com.quantcomponents.algo.TradingAgentFactoryHandle;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.marketdata.IMarketDataManager;
import com.quantcomponents.marketdata.IOHLCTimeSeries;
import com.quantcomponents.marketdata.IStockDatabase;

public class StockDatabaseTradingManager implements IStockDatabaseTradingManager {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(StockDatabaseTradingManager.class.getName());
	private final Map<TradingAgentBindingHandle, Map<String, IStockDatabase>> inputStockDbsByHandle = new ConcurrentHashMap<TradingAgentBindingHandle, Map<String, IStockDatabase>>();
	private volatile ITradingManager tradingManager;
	private volatile IMarketDataManager marketDataManager;

	public void deactivate() {
		inputStockDbsByHandle.clear();
	}
	
	public void setTradingManager(ITradingManager tradingManager) {
		this.tradingManager = tradingManager;
	}

	public void setMarketDataManager(IMarketDataManager marketDataManager) {
		this.marketDataManager = marketDataManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TradingAgentBindingHandle createBinding(TradingAgentConfigurationHandle configurationHandle, Map<String, IStockDatabase> inputStockDatabases, String name) {
		Map<String, IOHLCTimeSeries> inputSeries = createInputSeriesFromStockDatabases(inputStockDatabases);
		TradingAgentBindingHandle bindingHandle = tradingManager.createBinding(configurationHandle, (Map<String, ? extends ISeries<Date, Double, ISeriesPoint<Date, Double>>>) inputSeries, name);
		inputStockDbsByHandle.put(bindingHandle, inputStockDatabases);
		return bindingHandle;
	}

	@Override
	public void removeBinding(TradingAgentBindingHandle bindingHandle) {
		inputStockDbsByHandle.remove(bindingHandle);
		tradingManager.removeBinding(bindingHandle);
	}

	@Override
	public Map<String, IStockDatabase> getBindingInputStockDatabases(TradingAgentBindingHandle bindingHandle) {
		Map<String, IStockDatabase> inputStockDatabases = inputStockDbsByHandle.get(bindingHandle);
		if (inputStockDatabases == null) {
			inputStockDatabases = new HashMap<String, IStockDatabase>();
			Map<String, ISeries<Date, Double, ISeriesPoint<Date, Double>>> inputSeries = tradingManager.getBindingInputSeries(bindingHandle);
			if (inputSeries != null) {
				for (Map.Entry<String, ISeries<Date, Double, ISeriesPoint<Date, Double>>> entry : inputSeries.entrySet()) {
					ISeries<Date, Double, ISeriesPoint<Date, Double>> input = entry.getValue();
					if (input != null) {
						IStockDatabase stockDatabase = marketDataManager.getStockDatabase(entry.getValue().getPersistentID());
						inputStockDatabases.put(entry.getKey(), stockDatabase);
					} 
				}
			}
		}
		return inputStockDatabases;
	}
	
	private Map<String, IOHLCTimeSeries> createInputSeriesFromStockDatabases(Map<String, IStockDatabase> inputStockDatabase) {
		Map<String, IOHLCTimeSeries> inputSeries = new HashMap<String, IOHLCTimeSeries>();
		for (Map.Entry<String, IStockDatabase> entry : inputStockDatabase.entrySet()) {
			inputSeries.put(entry.getKey(), entry.getValue().getVirtualTimeSeries());
		}
		return inputSeries;
	}

	// ---------------- delegate methods ------------------ 
	
	@Override
	public String getPrettyName() {
		return tradingManager.getPrettyName();
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
	public Properties getConfigurationProperties(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		return tradingManager.getConfigurationProperties(tradingAgentConfigurationHandle);
	}

	@Override
	public void removeConfiguration(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		tradingManager.removeConfiguration(tradingAgentConfigurationHandle);
	}

	@Override
	public TradingAgentFactoryHandle getParent(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		return tradingManager.getParent(tradingAgentConfigurationHandle);
	}

	@Override
	public TradingAgentExecutionHandle createExecution(TradingAgentBindingHandle bindingHandle, ExecutionType type) throws ExecutionCreationException {
		return tradingManager.createExecution(bindingHandle, type);
	}

	@Override
	public Collection<TradingAgentBindingHandle> getChildren(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		return tradingManager.getChildren(tradingAgentConfigurationHandle);
	}

	@Override
	public ISeries<Date, Double, ISeriesPoint<Date, Double>> getExecutionOutput(TradingAgentExecutionHandle executionHandle) {
		return tradingManager.getExecutionOutput(executionHandle);
	}

	@Override
	public void removeExecution(TradingAgentExecutionHandle executionHandle) {
		tradingManager.removeExecution(executionHandle);
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
