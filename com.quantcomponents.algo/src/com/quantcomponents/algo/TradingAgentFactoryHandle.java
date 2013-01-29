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


/**
 * Handle to {@link ITradingAgentFactory}
 * @see ITradingHierarchyManager
 */
public class TradingAgentFactoryHandle extends HierarchyItemHandle {
	private static final long serialVersionUID = -2511960730258606225L;
	
	private final String[] configurationKeys;

	public TradingAgentFactoryHandle(String name, String[] configurationKeys) {
		super(name, name);
		this.configurationKeys = configurationKeys;
	}

	public String[] getConfigurationKeys() {
		return configurationKeys;
	}
	
}
