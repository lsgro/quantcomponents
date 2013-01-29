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
 * Handle to {@link TradingAgentConfiguration}
 * @see ITradingHierarchyManager
 */
public class TradingAgentConfigurationHandle extends HierarchyItemHandle {
	private static final long serialVersionUID = -2179624045097121172L;
	private final String[] inputSeriesNames;
	
	public TradingAgentConfigurationHandle(String name, String[] inputSeriesNames) {
		super(name);
		this.inputSeriesNames = inputSeriesNames;
	}

	public String[]  getInputSeriesNames() {
		return inputSeriesNames;
	}

}
