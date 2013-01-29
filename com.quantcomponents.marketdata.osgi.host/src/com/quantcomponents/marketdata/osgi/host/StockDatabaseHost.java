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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.remote.IUIDGenerator;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.marketdata.osgi.IMutableOHLCTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.IMutableOHLCTimeSeriesHostLocal;
import com.quantcomponents.marketdata.osgi.IMutableTickTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.IOHLCTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.IOHLCTimeSeriesHostLocal;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHost;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHostLocal;
import com.quantcomponents.marketdata.osgi.ITickTimeSeriesHostLocal;

public class StockDatabaseHost implements IStockDatabaseHost, IStockDatabaseHostLocal {
	private static class StockDatabaseInfo {
		IStockDatabase stockDatabase;
		ServiceHandle<IStockDatabaseHost> handle;
		ServiceHandle<IMutableOHLCTimeSeriesHost> ohlcTimeSeriesHandle;
		ServiceHandle<IMutableTickTimeSeriesHost> tickTimeSeriesHandle;
		ServiceHandle<IOHLCTimeSeriesHost> virtualTimeSeriesHandle;
	}

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(StockDatabaseHost.class.getName());
	private final Map<ServiceHandle<IStockDatabaseHost>, StockDatabaseInfo> allStockDbs = new ConcurrentHashMap<ServiceHandle<IStockDatabaseHost>, StockDatabaseInfo>();
	private volatile IMutableOHLCTimeSeriesHostLocal ohlcTimeSeriesHost;
	private volatile ITickTimeSeriesHostLocal tickTimeSeriesHost;
	private volatile IOHLCTimeSeriesHostLocal virtualTimeSeriesHost;
	private volatile IUIDGenerator uidGenerator;

	public StockDatabaseHost() {}
	
	public StockDatabaseHost(IMutableOHLCTimeSeriesHostLocal ohlcTimeSeriesHost, ITickTimeSeriesHostLocal tickTimeSeriesHost,
			IOHLCTimeSeriesHostLocal virtualTimeSeriesHost, IUIDGenerator uidGenerator) {
		this.ohlcTimeSeriesHost = ohlcTimeSeriesHost;
		this.tickTimeSeriesHost = tickTimeSeriesHost;
		this.virtualTimeSeriesHost = virtualTimeSeriesHost;
		this.uidGenerator = uidGenerator;
	}

	public void deactivate() {
		allStockDbs.clear();
	}
	
	public void setOhlcTimeSeriesHost(IMutableOHLCTimeSeriesHostLocal ohlcTimeSeriesHost) {
		this.ohlcTimeSeriesHost = ohlcTimeSeriesHost;
	}

	public void setTickTimeSeriesHost(ITickTimeSeriesHostLocal tickTimeSeriesHost) {
		this.tickTimeSeriesHost = tickTimeSeriesHost;
	}

	public void setVirtualTimeSeriesHost(IOHLCTimeSeriesHostLocal virtualTimeSeriesHost) {
		this.virtualTimeSeriesHost = virtualTimeSeriesHost;
	}

	public void setUidGenerator(IUIDGenerator uidGenerator) {
		this.uidGenerator = uidGenerator;
	}

	public void finalize() throws Throwable {
		close();
		super.finalize();
	}

	@Override
	public ServiceHandle<IMutableOHLCTimeSeriesHost> getOHLCTimeSeries(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		return retrieveStockDatabaseInfo(stockDbHandle).ohlcTimeSeriesHandle;
	}

	@Override
	public ServiceHandle<IMutableTickTimeSeriesHost> getTickTimeSeries(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		return retrieveStockDatabaseInfo(stockDbHandle).tickTimeSeriesHandle;
	}

	@Override
	public ServiceHandle<IOHLCTimeSeriesHost> getVirtualTimeSeries(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		return retrieveStockDatabaseInfo(stockDbHandle).virtualTimeSeriesHandle;
	}

	@Override
	public long getTimestamp(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		return retrieveStockDatabaseInfo(stockDbHandle).stockDatabase.getTimestamp();
	}

