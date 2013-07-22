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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.marketdata.IMarketDataManager;
import com.quantcomponents.marketdata.IMarketDataProvider;
import com.quantcomponents.marketdata.IMutableOHLCTimeSeries;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.marketdata.IStockDatabaseContainer;
import com.quantcomponents.marketdata.StockDatabase;

public class MarketDataManager implements IMarketDataManager {
	/**
	 * Fills a mutable time series with historical data
	 */
	public class HistoricalDataFeeder {
		private static final int MIN_REALTIME_DATA_RESOLUTION_SEC = 5;
		
		protected final IMarketDataProvider provider;
		protected final IMutableOHLCTimeSeries timeSeries;

		public HistoricalDataFeeder(IMarketDataProvider provider, IMutableOHLCTimeSeries timeSeries) {
			this.provider = provider;
			this.timeSeries = timeSeries;
		}

		protected Date alignToTimeResBoundary(Date input) {
			Calendar tmpCal = Calendar.getInstance();
			tmpCal.setTime(input);
			tmpCal.set(Calendar.MILLISECOND, 0);
			int seconds = tmpCal.get(Calendar.SECOND);
			if (seconds % MIN_REALTIME_DATA_RESOLUTION_SEC > 0) {
				tmpCal.add(Calendar.SECOND, MIN_REALTIME_DATA_RESOLUTION_SEC - seconds % MIN_REALTIME_DATA_RESOLUTION_SEC);
			}
			return tmpCal.getTime();
		}
		
		public void fillHistoricalData(Date startDateTime, Date endDateTime, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
			LinkedList<IOHLCPoint> backwardHistoricalData = new LinkedList<IOHLCPoint>();
			LinkedList<IOHLCPoint> forwardHistoricalData = new LinkedList<IOHLCPoint>();
			Date alignedStartDateTime = alignToTimeResBoundary(startDateTime);
			Date alignedEndDateTime = alignToTimeResBoundary(endDateTime);
			if (!timeSeries.isEmpty()) {
				Date seriesStartDateTime = timeSeries.getFirst().getIndex();
				if (alignedStartDateTime.before(seriesStartDateTime)) {
					logger.log(Level.INFO, "Retrieving older historical data");
					List<IOHLCPoint> olderHistoricalData = provider.historicalBars(timeSeries.getContract(), alignedStartDateTime, seriesStartDateTime, timeSeries.getBarSize(), timeSeries.getDataType(), timeSeries.isIncludeAfterHours(), taskMonitor);
					backwardHistoricalData.addAll(olderHistoricalData);
				}
				Date seriesEndDateTime = timeSeries.getLast().getIndex();
				if (alignedEndDateTime.after(seriesEndDateTime)) {
					logger.log(Level.INFO, "Retrieving latest historical data");
					List<IOHLCPoint> latestHistoricalData = provider.historicalBars(timeSeries.getContract(), seriesEndDateTime, alignedEndDateTime, timeSeries.getBarSize(), timeSeries.getDataType(), timeSeries.isIncludeAfterHours(), taskMonitor);
					forwardHistoricalData.addAll(latestHistoricalData);
				}
			} else {
				logger.log(Level.INFO, "Retrieving all historical data");
				List<IOHLCPoint> allHistoricalData = provider.historicalBars(timeSeries.getContract(), alignedStartDateTime, alignedEndDateTime, timeSeries.getBarSize(), timeSeries.getDataType(), timeSeries.isIncludeAfterHours(), taskMonitor);
				forwardHistoricalData.addAll(allHistoricalData);
			}
			ListIterator<IOHLCPoint> backwardData = backwardHistoricalData.listIterator(backwardHistoricalData.size());
			logger.log(Level.INFO, "Merging older historical data");
			while (backwardData.hasPrevious()) {
				timeSeries.addFirstIfNotExists(backwardData.previous());
			}
			Iterator<IOHLCPoint> forwardData = forwardHistoricalData.iterator();
			logger.log(Level.INFO, "Merging latest historical data");
			while (forwardData.hasNext()) {
				timeSeries.addLastIfNotExists(forwardData.next());
			}
			logger.log(Level.INFO, "Finished merging historical data");
		}
	}
	
