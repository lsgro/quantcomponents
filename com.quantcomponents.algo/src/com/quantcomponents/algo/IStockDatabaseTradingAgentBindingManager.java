/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo;

import java.util.Map;

import com.quantcomponents.marketdata.IStockDatabase;

/**
 * Interface to manage algorithm binding, specific for strategies based on stocks
 * @see ITradingHierarchyManager
 * @see com.quantcomponents.marketdata.IStockDatabase
 */
public interface IStockDatabaseTradingAgentBindingManager {
	/**
	 * Binds a specific algorithm to a series of stock database inputs
	 * The input stock dabatases must be mapped by the input names found in {@link com.quantcomponents.core.model.ISeriesProcessorFactory#getInputSeriesNames()}
	 * @param tradingAgentConfigurationHandle handle of the configured algorithm
	 * @param inputStockDatabase the stock databases tagged by input name
	 * @param name a readable name to be shown in the UI
	 * @return a handle to the binding created 
	 */
	TradingAgentBindingHandle createBinding(TradingAgentConfigurationHandle tradingAgentConfigurationHandle, Map<String, IStockDatabase> inputStockDatabase, String name);
	/**
	 * Retrieves the input stock database from a binding
	 * @param bindingHandle the handle of the binding
	 * @return the map of input as creates in the previous method
	 */
	Map<String, IStockDatabase> getBindingInputStockDatabases(TradingAgentBindingHandle bindingHandle);
	/**
	 * Removes a binding
	 * @param bindingHandle the handle of the binding to remove
	 */
	void removeBinding(TradingAgentBindingHandle bindingHandle);
}