	@Override
	public IContract getContract(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		return retrieveStockDatabaseInfo(stockDbHandle).stockDatabase.getContract();
	}

	@Override
	public BarSize getBarSize(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		return retrieveStockDatabaseInfo(stockDbHandle).stockDatabase.getBarSize();
	}

	@Override
	public DataType getDataType(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		return retrieveStockDatabaseInfo(stockDbHandle).stockDatabase.getDataType();
	}

	@Override
	public boolean isIncludeAfterHours(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		return retrieveStockDatabaseInfo(stockDbHandle).stockDatabase.isIncludeAfterHours();
	}

	@Override
	public TimeZone getTimeZone(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		return retrieveStockDatabaseInfo(stockDbHandle).stockDatabase.getTimeZone();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ServiceHandle<IStockDatabaseHost> addStockDatabase(IStockDatabase stockDatabase) {
		StockDatabaseInfo info = new StockDatabaseInfo();
		info.stockDatabase = stockDatabase;
		info.ohlcTimeSeriesHandle = (ServiceHandle<IMutableOHLCTimeSeriesHost>) ohlcTimeSeriesHost.addSeries(stockDatabase.getOHLCTimeSeries());
		info.tickTimeSeriesHandle = (ServiceHandle<IMutableTickTimeSeriesHost>) tickTimeSeriesHost.addSeries(stockDatabase.getTickTimeSeries());
		info.virtualTimeSeriesHandle = (ServiceHandle<IOHLCTimeSeriesHost>) virtualTimeSeriesHost.addSeries(stockDatabase.getVirtualTimeSeries());
		info.handle = new ServiceHandle<IStockDatabaseHost>(uidGenerator.nextUID());
		allStockDbs.put(info.handle, info);
		return info.handle;
	}

	@Override
	public void removeStockDatabase(ServiceHandle<IStockDatabaseHost> stockDatabaseHostHandle) {
		StockDatabaseInfo info = retrieveStockDatabaseInfo(stockDatabaseHostHandle);
		ohlcTimeSeriesHost.removeSeries(info.ohlcTimeSeriesHandle);
		tickTimeSeriesHost.removeSeries(info.tickTimeSeriesHandle);
		virtualTimeSeriesHost.removeSeries(info.virtualTimeSeriesHandle);
		allStockDbs.remove(info.handle);
	}

	@Override
	public IStockDatabase getStockDatabase(ServiceHandle<IStockDatabaseHost> handle) {
		StockDatabaseInfo info = retrieveStockDatabaseInfo(handle);
		IStockDatabase stockDb = null;
		if (info != null) {
			stockDb = info.stockDatabase;
		}
		return stockDb;
	}

	@Override
	public ServiceHandle<IStockDatabaseHost> getStockDatabaseHandle(IStockDatabase service) {
		for (Map.Entry<ServiceHandle<IStockDatabaseHost>, StockDatabaseInfo> entry : allStockDbs.entrySet()) {
			if (entry.getValue().stockDatabase.getPersistentID().equals(service.getPersistentID())) {
				return entry.getKey();
			}
		}
		return null;
	}

	@Override
	public String getPersistentID(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		return retrieveStockDatabaseInfo(stockDbHandle).stockDatabase.getPersistentID();
	}
	
	private StockDatabaseInfo retrieveStockDatabaseInfo(ServiceHandle<IStockDatabaseHost> stockDatabaseHostHandle) {
		StockDatabaseInfo info = allStockDbs.get(stockDatabaseHostHandle);
		if (info == null) {
			throw new IllegalArgumentException("Stock DB for handle: " + stockDatabaseHostHandle + " not found");
		}
		return info;
	}

	private void close() {
		Collection<ServiceHandle<IStockDatabaseHost>> handles = new ArrayList<ServiceHandle<IStockDatabaseHost>>();
		for (StockDatabaseInfo info : allStockDbs.values()) {
			handles.add(info.handle);
		}
		for (ServiceHandle<IStockDatabaseHost> handle : handles) {
			removeStockDatabase(handle);
		}
	}
	
}
