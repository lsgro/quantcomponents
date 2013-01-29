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

import java.util.Date;
import java.util.Map;

import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * 
 * Interface to manage algorithm input bindings
 * @see ITradingHierarchyManager
 */
public interface ITradingAgentBindingManager {
	/**
	 * Creates an algorithm binding to input series, and returns a handle to it
	 * @param tradingAgentConfigurationHandle handle to algorithm configuration
	 * @param inputSeries the input series to the algorithm instance, mapped to the input names found in {@link com.quantcomponents.core.model.ISeriesProcessorFactory#getInputSeriesNames()}
	 * @param name a readable name to be shown in the UI
	 * @return a handle to the binding created
	 */
	TradingAgentBindingHandle createBinding(TradingAgentConfigurationHandle tradingAgentConfigurationHandle, Map<String, ? extends ISeries<Date, Double, ISeriesPoint<Date, Double>>> inputSeries, String name);
	/**
	 * Returns the input series of the binding as set in the previous method
	 * @param bindingHandle the handle to the algorithm binding
	 * @return a map of input series to their names
	 */
	Map<String, ISeries<Date, Double, ISeriesPoint<Date, Double>>> getBindingInputSeries(TradingAgentBindingHandle bindingHandle);
	/**
	 * Remove a binding
	 * @param bindingHandle the handle to the binding to be removed
	 */
	void removeBinding(TradingAgentBindingHandle bindingHandle);
}
