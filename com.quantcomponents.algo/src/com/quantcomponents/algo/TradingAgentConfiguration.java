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

import java.io.Serializable;
import java.util.Properties;

/**
 * Trading algorithm completely configured
 */
public class TradingAgentConfiguration implements Serializable {
	private static final long serialVersionUID = 8922000346719818134L;
	private final ITradingAgentFactory factory;
	private final Properties properties;
	
	public TradingAgentConfiguration(ITradingAgentFactory factory, Properties properties) {
		this.factory = factory;
		this.properties = properties;
	}

	public ITradingAgentFactory getFactory() {
		return factory;
	}

	public Properties getProperties() {
		return properties;
	}
	
	public ITradingAgent newTradingAgent() {
		return getFactory().createProcessor(getProperties());
	}
	
}
