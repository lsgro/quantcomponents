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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.IPrettyNamed;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.core.remote.ITaskMonitorHostLocal;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IMarketDataManager;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.marketdata.osgi.IMarketDataManagerHost;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHandleMap;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHost;
import com.quantcomponents.marketdata.osgi.IStockDatabaseProxyFactory;

public class MarketDataManagerProxy implements IMarketDataManager, IStockDatabaseHandleMap, IPrettyNamed {
	private static final Logger logger = Logger.getLogger(MarketDataManagerProxy.class.getName());
	protected final Map<IStockDatabase, ServiceHandle<IStockDatabaseHost>> stockDbHandlesByStockDb = new ConcurrentHashMap<IStockDatabase, ServiceHandle<IStockDatabaseHost>>();
	protected final Map<ServiceHandle<IStockDatabaseHost>, IStockDatabase> stockDbProxiesByStockDbHandle = new ConcurrentHashMap<ServiceHandle<IStockDatabaseHost>, IStockDatabase>();
	protected volatile ITaskMonitorHostLocal taskMonitorHost;
	protected volatile IStockDatabaseProxyFactory stockDatabaseProxyFactory;
	private volatile IMarketDataManagerHost dataManagerHost;
	
	public MarketDataManagerProxy() {}

	public MarketDataManagerProxy(IMarketDataManagerHost dataManagerHost, ITaskMonitorHostLocal taskMonitorHost, IStockDatabaseProxyFactory stockDatabaseProxyFactory) {
		this.dataManagerHost = dataManagerHost;
		this.taskMonitorHost = taskMonitorHost;
		this.stockDatabaseProxyFactory = stockDatabaseProxyFactory;
	}
	
	public void deactivate() {
		stockDbHandlesByStockDb.clear();
		stockDbProxiesByStockDbHandle.clear();
	}
	
	public void setDataManagerHost(IMarketDataManagerHost dataManagerHost, Map<?,?> properties) {
		this.dataManagerHost = dataManagerHost;
	}

	protected IMarketDataManagerHost getMarketDataManagerHost() {
		return dataManagerHost;
	}
	
	public void setTaskMonitorHost(ITaskMonitorHostLocal taskMonitorHost) {
		this.taskMonitorHost = taskMonitorHost;
	}

	public void setStockDatabaseProxyFactory(IStockDatabaseProxyFactory stockDatabaseProxyFactory) {
		this.stockDatabaseProxyFactory = stockDatabaseProxyFactory;
	}

	@Override
	public String getPrettyName() {
		return getMarketDataManagerHost().getPrettyName();
	}

	@Override
	public List<IContract> searchContracts(IContract criteria, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		return getMarketDataManagerHost().searchContracts(criteria, taskMonitor == null ? null : taskMonitorHost.addTaskMonitor(taskMonitor));
	}

	@Override
	public Collection<IStockDatabase> allStockDatabases() {
		Collection<IStockDatabase> result = new ArrayList<IStockDatabase>();
		for (ServiceHandle<IStockDatabaseHost> handle : getMarketDataManagerHost().getAllStockDatabases()) {
			result.add(getOrCreateStockDbProxy(handle));
		}
		return result;
	}

	@Override
	public IStockDatabase findStockDatabase(IContract contract, DataType dataType, BarSize barSize, Boolean includeAfterHours) {
		ServiceHandle<IStockDatabaseHost> stockDbHandle = getMarketDataManagerHost().findStockDatabase(contract, dataType, barSize, includeAfterHours);
		if (stockDbHandle != null) {
			return getOrCreateStockDbProxy(stockDbHandle);
		}
		return null;
	}

	@Override
	public IStockDatabase getStockDatabase(String ID) {
		ServiceHandle<IStockDatabaseHost> stockDbHandle = null;
		try {
			stockDbHandle = getMarketDataManagerHost().getStockDatabase(ID);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error while retrieving stock DB handle from host for ID: " + ID, e);
		} 
		if (stockDbHandle != null) {
			return getOrCreateStockDbProxy(stockDbHandle);
		}
		return null;
	}

	@Override
	public ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> getSeries(String ID) {
		return getStockDatabase(ID).getVirtualTimeSeries();
	}
	
	@Override
	public IStockDatabase createStockDatabase(IContract contract, DataType dataType, BarSize barSize, boolean includeAfterHours, TimeZone timeZone) {
		ServiceHandle<IStockDatabaseHost> stockDbHandle = getMarketDataManagerHost().createStockDatabase(contract, dataType, barSize, includeAfterHours, timeZone);
		return getOrCreateStockDbProxy(stockDbHandle);
	}

	@Override
	public void removeStockDatabase(IStockDatabase stockDb) throws ConnectException, RequestFailedException {
		ServiceHandle<IStockDatabaseHost> stockDbHandle = stockDbHandlesByStockDb.remove(stockDb);
		if (stockDbHandle == null) {
			throw new IllegalArgumentException("Stock database not found: " + stockDb);
		}
		getMarketDataManagerHost().removeStockDatabase(stockDbHandle);
		stockDbProxiesByStockDbHandle.remove(stockDbHandle);
	}

	@Override
	public void fillHistoricalData(IStockDatabase stockDb, Date startDate, Date endDate, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		ServiceHandle<IStockDatabaseHost> stockDbHandle = stockDbHandlesByStockDb.get(stockDb);
		if (stockDbHandle == null) {
			throw new IllegalArgumentException("Stock database not found: " + stockDb);
		}
		getMarketDataManagerHost().fillHistoricalData(stockDbHandle, startDate, endDate,  taskMonitor == null ? null : taskMonitorHost.addTaskMonitor(taskMonitor));
	}

	protected ServiceHandle<IStockDatabaseHost> retrieveStockDatabaseHandle(IStockDatabase stockDb) {
		ServiceHandle<IStockDatabaseHost> stockDbHandle = stockDbHandlesByStockDb.get(stockDb);
		if (stockDbHandle == null) {
			throw new IllegalArgumentException("Stock database not found: " + stockDb);
		}
		return stockDbHandle;
	}
	
	private IStockDatabase getOrCreateStockDbProxy(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		IStockDatabase proxy = stockDbProxiesByStockDbHandle.get(stockDbHandle);
		if (proxy == null) {
			proxy = stockDatabaseProxyFactory.createStockDatabaseProxy(stockDbHandle);
			stockDbHandlesByStockDb.put(proxy, stockDbHandle);
			stockDbProxiesByStockDbHandle.put(stockDbHandle, proxy);
		}
		return proxy;
	}

	@Override
	public int numberOfStockDatabases() {
		return getMarketDataManagerHost().numberOfStockDatabases();
	}

	@Override
	public ServiceHandle<IStockDatabaseHost> getHandleByStockDb(IStockDatabase stockDb) {
		return retrieveStockDatabaseHandle(stockDb);
	}

	@Override
	public IStockDatabase getStockDbByHandle(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		return stockDbProxiesByStockDbHandle.get(stockDbHandle);
	}
}
