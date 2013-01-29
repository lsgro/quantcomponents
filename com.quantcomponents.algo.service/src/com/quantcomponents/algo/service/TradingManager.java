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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.algo.ExecutionCreationException;
import com.quantcomponents.algo.ExecutionType;
import com.quantcomponents.algo.IExecutionService;
import com.quantcomponents.algo.IManagedRunnable.RunningStatus;
import com.quantcomponents.algo.ISimulatedExecutionServiceFactory;
import com.quantcomponents.algo.ITradingAgent;
import com.quantcomponents.algo.ITradingAgentExecution;
import com.quantcomponents.algo.ITradingAgentFactory;
import com.quantcomponents.algo.ITradingAgentHierarchyContainer;
import com.quantcomponents.algo.ITradingManager;
import com.quantcomponents.algo.TradingAgentBinding;
import com.quantcomponents.algo.TradingAgentBindingHandle;
import com.quantcomponents.algo.TradingAgentConfiguration;
import com.quantcomponents.algo.TradingAgentConfigurationHandle;
import com.quantcomponents.algo.TradingAgentExecutionHandle;
import com.quantcomponents.algo.TradingAgentFactoryHandle;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.series.LinkedListSeries;

public class TradingManager implements ITradingManager {
	private static final Logger logger = Logger.getLogger(TradingManager.class.getName());
	private volatile String name;
	private volatile ITradingAgentHierarchyContainer hierarchyContainer;
	private volatile IExecutionService liveExecutionService;
	private volatile ISimulatedExecutionServiceFactory simulatedExecutionServiceFactory;
	private volatile ExecutorService threadPool;
	
