/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata.osgi.proxy;

import java.net.ConnectException;
import java.util.Map;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.IPrettyNamed;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.core.remote.ITaskMonitorHostLocal;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IRealTimeMarketDataManager;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.marketdata.osgi.IMarketDataManagerHost;
import com.quantcomponents.marketdata.osgi.IRealTimeMarketDataManagerHost;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHandleMap;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHost;
import com.quantcomponents.marketdata.osgi.IStockDatabaseProxyFactory;

public class RealTimeMarketDataManagerProxy extends MarketDataManagerProxy implements IRealTimeMarketDataManager, IStockDatabaseHandleMap, IPrettyNamed {
	public RealTimeMarketDataManagerProxy() {}

	public RealTimeMarketDataManagerProxy(IRealTimeMarketDataManagerHost dataManagerHost, ITaskMonitorHostLocal taskMonitorHost, IStockDatabaseProxyFactory stockDatabaseProxyFactory) {
		super(dataManagerHost, taskMonitorHost, stockDatabaseProxyFactory);
	}
	
	public void setDataManagerHost(IMarketDataManagerHost dataManagerHost, Map<?,?> properties) {
		IRealTimeMarketDataManagerHost.class.cast(dataManagerHost); 
		super.setDataManagerHost(dataManagerHost, properties);
	}

	protected IRealTimeMarketDataManagerHost getMarketDataManagerHost() {
		return (IRealTimeMarketDataManagerHost) super.getMarketDataManagerHost();
	}
	
	@Override
	public void startRealtimeUpdate(IStockDatabase stockDb, boolean fillHistoricalGap, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		ServiceHandle<IStockDatabaseHost> stockDbHandle = stockDbHandlesByStockDb.get(stockDb);
		if (stockDbHandle == null) {
			throw new IllegalArgumentException("Stock database not found: " + stockDb);
		}
		getMarketDataManagerHost().startRealtimeUpdate(stockDbHandle, fillHistoricalGap,  taskMonitor == null ? null : taskMonitorHost.addTaskMonitor(taskMonitor));
	}

	@Override
	public void stopRealtimeUpdate(IStockDatabase stockDb) throws ConnectException, RequestFailedException {
		getMarketDataManagerHost().stopRealtimeUpdate(retrieveStockDatabaseHandle(stockDb));
	}

	@Override
	public boolean isRealtimeUpdate(IStockDatabase stockDb) throws ConnectException, RequestFailedException {
		return getMarketDataManagerHost().isRealtimeUpdate(retrieveStockDatabaseHandle(stockDb));
	}
}
