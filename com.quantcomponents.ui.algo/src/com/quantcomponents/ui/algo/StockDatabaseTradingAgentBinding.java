/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ui.algo;

import java.util.HashMap;
import java.util.Map;

import com.quantcomponents.marketdata.IStockDatabase;

public class StockDatabaseTradingAgentBinding {
	private final Map<String, IStockDatabase> stockDatabases = new HashMap<String, IStockDatabase>();

	public StockDatabaseTradingAgentBinding(String[] inputNames) {
		for (String inputName : inputNames) {
			stockDatabases.put(inputName, null);
		}
	}
	
	public Map<String, IStockDatabase> getStockDatabases() {
		return stockDatabases;
	}
}
