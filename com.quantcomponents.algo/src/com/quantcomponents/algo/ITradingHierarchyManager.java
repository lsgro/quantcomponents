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
 * Manager of the algorithm hierarchy.
 * The algorithm hierarchy is composed by four levels:
 * <ul>
 * <li>An {@link ITradingAgentFactory}, that corresponds to an algorithm family, accessed by means of {@link TradingAgentFactoryHandle}</li>
 * <li>Several {@link TradingAgentConfiguration}, corresponding to the specific algorithm, created by
 * applying a specific configuration to an algorith family, accessed by means of {@link TradingAgentConfigurationHandle}</li>
 * <li>Several {@link TradingAgentBinding}, that connect a specific algorithm to a set of inputs, accessed by means of {@link TradingAgentBindingHandle}</li>
 * <li>A set of {@link ITradingAgentExecution}, corresponding to specific runs of the algorithm bound to a set of inputs, accessed by means of {@link TradingAgentExecutionHandle}</li>
 * </ul>
 * All the mentioned types are opaque, and they are not meant to be used directly.
 * This service lets callers browse the entity hierarchy without direct access, through the use
 * of handles.
 * Creation, removal and use of the entities is done through other interfaces:
 * @see IHierarchyItemHandle
 * @see ITradingFactoryManager
 * @see ITradingAgentConfigurationManager
 * @see ITradingAgentBindingManager
 * @see ITradingAgentExecutionManager
 */
public interface ITradingHierarchyManager {
	/**
	 * Retrieves the configuration handles belonging to a factory
	 * @return a (possibly empty) collection of configurations. This collection is a snapshot, and it must not be used
	 * to add/remove configurations.
	 */
	Collection<TradingAgentConfigurationHandle> getChildren(TradingAgentFactoryHandle tradingAgentFactoryHandle);
	/**
	 * Retrieves the factory to which this configuration belongs
	 */
	TradingAgentFactoryHandle getParent(TradingAgentConfigurationHandle tradingAgentConfigurationHandle);
	/**
	 * Retrieves the binding handles belonging to a configuration
	 * @return a (possibly empty) collection of bindings. This collection is a snapshot, and it must not be used
	 * to add/remove bindings.
	 */
	Collection<TradingAgentBindingHandle> getChildren(TradingAgentConfigurationHandle tradingAgentConfigurationHandle);
	/**
	 * Retrieves the configuration to which this binding belongs
	 */
	TradingAgentConfigurationHandle getParent(TradingAgentBindingHandle tradingAgentBindingHandle);
	/**
	 * Retrieves the execution handles belonging to a binding
	 * @return a (possibly empty) collection of executions. This collection is a snapshot, and it must not be used
	 * to add/remove executions.
	 */
	Collection<TradingAgentExecutionHandle> getChildren(TradingAgentBindingHandle tradingAgentBindingHandle);
	/**
	 * Retrieves the binding to which this execution belongs
	 */
	TradingAgentBindingHandle getParent(TradingAgentExecutionHandle TradingAgentExecutionHandle);
}
