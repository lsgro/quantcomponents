/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.series.jdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.marketdata.IMutableOHLCTimeSeries;
import com.quantcomponents.marketdata.IMutableTickTimeSeries;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.marketdata.IStockDatabaseContainer;
import com.quantcomponents.marketdata.ITickPoint;
import com.quantcomponents.marketdata.StockDatabase;

public class JdbcStockDatabaseContainer implements IStockDatabaseContainer {
	private static final Logger logger = Logger.getLogger(JdbcStockDatabaseContainer.class.getName());
	
	private enum EventType { ADD, UPDATE };
	
	private class OHLCEvent {
		final String stockDbId;
		final EventType type;
		final IOHLCPoint existingOhlc;
		final IOHLCPoint newOhlc;
		
		public OHLCEvent(String stockDbId, EventType type, IOHLCPoint existingOhlc, IOHLCPoint newOhlc) {
			this.stockDbId = stockDbId;
			this.type = type;
			this.existingOhlc = existingOhlc;
			this.newOhlc = newOhlc;
		}
	}
	
	private class TickEvent {
		public TickEvent(String stockDbId, ITickPoint tick) {
			this.stockDbId = stockDbId;
			this.tick = tick;
		}
		final String stockDbId;
		final ITickPoint tick;
	}
	
	private class OHLCTimeSeriesListener implements ISeriesListener<Date, Double> {
		private final String stockDbId;
		
		OHLCTimeSeriesListener(String stockDbId) {
			this.stockDbId = stockDbId;
		}

		@Override
		public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) {
			ohlcEventsQueue.add(new OHLCEvent(stockDbId, EventType.UPDATE, (IOHLCPoint) existingItem, (IOHLCPoint) updatedItem));
		}

