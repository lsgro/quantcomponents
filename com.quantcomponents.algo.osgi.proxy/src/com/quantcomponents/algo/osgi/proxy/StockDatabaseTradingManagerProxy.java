/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.osgi.proxy;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.quantcomponents.algo.ExecutionCreationException;
import com.quantcomponents.algo.ExecutionType;
import com.quantcomponents.algo.TradingAgentBindingHandle;
import com.quantcomponents.algo.TradingAgentConfigurationHandle;
import com.quantcomponents.algo.TradingAgentExecutionHandle;
import com.quantcomponents.algo.TradingAgentFactoryHandle;
import com.quantcomponents.algo.IManagedRunnable.RunningStatus;
import com.quantcomponents.algo.IStockDatabaseTradingManager;
import com.quantcomponents.algo.osgi.IStockDatabaseTradingManagerHost;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.osgi.ISeriesHost;
import com.quantcomponents.core.osgi.ISeriesIteratorHost;
import com.quantcomponents.core.osgi.ISeriesListenerHostLocal;
import com.quantcomponents.core.osgi.ImmutableSeriesProxy;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHandleMap;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHost;

public class StockDatabaseTradingManagerProxy implements IStockDatabaseTradingManager {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(StockDatabaseTradingManagerProxy.class.getName());
	private final Map<ServiceHandle<? extends ISeriesHost<Date, Double, ISeriesPoint<Date, Double>>>, ISeries<Date, Double, ISeriesPoint<Date, Double>>> outputSeriesByHandle = new ConcurrentHashMap<ServiceHandle<? extends ISeriesHost<Date, Double, ISeriesPoint<Date, Double>>>, ISeries<Date, Double, ISeriesPoint<Date, Double>>>();
	private volatile IStockDatabaseTradingManagerHost tradingManagerHost;
	private volatile IStockDatabaseHandleMap stockDatabaseHandleMap;
	private volatile ISeriesHost<Date, Double, ISeriesPoint<Date, Double>> seriesHost;
	private volatile ISeriesIteratorHost<Date, Double, ISeriesPoint<Date, Double>> seriesIteratorHost;
	private volatile ISeriesListenerHostLocal<Date, Double, ISeriesPoint<Date, Double>> listenerHost;
	
	public void deactivate() {
		tradingManagerHost = null;
		outputSeriesByHandle.clear();
	}
	
	public void setTradingAgentManagerHost(IStockDatabaseTradingManagerHost stockDatabaseTradingManagerHost, Map<?,?> properties) {
		this.tradingManagerHost = stockDatabaseTradingManagerHost;
	}

	public void setStockDatabaseHandleMap(IStockDatabaseHandleMap stockDatabaseHandleMap) {
		this.stockDatabaseHandleMap = stockDatabaseHandleMap;
	}
	
	public void setSeriesHost(ISeriesHost<Date, Double, ISeriesPoint<Date, Double>> seriesHost) {
		this.seriesHost = seriesHost;
	}

	public void setSeriesIteratorHost(ISeriesIteratorHost<Date, Double, ISeriesPoint<Date, Double>> seriesIteratorHost) {
		this.seriesIteratorHost = seriesIteratorHost;
	}

	public void setListenerHost(ISeriesListenerHostLocal<Date, Double, ISeriesPoint<Date, Double>> listenerHost) {
		this.listenerHost = listenerHost;
	}

	@Override
	public TradingAgentBindingHandle createBinding(TradingAgentConfigurationHandle tradingAgentConfigurationHandle, Map<String, IStockDatabase> inputStockDatabases, String name) {
		Map<String, ServiceHandle<IStockDatabaseHost>> inputStockDatabaseHandles = new HashMap<String, ServiceHandle<IStockDatabaseHost>>();
		for (Map.Entry<String, IStockDatabase> entry : inputStockDatabases.entrySet()) {
			ServiceHandle<IStockDatabaseHost> stockDbHandle = stockDatabaseHandleMap.getHandleByStockDb(entry.getValue());
			inputStockDatabaseHandles.put(entry.getKey(), stockDbHandle);
		}
		return tradingManagerHost.createBinding(tradingAgentConfigurationHandle, inputStockDatabaseHandles, name); 
	}

	@Override
	public Map<String, IStockDatabase> getBindingInputStockDatabases(TradingAgentBindingHandle bindingHandle) {
		Map<String, ServiceHandle<IStockDatabaseHost>> inputSeriesHandles = tradingManagerHost.getBindingInputStockDatabases(bindingHandle);
		Map<String, IStockDatabase> inputSeries = new HashMap<String, IStockDatabase>();
		for (Map.Entry<String, ServiceHandle<IStockDatabaseHost>> entry : inputSeriesHandles.entrySet()) {
			inputSeries.put(entry.getKey(), stockDatabaseHandleMap.getStockDbByHandle(entry.getValue()));
		}
		return inputSeries;
	}

