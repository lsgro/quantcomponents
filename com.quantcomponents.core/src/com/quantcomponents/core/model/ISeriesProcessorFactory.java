/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.model;

import java.util.Map;
import java.util.Properties;

/**
 * Factory for {@link ISeriesProcessor}.
 * The values from getInputSeriesNames() should be used as tags of the input
 * series when wiring the processor.
 * The values from getConfigurationKeys() must be used to provide the
 * configuration to the factory method. Configuration can be validated prior
 * to be used.
 *
 * @param <A> The Abscissa type
 * @param <O> The Ordinate type
 */
public interface ISeriesProcessorFactory<A extends Comparable<A>, O extends Comparable<O>> {
	/**
	 * The tags to be used to wire the input series via {@link ISeriesProcessor#wire}
	 * @return an array of String
	 */
	String[] getInputSeriesNames();
	/**
	 * The keys of configuration properties, to be used with the factory method {@link ISeriesProcessorFactory#createProcessor}
	 * @return an array of String
	 */
	String[] getConfigurationKeys();
	/**
	 * Validator method for a processor configuration.
	 * @param configuration The configuration to be validated
	 * @param messages An array of error messages, one for each problem with the configuration.
	 * @return true if the configuration is valid, false otherwise
	 */
	boolean isConfigurationValid(Properties configuration, Map<String, String> messages);
	/**
	 * Factory method for {@link ISeriesProcessor}.
	 * @param configuration the processor configuration
	 * @return a processor
	 */
	ISeriesProcessor<A, O> createProcessor(Properties configuration);
}
