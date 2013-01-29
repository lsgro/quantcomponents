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

import java.util.logging.Logger;

import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.osgi.IMutableOHLCTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.IMutableTickTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.IOHLCTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.IOHLCTimeSeriesIteratorHost;
import com.quantcomponents.marketdata.osgi.IOHLCTimeSeriesListenerHostLocal;
import com.quantcomponents.marketdata.osgi.IStockDatabaseHost;
import com.quantcomponents.marketdata.osgi.IStockDatabaseProxyFactory;
import com.quantcomponents.marketdata.osgi.ITickTimeSeriesIteratorHost;
import com.quantcomponents.marketdata.osgi.ITickTimeSeriesListenerHostLocal;


public class StockDatabaseProxyFactory implements IStockDatabaseProxyFactory {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(StockDatabaseProxyFactory.class.getName());
	private volatile IStockDatabaseHost stockDatabaseHost;
	private volatile IMutableOHLCTimeSeriesHost ohlcTimeSeriesHost;
	private volatile IMutableTickTimeSeriesHost tickTimeSeriesHost;
	private volatile IOHLCTimeSeriesHost virtualTimeSeriesHost;
	private volatile IOHLCTimeSeriesIteratorHost ohlcSeriesIteratorHost;
	private volatile ITickTimeSeriesIteratorHost tickSeriesIteratorHost;
	private volatile IOHLCTimeSeriesListenerHostLocal ohlcSeriesListenerHost;
	private volatile ITickTimeSeriesListenerHostLocal tickSeriesListenerHost;

	public StockDatabaseProxyFactory() {}
			
	public StockDatabaseProxyFactory(IStockDatabaseHost stockDatabaseHost, IMutableOHLCTimeSeriesHost ohlcTimeSeriesHost, IMutableTickTimeSeriesHost tickTimeSeriesHost,
			IOHLCTimeSeriesHost virtualTimeSeriesHost, IOHLCTimeSeriesIteratorHost ohlcSeriesIteratorHost, ITickTimeSeriesIteratorHost tickSeriesIteratorHost,
			IOHLCTimeSeriesListenerHostLocal ohlcSeriesListenerHost, ITickTimeSeriesListenerHostLocal tickSeriesListenerHost) {
		this.stockDatabaseHost = stockDatabaseHost;
		this.ohlcTimeSeriesHost = ohlcTimeSeriesHost;
		this.tickTimeSeriesHost = tickTimeSeriesHost;
		this.virtualTimeSeriesHost = virtualTimeSeriesHost;
		this.ohlcSeriesIteratorHost = ohlcSeriesIteratorHost;
		this.tickSeriesIteratorHost = tickSeriesIteratorHost;
		this.ohlcSeriesListenerHost = ohlcSeriesListenerHost;
		this.tickSeriesListenerHost = tickSeriesListenerHost;
	}

	public void deactivate() { }
	
	@Override
	public StockDatabaseProxy createStockDatabaseProxy(ServiceHandle<IStockDatabaseHost> stockDbHandle) {
		return new StockDatabaseProxy(stockDatabaseHost, ohlcTimeSeriesHost, tickTimeSeriesHost,
				virtualTimeSeriesHost, ohlcSeriesIteratorHost, tickSeriesIteratorHost,
				ohlcSeriesListenerHost, tickSeriesListenerHost, stockDbHandle);
	}

	public void setStockDatabaseHost(IStockDatabaseHost stockDatabaseHost) {
		this.stockDatabaseHost = stockDatabaseHost;
	}

	public void setOhlcTimeSeriesHost(IMutableOHLCTimeSeriesHost ohlcTimeSeriesHost) {
		this.ohlcTimeSeriesHost = ohlcTimeSeriesHost;
	}

	public void setTickTimeSeriesHost(IMutableTickTimeSeriesHost tickTimeSeriesHost) {
		this.tickTimeSeriesHost = tickTimeSeriesHost;
	}

	public void setVirtualTimeSeriesHost(IOHLCTimeSeriesHost virtualTimeSeriesHost) {
		this.virtualTimeSeriesHost = virtualTimeSeriesHost;
	}

	public void setOhlcSeriesIteratorHost(IOHLCTimeSeriesIteratorHost ohlcSeriesIteratorHost) {
		this.ohlcSeriesIteratorHost = ohlcSeriesIteratorHost;
	}

	public void setTickSeriesIteratorHost(ITickTimeSeriesIteratorHost tickSeriesIteratorHost) {
		this.tickSeriesIteratorHost = tickSeriesIteratorHost;
	}

	public void setOhlcSeriesListenerHost(IOHLCTimeSeriesListenerHostLocal ohlcSeriesListenerHost) {
		this.ohlcSeriesListenerHost = ohlcSeriesListenerHost;
	}

	public void setTickSeriesListenerHost(ITickTimeSeriesListenerHostLocal tickSeriesListenerHost) {
		this.tickSeriesListenerHost = tickSeriesListenerHost;
	}

}