	public TradingManager() {
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, "Cound not find hostname", e);
			hostname = "localhost";
		}
		name = "TradingManager@" + hostname;
	}
	
	public void deactivate() { 
		logger.log(Level.INFO, "Shutting down trading manager!");
	}
	
	public void setHierarchyContainer(ITradingAgentHierarchyContainer hierarchyContainer) {
		this.hierarchyContainer = hierarchyContainer;
	}

	public void addTradingAgentFactory(ITradingAgentFactory factory) {
		hierarchyContainer.addTradingAgentFactory(factory);
	}
	
	public void removeTradingAgentFactory(ITradingAgentFactory factory) {
		hierarchyContainer.removeTradingAgentFactory(factory);
	}
	
	public void setLiveExecutionService(IExecutionService liveExecutionService) {
		logger.log(Level.INFO, "Setting LIVE execution service");
		this.liveExecutionService = liveExecutionService;
		if (liveExecutionService != null) {
			name += "[" + liveExecutionService.toString() + "]";
		}
	}
	
	public void resetLiveExecutionService(IExecutionService liveExecutionService) {
		if (this.liveExecutionService == liveExecutionService) {
			this.liveExecutionService = null;
		}
	}

	public void setSimulatedExecutionServiceFactory(ISimulatedExecutionServiceFactory simulatedExecutionServiceFactory) {
		logger.log(Level.INFO, "Setting SIMULATED execution service factory");
		this.simulatedExecutionServiceFactory = simulatedExecutionServiceFactory;
	}

	public void resetSimulatedExecutionServiceFactory(ISimulatedExecutionServiceFactory simulatedExecutionServiceFactory) {
		if (this.simulatedExecutionServiceFactory == simulatedExecutionServiceFactory) {
			this.simulatedExecutionServiceFactory = null;
		}
	}

	public void setThreadPool(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}

	@Override
	public Collection<TradingAgentFactoryHandle> getAllTradingAgentFactories() {
		return hierarchyContainer.getAllTradingAgentFactories();
	}

	@Override
	public boolean isConfigurationValid(TradingAgentFactoryHandle factoryHandle, Properties configuration, Map<String, String> messages) {
		ITradingAgentFactory factory = retrieveFactory(factoryHandle);
		return factory.isConfigurationValid(configuration, messages);
	}
	
	@Override
	public TradingAgentConfigurationHandle createConfiguration(TradingAgentFactoryHandle factoryHandle, Properties properties, String name) {
		ITradingAgentFactory factory = retrieveFactory(factoryHandle);
		TradingAgentConfiguration configuration = new TradingAgentConfiguration(factory, properties);
		TradingAgentConfigurationHandle handle = new TradingAgentConfigurationHandle(name == null || name.equals("") ? configuration.toString() : name, factory.getInputSeriesNames());
		hierarchyContainer.putTradingAgentConfiguration(handle, configuration, factoryHandle);
		return handle;
	}

	@Override
	public TradingAgentBindingHandle createBinding(TradingAgentConfigurationHandle configurationHandle, Map<String, ? extends ISeries<Date, Double, ISeriesPoint<Date, Double>>> inputSeries, String name) {
		TradingAgentConfiguration configuration = retrieveConfiguration(configurationHandle);
		TradingAgentBinding binding = new TradingAgentBinding(configuration, inputSeries);
		TradingAgentBindingHandle handle = new TradingAgentBindingHandle(name == null || name.equals("") ? binding.toString() : name);
		hierarchyContainer.putTradingAgentBinding(handle, binding, configurationHandle);
		return handle;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ISeries<Date, Double, ISeriesPoint<Date, Double>>> getBindingInputSeries(TradingAgentBindingHandle bindingHandle) {
		TradingAgentBinding binding = retrieveBinding(bindingHandle);
		return binding == null ? null : (Map<String, ISeries<Date, Double, ISeriesPoint<Date, Double>>>) binding.getInputSeries();
	}
	
	@Override
	public TradingAgentExecutionHandle createExecution(TradingAgentBindingHandle bindingHandle, ExecutionType type) throws ExecutionCreationException {
		TradingAgentBinding binding = retrieveBinding(bindingHandle);
		ITradingAgentExecution execution;
		ITradingAgent tradingAgent = binding.getConfiguration().newTradingAgent();
		if (type == ExecutionType.LIVE){
			if (liveExecutionService == null) {
				throw new ExecutionCreationException("Live execution service not available");
			}
			execution = new TradingAgentExecution(tradingAgent, liveExecutionService);
		} else {
			if (simulatedExecutionServiceFactory == null) {
				throw new ExecutionCreationException("Simulated execution service factory not available");
			}
			execution = new SimulatedTradingAgentExecution(tradingAgent, simulatedExecutionServiceFactory.createSimulatedExecutionService());
		}
		String executionOutputSeriesId = "output-" + execution.toString();
		LinkedListSeries<Date, Double, ISeriesPoint<Date, Double>> outputSeries = new LinkedListSeries<Date, Double, ISeriesPoint<Date, Double>>(executionOutputSeriesId, false); 
		execution.wire(binding.getInputSeries(), outputSeries);
		TradingAgentExecutionHandle handle = new TradingAgentExecutionHandle(execution.toString());
		hierarchyContainer.putTradingAgentExecution(handle, execution, bindingHandle);
		return handle;
	}

	@Override
	public ISeries<Date, Double, ISeriesPoint<Date, Double>> getExecutionOutput(TradingAgentExecutionHandle executionHandle) {
		return hierarchyContainer.getTradingAgentExecution(executionHandle).getOutput();
	}

	@Override
	public Properties getConfigurationProperties(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		return hierarchyContainer.getTradingAgentConfiguration(tradingAgentConfigurationHandle).getProperties();
	}

	@Override
	public void removeExecution(TradingAgentExecutionHandle executionHandle) {
		ITradingAgentExecution execution = hierarchyContainer.removeTradingAgentExecution(executionHandle);
		if (execution == null) {
			throw new IllegalArgumentException("Handle: " + executionHandle + " does not correspont to an object");
		}
		execution.kill();
	}

	@Override
	public void removeBinding(TradingAgentBindingHandle bindingHandle) {
		hierarchyContainer.removeTradingAgentBinding(bindingHandle);
	}

	@Override
	public void removeConfiguration(TradingAgentConfigurationHandle configurationHandle) {
		hierarchyContainer.removeTradingAgentConfiguration(configurationHandle);
	}

	@Override
	public void startExecution(TradingAgentExecutionHandle executionHandle) {
		threadPool.execute(retrieveExecution(executionHandle));
	}

	@Override
	public void pauseExecution(TradingAgentExecutionHandle executionHandle) {
		retrieveExecution(executionHandle).pause();
	}

	@Override
	public void resumeExecution(TradingAgentExecutionHandle executionHandle) {
		retrieveExecution(executionHandle).resume();
	}

	@Override
	public void killExecution(TradingAgentExecutionHandle executionHandle) {
		retrieveExecution(executionHandle).kill();
	}

	@Override
	public RunningStatus getRunningStatus(TradingAgentExecutionHandle executionHandle) {
		return retrieveExecution(executionHandle).getRunningStatus();
	}
	
	private ITradingAgentExecution retrieveExecution(TradingAgentExecutionHandle executionHandle) {
		ITradingAgentExecution execution = hierarchyContainer.getTradingAgentExecution(executionHandle);
		if (execution == null) {
			throw new IllegalArgumentException("Handle: " + execution + " does not correspont to an object");
		}
		return execution;
	}
	
	private TradingAgentBinding retrieveBinding(TradingAgentBindingHandle bindingHandle) {
		TradingAgentBinding binding = hierarchyContainer.getTradingAgentBinding(bindingHandle);
		if (binding == null) {
			throw new IllegalArgumentException("Handle: " + bindingHandle + " does not correspont to an object");
		}
		return binding;
	}
	
	private TradingAgentConfiguration retrieveConfiguration(TradingAgentConfigurationHandle configurationHandle) {
		TradingAgentConfiguration configuration = hierarchyContainer.getTradingAgentConfiguration(configurationHandle);
		if (configuration == null) {
			throw new IllegalArgumentException("Handle: " + configurationHandle + " does not correspont to an object");
		}
		return configuration;
	}
	
	private ITradingAgentFactory retrieveFactory(TradingAgentFactoryHandle factoryHandle) {
		ITradingAgentFactory factory = hierarchyContainer.getTradingAgentFactory(factoryHandle);
		if (factory == null) {
			throw new IllegalArgumentException("Handle: " + factoryHandle + " does not correspont to an object");
		}
		return factory;
	}

	@Override
	public String getPrettyName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getPrettyName();
	}

	@Override
	public Collection<TradingAgentConfigurationHandle> getChildren(TradingAgentFactoryHandle tradingAgentFactoryHandle) {
		return hierarchyContainer.getChildrenHandles(tradingAgentFactoryHandle);
	}

	@Override
	public TradingAgentFactoryHandle getParent(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		return hierarchyContainer.getParentHandle(tradingAgentConfigurationHandle);
	}

	@Override
	public Collection<TradingAgentBindingHandle> getChildren(TradingAgentConfigurationHandle tradingAgentConfigurationHandle) {
		return hierarchyContainer.getChildrenHandles(tradingAgentConfigurationHandle);
	}

	@Override
	public TradingAgentConfigurationHandle getParent(TradingAgentBindingHandle tradingAgentBindingHandle) {
		return hierarchyContainer.getParentHandle(tradingAgentBindingHandle);
	}

	@Override
	public Collection<TradingAgentExecutionHandle> getChildren(TradingAgentBindingHandle tradingAgentBindingHandle) {
		return hierarchyContainer.getChildrenHandles(tradingAgentBindingHandle);
	}

	@Override
	public TradingAgentBindingHandle getParent(TradingAgentExecutionHandle tradingAgentExecutionHandle) {
		return hierarchyContainer.getParentHandle(tradingAgentExecutionHandle);
	}

}
