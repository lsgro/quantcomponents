/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.container.serializing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.algo.DummyTradingAgentExecution;
import com.quantcomponents.algo.IHierarchyItemHandle;
import com.quantcomponents.algo.ITradingAgentExecution;
import com.quantcomponents.algo.ITradingAgentFactory;
import com.quantcomponents.algo.MemoryTradingAgentHierarchyContainer;
import com.quantcomponents.algo.TradingAgentBinding;
import com.quantcomponents.algo.TradingAgentBindingHandle;
import com.quantcomponents.algo.TradingAgentConfiguration;
import com.quantcomponents.algo.TradingAgentConfigurationHandle;
import com.quantcomponents.algo.TradingAgentExecutionHandle;
import com.quantcomponents.algo.TradingAgentFactoryHandle;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesAugmentable;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.marketdata.IMarketDataManager;

public class SerializingTradingAgentHierarchyContainer extends MemoryTradingAgentHierarchyContainer {
	
	private static class BindingPersistentData implements Serializable {
		private static final long serialVersionUID = -3389176585646320417L;
		Map<String, String> inputSeriesNames = new HashMap<String, String>(); 
		IHierarchyItemHandle configurationHandle;
	}
	
	private static class ExecutionPersistentData implements Serializable {
		private static final long serialVersionUID = 6022916144134872069L;
		Map<String, String> inputSeriesNames = new HashMap<String, String>(); 
		ISeries<Date, Double, ISeriesPoint<Date, Double>> outputSeries;
	}
	
	private static final Logger logger = Logger.getLogger(SerializingTradingAgentHierarchyContainer.class.getName());
	public static final String DEFAULT_PERSISTENCE_DIR_NAME = ".";
	public static final String PERSISTENCE_DIR_NAME_KEY = "persistence.directory";
	private volatile IMarketDataManager marketDataManager;
	private volatile File persistenceDirectory;
	
	public void setMarketDataManager(IMarketDataManager marketDataManager) {
		this.marketDataManager = marketDataManager;
	}
	
	public void activate(Map<?,?> properties) {
		String persistenceDirName = (String) properties.get(PERSISTENCE_DIR_NAME_KEY);
		if (persistenceDirName == null) {
			persistenceDirName = DEFAULT_PERSISTENCE_DIR_NAME;
		}
		persistenceDirectory = new File(persistenceDirName);
	}
	
	public void deactivate() { 
		saveState();
	}
	
	@Override
	public void addTradingAgentFactory(ITradingAgentFactory factory) {
		super.addTradingAgentFactory(factory);
		TradingAgentFactoryHandle factoryHandle = new TradingAgentFactoryHandle(factory.getClass().getName(), factory.getConfigurationKeys());
		if (persistenceDirectory.exists() && persistenceDirectory.isDirectory()) {
			File[] dirFiles = persistenceDirectory.listFiles();
			String dataFileName = factoryHandle.getPrettyName() + ".hierarchy";
			for (File file : dirFiles) {
				if (file.getName().equals(dataFileName)) {
					try {
						FileInputStream inputStream = new FileInputStream(file);
						restoreFactoryHierarchy(factoryHandle, inputStream);
						inputStream.close();
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error while restoring hierarchy data from file: '" + file.getAbsolutePath() + "'", e);
					}
					break;
				}
			}
		}
	}
	
	@Override
	public void removeTradingAgentFactory(ITradingAgentFactory factory) {
		TradingAgentFactoryHandle factoryHandle = new TradingAgentFactoryHandle(factory.getClass().getName(), factory.getConfigurationKeys());
		persistFactoryHierarchy(factoryHandle);
		super.removeTradingAgentFactory(factory);
	}

	public void saveState() {
		for (TradingAgentFactoryHandle factoryHandle : factoriesByHandle.keySet()) {
			persistFactoryHierarchy(factoryHandle);
		}
	}