	@Override
	public synchronized ISeries<Date, Double, ISeriesPoint<Date, Double>> getExecutionOutput(TradingAgentExecutionHandle executionHandle) {
		ISeries<Date, Double, ISeriesPoint<Date, Double>> outputSeries = outputSeriesByHandle.get(executionHandle);
		if (outputSeries == null) {
			ServiceHandle<? extends ISeriesHost<Date, Double, ISeriesPoint<Date, Double>>> outputSeriesHandle = tradingManagerHost.getExecutionOutput(executionHandle);
			outputSeries = new ImmutableSeriesProxy<Date, Double, ISeriesPoint<Date, Double>>(seriesHost, seriesIteratorHost, listenerHost, outputSeriesHandle);
			outputSeriesByHandle.put(outputSeriesHandle, outputSeries);
		}
		return outputSeries;
	}
	
	@Override
	public void removeExecution(TradingAgentExecutionHandle executionHandle) {
		outputSeriesByHandle.remove(executionHandle);
		tradingManagerHost.removeExecution(executionHandle);
	}
	
	@Override
	public String getPrettyName() {
		return tradingManagerHost.getPrettyName();
	}
	
	// ----------------- delegate methods -----------------
	
	@Override
	public Collection<TradingAgentFactoryHandle> getAllTradingAgentFactories() {
		return tradingManagerHost.getAllTradingAgentFactories();
	}

	@Override
	public boolean isConfigurationValid(TradingAgentFactoryHandle factoryHandle, Properties configuration, Map<String, String> messages) {
		return tradingManagerHost.isConfigurationValid(factoryHandle, configuration, messages);
	}

	@Override
	public TradingAgentConfigurationHandle createConfiguration(TradingAgentFactoryHandle factoryHandle, Properties configuration, String name) {
		return tradingManagerHost.createConfiguration(factoryHandle, configuration, name);
	}

	@Override
	public Properties getConfigurationProperties(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		return tradingManagerHost.getConfigurationProperties(tradingAgentConfigurationHandle);
	}

	@Override
	public Collection<TradingAgentConfigurationHandle> getChildren(TradingAgentFactoryHandle tradingAgentFactoryHandle) {
		return tradingManagerHost.getChildren(tradingAgentFactoryHandle);
	}
	
	@Override
	public TradingAgentFactoryHandle getParent(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		return tradingManagerHost.getParent(tradingAgentConfigurationHandle);
	}
	
	@Override
	public Collection<TradingAgentBindingHandle> getChildren(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		return tradingManagerHost.getChildren(tradingAgentConfigurationHandle);
	}
	
	@Override
	public TradingAgentConfigurationHandle getParent(TradingAgentBindingHandle tradingAgentBindingHandle) {
		return tradingManagerHost.getParent(tradingAgentBindingHandle);
	}
	
	@Override
	public boolean isExecutionTypeAvailable(ExecutionType type) {
		return tradingManagerHost.isExecutionTypeAvailable(type);
	}
	
	@Override
	public TradingAgentExecutionHandle createExecution(TradingAgentBindingHandle bindingHandle, ExecutionType type) throws ExecutionCreationException {
		return tradingManagerHost.createExecution(bindingHandle, type);
	}
	
	@Override
	public Collection<TradingAgentExecutionHandle> getChildren(TradingAgentBindingHandle tradingAgentBindingHandle) {
		return tradingManagerHost.getChildren(tradingAgentBindingHandle);
	}
	
	@Override
	public TradingAgentBindingHandle getParent(TradingAgentExecutionHandle TradingAgentExecutionHandle) {
		return tradingManagerHost.getParent(TradingAgentExecutionHandle);
	}
	
	@Override
	public void removeBinding(TradingAgentBindingHandle bindingHandle) {
		tradingManagerHost.removeBinding(bindingHandle);
	}
	
	@Override
	public void removeConfiguration(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		tradingManagerHost.removeConfiguration(tradingAgentConfigurationHandle);
	}
	
	@Override
	public void startExecution(TradingAgentExecutionHandle executionHandle) {
		tradingManagerHost.startExecution(executionHandle);
	}
	
	@Override
	public void pauseExecution(TradingAgentExecutionHandle executionHandle) {
		tradingManagerHost.pauseExecution(executionHandle);
	}
	
	@Override
	public void resumeExecution(TradingAgentExecutionHandle executionHandle) {
		tradingManagerHost.resumeExecution(executionHandle);
	}

	@Override
	public void killExecution(TradingAgentExecutionHandle executionHandle) {
		tradingManagerHost.killExecution(executionHandle);
	}

	@Override
	public RunningStatus getRunningStatus(TradingAgentExecutionHandle executionHandle) {
		return tradingManagerHost.getRunningStatus(executionHandle);
	}
}
