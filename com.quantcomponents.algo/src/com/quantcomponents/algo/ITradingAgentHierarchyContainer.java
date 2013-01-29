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

import java.util.Collection;

/**
 * Container for trading algorithm hierarchy.
 * Used internally to decouple the {@link ITradingManager} from the storage of the algo trading hierarchy
 * Implementations can use database or other means of providing persistence
 * @see ITradingManager
 */
public interface ITradingAgentHierarchyContainer {
	void addTradingAgentFactory(ITradingAgentFactory factory);
	void removeTradingAgentFactory(ITradingAgentFactory factory);
	Collection<TradingAgentFactoryHandle> getAllTradingAgentFactories();
	ITradingAgentFactory getTradingAgentFactory(TradingAgentFactoryHandle handle);
	Collection<TradingAgentConfigurationHandle> getChildrenHandles(TradingAgentFactoryHandle handle);
	void putTradingAgentConfiguration(TradingAgentConfigurationHandle handle, TradingAgentConfiguration tradingAgentConfiguration, TradingAgentFactoryHandle parentHandle);
	TradingAgentConfiguration getTradingAgentConfiguration(TradingAgentConfigurationHandle handle);
	Collection<TradingAgentBindingHandle> getChildrenHandles(TradingAgentConfigurationHandle handle);
	TradingAgentFactoryHandle getParentHandle(TradingAgentConfigurationHandle handle);
	TradingAgentConfiguration removeTradingAgentConfiguration(TradingAgentConfigurationHandle handle);
	void putTradingAgentBinding(TradingAgentBindingHandle handle, TradingAgentBinding tradingAgentBinding, TradingAgentConfigurationHandle parentHandle);
	TradingAgentBinding getTradingAgentBinding(TradingAgentBindingHandle handle);
	Collection<TradingAgentExecutionHandle> getChildrenHandles(TradingAgentBindingHandle handle);
	TradingAgentConfigurationHandle getParentHandle(TradingAgentBindingHandle handle);
	TradingAgentBinding removeTradingAgentBinding(TradingAgentBindingHandle handle);
	void putTradingAgentExecution(TradingAgentExecutionHandle handle, ITradingAgentExecution tradingAgentExecution, TradingAgentBindingHandle parentHandle);
	ITradingAgentExecution getTradingAgentExecution(TradingAgentExecutionHandle handle);
	TradingAgentBindingHandle getParentHandle(TradingAgentExecutionHandle handle);
	ITradingAgentExecution removeTradingAgentExecution(TradingAgentExecutionHandle handle);
}