		@Override
		public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
			ohlcEventsQueue.add(new OHLCEvent(stockDbId, EventType.ADD, null, (IOHLCPoint) newItem));
		}
	}
	
	private class TickTimeSeriesListener implements ISeriesListener<Date, Double> {
		private final String stockDbId;
		
		TickTimeSeriesListener(String stockDbId) {
			this.stockDbId = stockDbId;
		}

		@Override
		public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
			tickEventsQueue.add(new TickEvent(stockDbId, (ITickPoint) newItem));
		}
	}
	
	private static class StockDbCacheInfo {
		public StockDbCacheInfo(String id, IStockDatabase stockDatabase, OHLCTimeSeriesListener ohlcListener, TickTimeSeriesListener tickListener) {
			this.id = id;
			this.stockDatabase = stockDatabase;
			this.ohlcListener = ohlcListener;
			this.tickListener = tickListener;
		}
		String id;
		IStockDatabase stockDatabase;
		OHLCTimeSeriesListener ohlcListener;
		TickTimeSeriesListener tickListener;
	}
	
	private final Map<String, StockDbCacheInfo> cacheById = new HashMap<String, StockDbCacheInfo>();
	private final Map<IStockDatabase, StockDbCacheInfo> cacheByStockDb = new HashMap<IStockDatabase, StockDbCacheInfo>();
	private final BlockingQueue<OHLCEvent> ohlcEventsQueue = new LinkedBlockingQueue<OHLCEvent>(); 
	private final BlockingQueue<TickEvent> tickEventsQueue = new LinkedBlockingQueue<TickEvent>(); 
	private volatile IStockDatabaseHeaderDao stockDbHeaderDao;
	private volatile IOHLCPointDao ohlcPointDao;
	private volatile ITickPointDao tickPointDao;
	private volatile boolean interrupt;
	private volatile boolean asyncPersistence = false;
	private volatile Thread asyncOhlcPersisterThread;
	private volatile Thread asyncTickPersisterThread;
	
	private final Runnable asyncOhlcPersister = new Runnable() {
		@Override
		public void run() {
			try {
				while (!interrupt) {
					OHLCEvent event = ohlcEventsQueue.take();
					if (event.type == EventType.ADD) {
						ohlcPointDao.save(event.stockDbId, event.newOhlc);
					} else if (event.type == EventType.UPDATE) {
						ohlcPointDao.update(event.stockDbId, event.existingOhlc, event.newOhlc);
					}
					if (!asyncPersistence) { // for testing: do not call startAsynchronousPersisters()
						break;
					}
				}
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Exception while persisting OHLCPoint", e);
			} catch (InterruptedException e) {
				logger.log(Level.WARNING, "OHLC persisting thread interrupted");
			}
		}};
		
	private final Runnable asyncTickPersister = new Runnable() {
		@Override
		public void run() {
			try {
				while (!interrupt) {
					TickEvent event = tickEventsQueue.take();
					tickPointDao.save(event.stockDbId, event.tick);
					if (!asyncPersistence) { // for testing: do not call startAsynchronousPersisters()
						break;
					}
				}
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Exception while persisting TickPoint", e);
			} catch (InterruptedException e) {
				logger.log(Level.WARNING, "Tick persisting thread interrupted");
			}
		}};
		
	public void setStockDbHeaderDao(IStockDatabaseHeaderDao stockDbHeaderDao) {
		this.stockDbHeaderDao = stockDbHeaderDao;
	}

	public void setOhlcPointDao(IOHLCPointDao ohlcPointDao) {
		this.ohlcPointDao = ohlcPointDao;
	}

	public void setTickPointDao(ITickPointDao tickPointDao) {
		this.tickPointDao = tickPointDao;
	}

	public void activate() throws SQLException {
		init();
		allStockDatabases();
		startAsynchronousPersisters();
	}
	
	public void deactivate() throws SQLException {
		stopAsynchronousPersisters();
	}
	
	public void init() throws SQLException {
		stockDbHeaderDao.initDb();
		ohlcPointDao.initDb();
		tickPointDao.initDb();
	}
	
	private void startAsynchronousPersisters() {
		asyncPersistence = true;
		asyncOhlcPersisterThread = new Thread(getAsyncOhlcPersister(), "OHLC persister");
		asyncTickPersisterThread = new Thread(getAsyncTickPersister(), "Tick persister");
		asyncOhlcPersisterThread.start();
		asyncTickPersisterThread.start();
	}
	
	private void stopAsynchronousPersisters() {
		interrupt = true;
		asyncOhlcPersisterThread.interrupt();
		asyncTickPersisterThread.interrupt();
	}
	
	public Runnable getAsyncOhlcPersister() {
		return asyncOhlcPersister;
	}

	public Runnable getAsyncTickPersister() {
		return asyncTickPersister;
	}

	@Override
	public synchronized void addStockDatabase(IStockDatabase stockDatabase) {
		StockDatabaseHeader hdr = StockDatabaseHeader.fromStockDatabase(stockDatabase);
		try {
			stockDbHeaderDao.save(hdr);
			for (IOHLCPoint ohlc : stockDatabase.getOHLCTimeSeries()) {
				ohlcPointDao.save(hdr.id, ohlc);
			}
			for (ITickPoint tick : stockDatabase.getTickTimeSeries()) {
				tickPointDao.save(hdr.id, tick);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Exception while saving stock DB", e);
			return;
		}
		addToCache(stockDatabase, hdr.id);
	}

	@Override
	public synchronized Collection<IStockDatabase> allStockDatabases() {
		Collection<IStockDatabase> result = new LinkedList<IStockDatabase>();
		try {
			Set<StockDatabaseHeader> headerSet = null;
			headerSet = stockDbHeaderDao.findAll();
			for (StockDatabaseHeader hdr : headerSet) {
				result.add(getOrCreate(hdr));
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Exception while finding all stock DBs", e);
			return result;
		}
		return result;
	}

	@Override
	public Collection<IStockDatabase> findStockDatabases(IContract contract) {
		Collection<IStockDatabase> result = new LinkedList<IStockDatabase>();
		try {
			Set<StockDatabaseHeader> headerSet = null;
			headerSet = stockDbHeaderDao.findByContract(contract);
			for (StockDatabaseHeader hdr : headerSet) {
				result.add(getOrCreate(hdr));
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Exception while finding stock DBs for contract: " + contract, e);
			return result;
		}
		return result;
	}

	@Override
	public synchronized boolean removeStockDatabase(IStockDatabase stockDatabase) {
		StockDbCacheInfo cacheItem = cacheByStockDb.get(stockDatabase);
		if (cacheItem != null) {
			stockDatabase.getOHLCTimeSeries().removeSeriesListener(cacheItem.ohlcListener);
			stockDatabase.getTickTimeSeries().removeSeriesListener(cacheItem.tickListener);
			cacheByStockDb.remove(stockDatabase);
			cacheById.remove(cacheItem.id);
			try {
				stockDbHeaderDao.delete(cacheItem.id);
				ohlcPointDao.deleteAll(cacheItem.id);
				tickPointDao.deleteAll(cacheItem.id);
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Exception while deleting stock DB", e);
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	private void addToCache(IStockDatabase stockDatabase, String id) {
		OHLCTimeSeriesListener ohlcListener = new OHLCTimeSeriesListener(id);
		stockDatabase.getOHLCTimeSeries().addSeriesListener(ohlcListener);
		TickTimeSeriesListener tickListener = new TickTimeSeriesListener(id);
		stockDatabase.getTickTimeSeries().addSeriesListener(tickListener);
		StockDbCacheInfo cacheItem = new StockDbCacheInfo(id, stockDatabase, ohlcListener, tickListener);
		cacheById.put(id, cacheItem);
		cacheByStockDb.put(stockDatabase, cacheItem);
	}
	
	private IStockDatabase getOrCreate(StockDatabaseHeader hdr) throws SQLException {
		IStockDatabase stockDatabase;
		StockDbCacheInfo cacheItem = cacheById.get(hdr.id);
		if (cacheItem != null)	{
			stockDatabase = cacheItem.stockDatabase;
		} else {
			stockDatabase = new StockDatabase(hdr.contract, hdr.dataType, hdr.barSize, hdr.includeAfterHours, hdr.timeZone);
			IMutableOHLCTimeSeries ohlcTimeSeries = stockDatabase.getOHLCTimeSeries();
			for (IOHLCPoint ohlc : ohlcPointDao.find(hdr.id)) {
				ohlcTimeSeries.addLast(ohlc);
			}
			IMutableTickTimeSeries tickTimeSeries = stockDatabase.getTickTimeSeries();
			for (ITickPoint tick : tickPointDao.find(hdr.id)) {
				tickTimeSeries.addLast(tick);
			}
			addToCache(stockDatabase, hdr.id);
		}
		return stockDatabase;
	}

	@Override
	public int size() {
		int size = -1;
		try {
			size = stockDbHeaderDao.countAll();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Exception while counting all stock DBs", e);
		}
		return size;
	}

	@Override
	public IStockDatabase getStockDatabase(String ID) {
		StockDbCacheInfo info = cacheById.get(ID);
		if (info == null) {
			logger.log(Level.SEVERE, "Stock database not found for ID: " + ID);
			return null;
		}
		return cacheById.get(ID).stockDatabase;
	}
}