	private synchronized void persistFactoryHierarchy(TradingAgentFactoryHandle factoryHandle) {
		if (persistenceDirectory.exists()) {
			String fileName = persistenceDirectory.getAbsolutePath() + File.separator + factoryHandle.getPrettyName() + ".hierarchy";
			try {
				FileOutputStream outputStream = new FileOutputStream(fileName);
				persistFactoryHierarchy(factoryHandle, outputStream);
				outputStream.close();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error while persisting hierarchy data to file: '" + fileName + "'", e);
			}
		}
	}
	
	private void persistFactoryHierarchy(TradingAgentFactoryHandle factoryHandle, OutputStream outputStream) throws IOException {
		Map<IHierarchyItemHandle, HierarchyItem> factoryHierarchy = new HashMap<IHierarchyItemHandle, HierarchyItem>();
		Map<TradingAgentConfigurationHandle, Properties> factoryConfigurationByHandle = new HashMap<TradingAgentConfigurationHandle, Properties>();
		Map<TradingAgentBindingHandle, BindingPersistentData> factoryBindingByHandle = new HashMap<TradingAgentBindingHandle, BindingPersistentData>();
		Map<TradingAgentExecutionHandle, ExecutionPersistentData> factoryExecutionByHandle = new HashMap<TradingAgentExecutionHandle, ExecutionPersistentData>();
		HierarchyItem factoryHierarchyItem = hierarchy.get(factoryHandle);
		for (IHierarchyItemHandle configurationHandle : factoryHierarchyItem.children) {
			HierarchyItem configurationInfo = hierarchy.get(configurationHandle);
			factoryHierarchy.put(configurationHandle, configurationInfo);
			factoryConfigurationByHandle.put((TradingAgentConfigurationHandle) configurationHandle, configurationByHandle.get(configurationHandle).getProperties());
			for (IHierarchyItemHandle bindingHandle : configurationInfo.children) {
				HierarchyItem bindingInfo = hierarchy.get(bindingHandle);
				factoryHierarchy.put(bindingHandle, bindingInfo);
				TradingAgentBinding binding = bindingByHandle.get(bindingHandle);
				BindingPersistentData bindingData = new BindingPersistentData();
				bindingData.configurationHandle = configurationHandle;
				bindingData.inputSeriesNames = encodeInputSeriesMap(binding.getInputSeries());
				factoryBindingByHandle.put((TradingAgentBindingHandle) bindingHandle, bindingData);
				for (IHierarchyItemHandle executionHandle : bindingInfo.children) {
					HierarchyItem executionInfo = hierarchy.get(executionHandle);
					factoryHierarchy.put(executionHandle, executionInfo);
					ITradingAgentExecution execution = executionByHandle.get(executionHandle);
					ExecutionPersistentData executionData = new ExecutionPersistentData();
					executionData.inputSeriesNames = encodeInputSeriesMap(execution.getInput());
					executionData.outputSeries = execution.getOutput();
					factoryExecutionByHandle.put((TradingAgentExecutionHandle) executionHandle, executionData);
				}
			}
		}
		ObjectOutputStream oos = new ObjectOutputStream(outputStream);
		oos.writeObject(factoryConfigurationByHandle);
		oos.writeObject(factoryBindingByHandle);
		oos.writeObject(factoryExecutionByHandle);
		oos.writeObject(factoryHierarchy);
		oos.close();
	}
	
