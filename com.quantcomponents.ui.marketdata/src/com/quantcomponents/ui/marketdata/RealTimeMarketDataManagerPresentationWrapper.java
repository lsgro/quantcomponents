/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ui.marketdata;

import java.net.ConnectException;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.IPrettyNamed;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.marketdata.IMarketDataManager;
import com.quantcomponents.marketdata.IRealTimeMarketDataManager;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.ui.core.IMonitorableContainer;
import com.quantcomponents.ui.core.IMutableMonitorableContainer;

public class RealTimeMarketDataManagerPresentationWrapper extends MarketDataManagerPresentationWrapper implements IRealTimeMarketDataManager, IPrettyNamed, IMonitorableContainer<StockDatabasePresentationWrapper> {	
	public RealTimeMarketDataManagerPresentationWrapper(IRealTimeMarketDataManager manager, IMutableMonitorableContainer<IMarketDataManager, MarketDataManagerPresentationWrapper> parent) {
		super(manager, parent);
	}

	public IRealTimeMarketDataManager getMarketDataManager() {
		return (IRealTimeMarketDataManager) getMarketDataManager();
	}

	@Override
	public void startRealtimeUpdate(IStockDatabase stockDb, boolean fillHistoricalGap, ITaskMonitor taskMonitor) throws ConnectException,
			RequestFailedException, UnsupportedOperationException {
		if (stockDb instanceof StockDatabasePresentationWrapper) {
			stockDb = ((StockDatabasePresentationWrapper) stockDb).getInner();
		}
		getMarketDataManager().startRealtimeUpdate(stockDb, fillHistoricalGap, taskMonitor);
	}

	@Override
	public void stopRealtimeUpdate(IStockDatabase stockDb) throws ConnectException, RequestFailedException, UnsupportedOperationException {
		if (stockDb instanceof StockDatabasePresentationWrapper) {
			stockDb = ((StockDatabasePresentationWrapper) stockDb).getInner();
		}
		getMarketDataManager().stopRealtimeUpdate(stockDb);
	}

	@Override
	public boolean isRealtimeUpdate(IStockDatabase stockDb) throws ConnectException, RequestFailedException, UnsupportedOperationException {
		if (stockDb instanceof StockDatabasePresentationWrapper) {
			stockDb = ((StockDatabasePresentationWrapper) stockDb).getInner();
		}
		return getMarketDataManager().isRealtimeUpdate(stockDb);
	}
}
