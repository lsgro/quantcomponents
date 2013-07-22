/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata.service;

import java.net.ConnectException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.marketdata.IMarketDataProvider;
import com.quantcomponents.marketdata.IMarketDataProvider.ITickListener;
import com.quantcomponents.marketdata.IMutableTickTimeSeries;
import com.quantcomponents.marketdata.IRealTimeMarketDataManager;
import com.quantcomponents.marketdata.IRealTimeMarketDataProvider;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.marketdata.ITickPoint;

public class RealTimeMarketDataManager extends MarketDataManager implements IRealTimeMarketDataManager {
	
	/**
	 * Fills a mutable time series with real-time data
	 */
	public class RealtimeDataFeeder {
		private final IRealTimeMarketDataProvider provider;
		private boolean autoUpdate;
		private final IMutableTickTimeSeries timeSeries;
		private final DataType dataType;
		private ITickListener listener;
		
		private class TickListener implements IMarketDataProvider.ITickListener {
			@Override
			public void onTick(ITickPoint tick) {
				if (!isRunning()) {
					logger.log(Level.SEVERE, "Data received with feeder stopped");
					return;
				}
				if (!dataType.includes(tick.getDataType())) {
					return;
				}
				ITickPoint lastExistingTick = (ITickPoint) timeSeries.getLast();
				if (lastExistingTick != null && tick.getIndex().before(lastExistingTick.getIndex())) {
					logger.log(Level.WARNING, "Received old tick: " + tick.getIndex() + "; last available: " + lastExistingTick.getIndex());
				} else {
					timeSeries.addLast(tick);
				}
			}
		}
		
		public RealtimeDataFeeder(IRealTimeMarketDataProvider provider, IMutableTickTimeSeries timeSeries, DataType dataType) {
			this.provider = provider;
			this.timeSeries = timeSeries;
			this.dataType = dataType;
		}

		public void start() throws ConnectException, RequestFailedException {
			if (!autoUpdate) {
				autoUpdate = true;
				listener = new TickListener();
				provider.startTicks(timeSeries.getContract(), listener);
			}
		}

		public void stop() throws ConnectException {
			if (autoUpdate) {
				autoUpdate = false;
				provider.stopTicks(timeSeries.getContract(), listener);
				listener = null;
			}
		}

		public boolean isRunning() {
			return autoUpdate;
		}
	}

	private static final Logger logger = Logger.getLogger(RealTimeMarketDataManager.class.getName());
	protected final Map<IStockDatabase, RealtimeDataFeeder> stockDbFeeders = new ConcurrentHashMap<IStockDatabase, RealtimeDataFeeder>();
	
	// Overrides superclass method (same signature) but expects a parameter of type IRealTimeMarketDataProvider
	public void setMarketDataProvider(IMarketDataProvider marketDataProvider) {
		IRealTimeMarketDataProvider.class.cast(marketDataProvider); // ensure correct type of parameter
		super.setMarketDataProvider(marketDataProvider);
	}
	
	protected IRealTimeMarketDataProvider getMarketDataProvider() {
		return (IRealTimeMarketDataProvider) super.getMarketDataProvider();
	}

	@Override
	public void removeStockDatabase(IStockDatabase stockDb) throws ConnectException, RequestFailedException {
		RealtimeDataFeeder feeder = stockDbFeeders.remove(stockDb);
		if (feeder != null) {
			feeder.stop();
		}
		super.removeStockDatabase(stockDb);
	}

	@Override
	public void startRealtimeUpdate(IStockDatabase stockDb, boolean fillHistoricalGap, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		RealtimeDataFeeder realtimeFeeder = null;
		synchronized (stockDb) {
			realtimeFeeder = stockDbFeeders.get(stockDb);
			if (realtimeFeeder == null) {
				realtimeFeeder = new RealtimeDataFeeder(getMarketDataProvider(), stockDb.getTickTimeSeries(), stockDb.getDataType());
				stockDbFeeders.put(stockDb, realtimeFeeder);
			} else {
				if (realtimeFeeder.isRunning()) {
					return;
				}
			}
		}
		if (fillHistoricalGap) {
			if (stockDb.getVirtualTimeSeries().getLast() != null) {
				Date lastDataIndex = stockDb.getVirtualTimeSeries().getLast().getIndex();
				fillHistoricalData(stockDb, lastDataIndex, new Date(), taskMonitor);
			}
		}
		realtimeFeeder.start();
	}

	@Override
	public void stopRealtimeUpdate(IStockDatabase stockDb) throws ConnectException, RequestFailedException {
		RealtimeDataFeeder realtimeFeeder = stockDbFeeders.get(stockDb);
		if (realtimeFeeder == null) {
			throw new IllegalArgumentException("Stock DB " + stockDb + " has no realtime feeding");
		}
		realtimeFeeder.stop();
	}

	@Override
	public boolean isRealtimeUpdate(IStockDatabase stockDb) throws ConnectException, RequestFailedException {
		RealtimeDataFeeder realtimeFeeder = stockDbFeeders.get(stockDb);
		if (realtimeFeeder == null) {
			return false;
		}
		return realtimeFeeder.isRunning();
	}

}
