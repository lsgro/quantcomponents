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
import java.util.Properties;

/**
 * 
 * Interface to manage trading algorithm configurations
 * @see ITradingHierarchyManager
 */
public interface ITradingAgentConfigurationManager {
	/**
	 * Validation method for an algorithm configuration
	 * @param factoryHandle handle to the algorithm factory
	 * @param configuration configuration to be validated
	 * @param messages empty map to be filled with error messages, in case of error
	 * @return true if the configuration is correct, false otherwise
	 */
	boolean isConfigurationValid(TradingAgentFactoryHandle factoryHandle, Properties configuration, Map<String, String> messages);
	/**
	 * Creates an algorithm configuration and returns a handle to it
	 * @param factoryHandle the handle to the algorithm factory
	 * @param configuration the configuration
	 * @param name a readable name to be shown in the UI
	 * @return a handle to the created configuration
	 */
	TradingAgentConfigurationHandle createConfiguration(TradingAgentFactoryHandle factoryHandle, Properties configuration, String name);
	/**
	 * Returns the configuration properties as set in the previous method
	 * @param tradingAgentConfigurationHandle a handle to a configuration
	 * @return the properties of the configuration
	 */
	Properties getConfigurationProperties(TradingAgentConfigurationHandle tradingAgentConfigurationHandle);
	/**
	 * Removes a configuration
	 * @param tradingAgentConfigurationHandle the handle of the configuration to be removed
	 */
	void removeConfiguration(TradingAgentConfigurationHandle tradingAgentConfigurationHandle);
}