	private static final Logger logger = Logger.getLogger(MarketDataManager.class.getName());
	private volatile IStockDatabaseContainer stockDatabaseContainer;
	private volatile String name;
	private volatile IMarketDataProvider marketDataProvider;
	
	public MarketDataManager() {
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, "Cound not find hostname", e);
			hostname = "localhost";
		}
		setName("MarketDataManager@" + hostname);
	}

	public void setMarketDataProvider(IMarketDataProvider marketDataProvider) {
		this.marketDataProvider = marketDataProvider;
		if (marketDataProvider != null) {
			setName(getName() + "[" + marketDataProvider.toString() + "]");
		}
	}
	
	public void setStockDatabaseContainer(IStockDatabaseContainer stockDatabaseContainer) {
		this.stockDatabaseContainer = stockDatabaseContainer;
	}

	@Override
	public String getPrettyName() {
		return getName();
	}
	
	@Override
	public List<IContract> searchContracts(IContract criteria, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		return getMarketDataProvider().searchContracts(criteria, taskMonitor);
	}

	@Override
	public Collection<IStockDatabase> allStockDatabases() {
		return new ArrayList<IStockDatabase>(getStockDatabaseContainer().allStockDatabases());
	}


	@Override
	public IStockDatabase findStockDatabase(IContract contract, DataType dataType, BarSize barSize, Boolean includeAfterHours) {
		for (IStockDatabase stockDb : getStockDatabaseContainer().allStockDatabases()) {
			if (stockDatabaseQualifies(stockDb, contract, dataType, barSize, includeAfterHours)) {
				return stockDb;
			}
		}
		return null;
	}

	@Override
	public IStockDatabase getStockDatabase(String ID) {
		return getStockDatabaseContainer().getStockDatabase(ID);
	}

	@Override
	public ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> getSeries(String ID) {
		IStockDatabase stockDatabase = getStockDatabase(ID);
		return stockDatabase == null ? null : stockDatabase.getVirtualTimeSeries();
	}

	@Override
	public IStockDatabase createStockDatabase(IContract contract, DataType dataType, BarSize barSize, boolean includeAfterHours, TimeZone timeZone) {
		IStockDatabase stockDb = new StockDatabase(contract, dataType, barSize, includeAfterHours, timeZone);
		getStockDatabaseContainer().addStockDatabase(stockDb);
		return stockDb;
	}

	@Override
	public void removeStockDatabase(IStockDatabase stockDb) throws ConnectException, RequestFailedException {
		getStockDatabaseContainer().removeStockDatabase(stockDb);
	}

	@Override
	public void fillHistoricalData(IStockDatabase stockDb, Date startDate, Date endDate, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		new HistoricalDataFeeder(getMarketDataProvider(), stockDb.getOHLCTimeSeries()).fillHistoricalData(startDate, endDate, taskMonitor);
	}

	@Override
	public int numberOfStockDatabases() {
		return getStockDatabaseContainer().size();
	}

	private static boolean stockDatabaseQualifies(IStockDatabase stockDb, IContract contract, DataType dataType, BarSize barSize, Boolean includeAfterHours) {
		if (contract != null && !stockDb.getContract().equals(contract)) {
			return false;
		}
		if (dataType != null && !dataType.equals(stockDb.getDataType())) {
			return false;
		}
		if (barSize != null && !barSize.equals(stockDb.getBarSize())) {
			return false;
		}
		if (includeAfterHours != null && !includeAfterHours.equals(stockDb.isIncludeAfterHours())) {
			return false;
		}
		return true;
	}

	protected IStockDatabaseContainer getStockDatabaseContainer() {
		return stockDatabaseContainer;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	protected String getName() {
		return name;
	}

	protected IMarketDataProvider getMarketDataProvider() {
		return marketDataProvider;
	}
}
