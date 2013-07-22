/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata.osgi.host;

import java.net.ConnectException;
import java.util.logging.Logger;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.remote.ITaskMonitorHost;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.core.remote.TaskMonitorProxy;
import com.quantcomponents.marketdata.IMarketDataManager;
import com.quantcomponents.marketdata.IRealTimeMarketDataManager;
import com.quantcomponents.marketdata.osgi.IRealTimeMarketDataManagerHost;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHost;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHostLocal;

public class RealTimeMarketDataManagerHost extends MarketDataManagerHost implements IRealTimeMarketDataManagerHost {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RealTimeMarketDataManagerHost.class.getName());
	private volatile ITaskMonitorHost taskMonitorHost;
	
	public RealTimeMarketDataManagerHost() {}

	public RealTimeMarketDataManagerHost(IRealTimeMarketDataManager dataManagerService, IStockDatabaseHostLocal stockDatabaseHost, ITaskMonitorHost taskMonitorHost) {
		super(dataManagerService, stockDatabaseHost, taskMonitorHost);
	}

	@Override
	public void setDataManagerService(IMarketDataManager dataManagerService) {
		IRealTimeMarketDataManager.class.cast(dataManagerService); // ensure correct type of parameter
		super.setDataManagerService(dataManagerService);
	}


	@Override
	public void startRealtimeUpdate(ServiceHandle<IStockDatabaseHost> stockDbHostHandle, boolean fillHistoricalGap, ServiceHandle<ITaskMonitorHost> taskMonitorHandle) throws ConnectException,
			RequestFailedException {
		ServiceInfo serviceInfo = retrieveServiceInfo(stockDbHostHandle);
		getMarketDataManager().startRealtimeUpdate(serviceInfo.stockDb, fillHistoricalGap, taskMonitorHost == null || taskMonitorHandle == null ? null : new TaskMonitorProxy(taskMonitorHost, taskMonitorHandle));
	}

	@Override
	public void stopRealtimeUpdate(ServiceHandle<IStockDatabaseHost> stockDbHostHandle) throws ConnectException, RequestFailedException {
		ServiceInfo serviceInfo = retrieveServiceInfo(stockDbHostHandle);
		getMarketDataManager().stopRealtimeUpdate(serviceInfo.stockDb);
	}

	@Override
	public boolean isRealtimeUpdate(ServiceHandle<IStockDatabaseHost> stockDbHostHandle) throws ConnectException, RequestFailedException {
		ServiceInfo serviceInfo = retrieveServiceInfo(stockDbHostHandle);
		return getMarketDataManager().isRealtimeUpdate(serviceInfo.stockDb);
	}
	
	@Override
	protected IRealTimeMarketDataManager getMarketDataManager() {
		return (IRealTimeMarketDataManager) super.getMarketDataManager();
	}
}