	@SuppressWarnings("unchecked")
	private void restoreFactoryHierarchy(TradingAgentFactoryHandle factoryHandle, InputStream inputStream) throws IOException, ClassNotFoundException {
		Map<IHierarchyItemHandle, IHierarchyItemHandle> handleIdentities = new HashMap<IHierarchyItemHandle, IHierarchyItemHandle>();
		ObjectInputStream ois = new ObjectInputStream(inputStream);
		
		Map<TradingAgentConfigurationHandle, Properties> factoryConfigurationByHandle = (Map<TradingAgentConfigurationHandle, Properties>) ois.readObject();
		Map<TradingAgentBindingHandle, BindingPersistentData> factoryBindingByHandle = (Map<TradingAgentBindingHandle, BindingPersistentData>) ois.readObject();
		Map<TradingAgentExecutionHandle, ExecutionPersistentData> factoryExecutionByHandle = (Map<TradingAgentExecutionHandle, ExecutionPersistentData>) ois.readObject();
		Map<IHierarchyItemHandle, HierarchyItem> factoryHierarchy = (Map<IHierarchyItemHandle, HierarchyItem>) ois.readObject();
		
		HierarchyItem factoryHierarchyItem = hierarchy.get(factoryHandle);
		handleIdentities.put(factoryHandle, factoryHandle);
		
		ITradingAgentFactory factory = factoriesByHandle.get(factoryHandle);
		for (Map.Entry<TradingAgentConfigurationHandle, Properties> entry : factoryConfigurationByHandle.entrySet()) {
			configurationByHandle.put(entry.getKey(), new TradingAgentConfiguration(factory, entry.getValue()));
			factoryHierarchyItem.children.add(entry.getKey());
			handleIdentities.put(entry.getKey(), entry.getKey());
		}
		
		for (Map.Entry<TradingAgentBindingHandle, BindingPersistentData> entry : factoryBindingByHandle.entrySet()) {
			TradingAgentConfiguration configuration = configurationByHandle.get(entry.getValue().configurationHandle);
			TradingAgentBinding binding = new TradingAgentBinding(configuration, decodeInputSeriesMap(entry.getValue().inputSeriesNames));
			bindingByHandle.put(entry.getKey(), binding);
			handleIdentities.put(entry.getKey(), entry.getKey());
		}
		
		for (Map.Entry<TradingAgentExecutionHandle, ExecutionPersistentData> entry : factoryExecutionByHandle.entrySet()) {
			DummyTradingAgentExecution execution = new DummyTradingAgentExecution();
			execution.wire(decodeInputSeriesMap(entry.getValue().inputSeriesNames), (ISeriesAugmentable<Date, Double, ISeriesPoint<Date, Double>>) entry.getValue().outputSeries);
			executionByHandle.put(entry.getKey(), execution);
			handleIdentities.put(entry.getKey(), entry.getKey());
		}
		
		for (Map.Entry<IHierarchyItemHandle, HierarchyItem> entry : factoryHierarchy.entrySet()) {
			IHierarchyItemHandle key = handleIdentities.get(entry.getKey());
			HierarchyItem item = entry.getValue();
			item.parent = handleIdentities.get(item.parent); // substitute 'copy' handle with 'real' handle
			CopyOnWriteArrayList<IHierarchyItemHandle> children = new CopyOnWriteArrayList<IHierarchyItemHandle>();
			for (IHierarchyItemHandle child : item.children) {
				children.add(handleIdentities.get(child)); // add 'real' handle instead of 'copy' handle
			}
			item.children = children;
			hierarchy.put(key, item);
		}
	}
	
	private Map<String, String> encodeInputSeriesMap(Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> inputSeriesMap) {
		Map<String, String> inputMap = new HashMap<String, String>();
		for (Map.Entry<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> seriesEntry : inputSeriesMap.entrySet()) {
			inputMap.put(seriesEntry.getKey(), seriesEntry.getValue().getPersistentID());
		}
		return inputMap;
	}
	
	private Map<String, ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> decodeInputSeriesMap(Map<String, String> inputMap) { 
		Map<String, ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> inputSeriesMap = new HashMap<String, ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>>();
		for (Map.Entry<String, String> inputNameEntry : inputMap.entrySet()) {
			ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> inputSeries = marketDataManager.getSeries(inputNameEntry.getValue());
			inputSeriesMap.put(inputNameEntry.getKey(), inputSeries);
		}
		return inputSeriesMap;
	}
}
