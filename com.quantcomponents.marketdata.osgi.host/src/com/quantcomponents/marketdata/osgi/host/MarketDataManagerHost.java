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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.remote.ITaskMonitorHost;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.core.remote.TaskMonitorProxy;
import com.quantcomponents.marketdata.IMarketDataManager;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.marketdata.osgi.IMarketDataManagerHost;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHost;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHostLocal;

public class MarketDataManagerHost implements IMarketDataManagerHost {
	
	protected static class ServiceInfo {
		IStockDatabase stockDb;
		ServiceHandle<IStockDatabaseHost> handle;
	}
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(MarketDataManagerHost.class.getName());
	private final Map<IStockDatabase, ServiceInfo> databaseInfoByStockDb = new ConcurrentHashMap<IStockDatabase, ServiceInfo>();
	private final Map<ServiceHandle<IStockDatabaseHost>, ServiceInfo> databaseInfoByHandle = new ConcurrentHashMap<ServiceHandle<IStockDatabaseHost>, ServiceInfo>();
	private volatile IMarketDataManager marketDataManager;
	private volatile IStockDatabaseHostLocal stockDatabaseHost;
	private volatile ITaskMonitorHost taskMonitorHost;
	
	public MarketDataManagerHost() {}

	public MarketDataManagerHost(IMarketDataManager dataManagerService, IStockDatabaseHostLocal stockDatabaseHost, ITaskMonitorHost taskMonitorHost) {
		this.marketDataManager = dataManagerService;
		this.stockDatabaseHost = stockDatabaseHost;
		this.taskMonitorHost = taskMonitorHost;
	}

	public void activate() {
		publishAllStockDbHosts();
	}
	
	public void deactivate() {
		databaseInfoByStockDb.clear();
		databaseInfoByHandle.clear();
	}
	
	public void setDataManagerService(IMarketDataManager dataManagerService) {
		this.marketDataManager = dataManagerService;
	}

	public void setStockDatabaseHost(IStockDatabaseHostLocal stockDatabaseHost) {
		this.stockDatabaseHost = stockDatabaseHost;
	}

	public void setTaskMonitorHost(ITaskMonitorHost taskMonitorHost) {
		this.taskMonitorHost = taskMonitorHost;
	}

	public void resetTaskMonitorHost(ITaskMonitorHost taskMonitorHost) {
		if (this.taskMonitorHost == taskMonitorHost) {
			this.taskMonitorHost = null;
		}
	}

	@Override
	public String getPrettyName() {
		return getMarketDataManager().getPrettyName();
	}

	public List<IContract> searchContracts(IContract criteria, ServiceHandle<ITaskMonitorHost> taskMonitorHandle) throws ConnectException, RequestFailedException {
		return getMarketDataManager().searchContracts(criteria, taskMonitorHost == null || taskMonitorHandle == null ? null : new TaskMonitorProxy(taskMonitorHost, taskMonitorHandle));
	}

	@Override
	public Collection<ServiceHandle<IStockDatabaseHost>> getAllStockDatabases() {
		Collection<ServiceHandle<IStockDatabaseHost>> allStockDbHandles = new ArrayList<ServiceHandle<IStockDatabaseHost>>();
		for (ServiceInfo serviceInfo : databaseInfoByHandle.values()) {
			allStockDbHandles.add(serviceInfo.handle);
		}
		return allStockDbHandles;
	}

	@Override
	public ServiceHandle<IStockDatabaseHost> findStockDatabase(IContract contract, DataType dataType, BarSize barSize, Boolean includeAfterHours) {
		ServiceHandle<IStockDatabaseHost> serviceHandle = null;
		IStockDatabase stockDatabase = getMarketDataManager().findStockDatabase(contract, dataType, barSize, includeAfterHours);
		if (stockDatabase != null) {
			serviceHandle = publishStockDbHost(stockDatabase);
		}
		return serviceHandle;
	}

	@Override
	public ServiceHandle<IStockDatabaseHost> getStockDatabase(String ID) throws ConnectException, RequestFailedException {
		ServiceHandle<IStockDatabaseHost> serviceHandle = null;
		IStockDatabase stockDatabase = getMarketDataManager().getStockDatabase(ID);
		if (stockDatabase != null) {
			serviceHandle = publishStockDbHost(stockDatabase);
		}
		return serviceHandle;
	}

	@Override
	public ServiceHandle<IStockDatabaseHost> createStockDatabase(IContract contract, DataType dataType, BarSize barSize, boolean includeAfterHours, TimeZone timeZone) {
		IStockDatabase stockDb = getMarketDataManager().createStockDatabase(contract, dataType, barSize, includeAfterHours, timeZone);
		return publishStockDbHost(stockDb);
	}

	@Override
	public void removeStockDatabase(ServiceHandle<IStockDatabaseHost> stockDbHostHandle) throws ConnectException, RequestFailedException {
		ServiceInfo serviceInfo;
		synchronized (this) {
			serviceInfo = databaseInfoByHandle.remove(stockDbHostHandle);
			if (serviceInfo != null) {
				databaseInfoByStockDb.remove(serviceInfo.stockDb);
			} else {
				throw new IllegalArgumentException("Handle refers to a stock database not managed: " + stockDbHostHandle);
			}
		}
		getMarketDataManager().removeStockDatabase(serviceInfo.stockDb);
	}

	@Override
	public void fillHistoricalData(ServiceHandle<IStockDatabaseHost> stockDbHostHandle, Date startDate, Date endDate, ServiceHandle<ITaskMonitorHost> taskMonitorHandle) throws ConnectException,
			RequestFailedException {
		ServiceInfo serviceInfo = retrieveServiceInfo(stockDbHostHandle);
		getMarketDataManager().fillHistoricalData(serviceInfo.stockDb, startDate, endDate, taskMonitorHost == null || taskMonitorHandle == null ? null : new TaskMonitorProxy(taskMonitorHost, taskMonitorHandle));
	}

	private void publishAllStockDbHosts() {
		Collection<IStockDatabase> allStockDb = getMarketDataManager().allStockDatabases();
		for (IStockDatabase stockDb : allStockDb) {
			publishStockDbHost(stockDb);
		}
	}
	
	private synchronized ServiceHandle<IStockDatabaseHost> publishStockDbHost(IStockDatabase stockDb) {
		ServiceInfo serviceInfo = databaseInfoByStockDb.get(stockDb);
		if (serviceInfo == null) {
			serviceInfo = new ServiceInfo();
			serviceInfo.stockDb = stockDb;
			serviceInfo.handle = stockDatabaseHost.addStockDatabase(stockDb);;
			databaseInfoByHandle.put(serviceInfo.handle, serviceInfo);
			databaseInfoByStockDb.put(stockDb, serviceInfo);
		}
		return serviceInfo.handle;
	}
	
	protected ServiceInfo retrieveServiceInfo(ServiceHandle<IStockDatabaseHost> stockDbHostHandle) {
		ServiceInfo serviceInfo = databaseInfoByHandle.get(stockDbHostHandle);
		if (serviceInfo == null) {
			throw new IllegalArgumentException("Handle refers to a stock database not managed: " + stockDbHostHandle);
		}
		return serviceInfo;
	}

	protected IMarketDataManager getMarketDataManager() {
		return marketDataManager;
	}
	
	@Override
	public int numberOfStockDatabases() {
		return databaseInfoByHandle.size();
	}
}
