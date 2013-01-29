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

import java.util.Date;
import java.util.TimeZone;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.osgi.ISeriesIteratorHost;
import com.quantcomponents.core.osgi.ISeriesListenerHostLocal;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IMutableOHLCTimeSeries;
import com.quantcomponents.marketdata.IMutableTickTimeSeries;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.IOHLCTimeSeries;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.marketdata.ITickPoint;
import com.quantcomponents.marketdata.osgi.IMutableOHLCTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.IMutableTickTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.IOHLCTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHost;
import com.quantcomponents.marketdata.osgi.ImmutableOHLCTimeSeriesProxy;

public class StockDatabaseProxy implements IStockDatabase {
	private final IStockDatabaseHost stockDatabaseHost;
	private final IMutableOHLCTimeSeriesHost ohlcTimeSeriesHost;
	private final IMutableTickTimeSeriesHost tickTimeSeriesHost;
	private final IOHLCTimeSeriesHost virtualTimeSeriesHost;
	private final ISeriesIteratorHost<Date, Double, IOHLCPoint> ohlcSeriesIteratorHost;
	private final ISeriesIteratorHost<Date, Double, ITickPoint> tickSeriesIteratorHost;
	private final ISeriesListenerHostLocal<Date, Double, IOHLCPoint> ohlcSeriesListenerHost;
	private final ISeriesListenerHostLocal<Date, Double, ITickPoint> tickSeriesListenerHost;
	private final ServiceHandle<IStockDatabaseHost> stockDatabaseHandle;
	
	public StockDatabaseProxy(IStockDatabaseHost stockDatabaseHost, IMutableOHLCTimeSeriesHost ohlcTimeSeriesHost, IMutableTickTimeSeriesHost tickTimeSeriesHost,
			IOHLCTimeSeriesHost virtualTimeSeriesHost, ISeriesIteratorHost<Date, Double, IOHLCPoint> ohlcSeriesIteratorHost, ISeriesIteratorHost<Date, Double, ITickPoint> tickSeriesIteratorHost,
			ISeriesListenerHostLocal<Date, Double, IOHLCPoint> ohlcSeriesListenerHost, ISeriesListenerHostLocal<Date, Double, ITickPoint> tickSeriesListenerHost, ServiceHandle<IStockDatabaseHost> stockDatabaseHandle) {
		this.stockDatabaseHost = stockDatabaseHost;
		this.ohlcTimeSeriesHost = ohlcTimeSeriesHost;
		this.tickTimeSeriesHost = tickTimeSeriesHost;
		this.virtualTimeSeriesHost = virtualTimeSeriesHost;
		this.ohlcSeriesIteratorHost = ohlcSeriesIteratorHost;
		this.tickSeriesIteratorHost = tickSeriesIteratorHost;
		this.ohlcSeriesListenerHost = ohlcSeriesListenerHost;
		this.tickSeriesListenerHost = tickSeriesListenerHost;
		this.stockDatabaseHandle = stockDatabaseHandle;
	}

	@Override
	public IMutableOHLCTimeSeries getOHLCTimeSeries() {
		ServiceHandle<IMutableOHLCTimeSeriesHost> timeSeriesHostHandle = stockDatabaseHost.getOHLCTimeSeries(stockDatabaseHandle);
		return new OHLCTimeSeriesProxy(ohlcTimeSeriesHost, ohlcSeriesIteratorHost, ohlcSeriesListenerHost, timeSeriesHostHandle);
	}

	@Override
	public IMutableTickTimeSeries getTickTimeSeries() {
		ServiceHandle<IMutableTickTimeSeriesHost> timeSeriesHostHandle = stockDatabaseHost.getTickTimeSeries(stockDatabaseHandle);
		return new TickTimeSeriesProxy(tickTimeSeriesHost, tickSeriesIteratorHost, tickSeriesListenerHost, timeSeriesHostHandle);
	}

	@Override
	public IOHLCTimeSeries getVirtualTimeSeries() {
		ServiceHandle<IOHLCTimeSeriesHost> timeSeriesHostHandle = stockDatabaseHost.getVirtualTimeSeries(stockDatabaseHandle);
		return new ImmutableOHLCTimeSeriesProxy(virtualTimeSeriesHost, ohlcSeriesIteratorHost, ohlcSeriesListenerHost, timeSeriesHostHandle);
	}

	@Override
	public long getTimestamp() {
		return stockDatabaseHost.getTimestamp(stockDatabaseHandle);
	}

	@Override
	public TimeZone getTimeZone() {
		return stockDatabaseHost.getTimeZone(stockDatabaseHandle);
	}

	@Override
	public IContract getContract() {
		return stockDatabaseHost.getContract(stockDatabaseHandle);
	}

	@Override
	public BarSize getBarSize() {
		return stockDatabaseHost.getBarSize(stockDatabaseHandle);
	}

	@Override
	public DataType getDataType() {
		return stockDatabaseHost.getDataType(stockDatabaseHandle);
	}

	@Override
	public boolean isIncludeAfterHours() {
		return stockDatabaseHost.isIncludeAfterHours(stockDatabaseHandle);
	}

	@Override
	public String getPersistentID() {
		return stockDatabaseHost.getPersistentID(stockDatabaseHandle);
	}
}
