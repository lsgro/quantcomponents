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
import java.util.Properties;

import com.quantcomponents.core.model.ISeriesProcessorFactory;

/**
 * 
 * Specialized {@link com.quantcomponents.core.model.ISeriesProcessorFactory} that creates trading algorithm instances
 */
public interface ITradingAgentFactory extends ISeriesProcessorFactory<Date, Double> {
	/**
	 * This methods accepts a configuration, and creates an instance of configured trading algorithm
	 */
	ITradingAgent createProcessor(Properties configuration);
}
