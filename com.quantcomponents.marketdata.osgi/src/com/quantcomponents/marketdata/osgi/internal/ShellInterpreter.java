/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata.osgi.internal;

import com.quantcomponents.marketdata.IMarketDataManager;
import com.quantcomponents.marketdata.IMarketDataProvider;

public class ShellInterpreter {
	private IMarketDataProvider marketDataProvider;
	private IMarketDataManager marketDataManager;
	
	public void setMarketDataProvider(IMarketDataProvider marketDataProvider) {
		this.marketDataProvider = marketDataProvider;
	}

	public void setMarketDataManager(IMarketDataManager marketDataManager) {
		this.marketDataManager = marketDataManager;
	}
	
	public IMarketDataProvider marketdataprovider() {
		return marketDataProvider;
	}

	public IMarketDataManager marketdatamanager() {
		return marketDataManager;
	}

}
