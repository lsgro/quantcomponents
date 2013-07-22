/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ui.marketdata;

import java.net.ConnectException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.IPrettyNamed;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.marketdata.IMarketDataManager;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.ui.core.IMonitorableContainer;
import com.quantcomponents.ui.core.IMonitorableContainerListener;
import com.quantcomponents.ui.core.IMutableMonitorableContainer;

public class MarketDataManagerPresentationWrapper implements IMarketDataManager, IPrettyNamed, IMonitorableContainer<StockDatabasePresentationWrapper> {
	private final IMarketDataManager marketDataManager;
	private final IMutableMonitorableContainer<IMarketDataManager, MarketDataManagerPresentationWrapper> parent;
	private final Map<String, StockDatabasePresentationWrapper> wrappersByID = new ConcurrentHashMap<String, StockDatabasePresentationWrapper>();
	private final Set<IMonitorableContainerListener<StockDatabasePresentationWrapper>> listeners = new CopyOnWriteArraySet<IMonitorableContainerListener<StockDatabasePresentationWrapper>>(); 
	
	public MarketDataManagerPresentationWrapper(IMarketDataManager manager, IMutableMonitorableContainer<IMarketDataManager, MarketDataManagerPresentationWrapper>  parent) {
		this.marketDataManager = manager;
		this.parent = parent;
	}
	
	public IMarketDataManager getMarketDataManager() {
		return marketDataManager;
	}
	
	public IMutableMonitorableContainer<IMarketDataManager, MarketDataManagerPresentationWrapper> getParent() {
		return parent;
	}
	
	@Override
	public String getPrettyName() {
		return getMarketDataManager().getPrettyName();
	}
	
	public void synchronizeStockDatabases() {
		Collection<IStockDatabase> stockDatabases = getMarketDataManager().allStockDatabases();
		Set<String> stockDbNames = new HashSet<String>();
		for (IStockDatabase stockDb : stockDatabases) {
			String stockDbID = stockDb.getPersistentID();
			stockDbNames.add(stockDbID);
			if (!wrappersByID.containsKey(stockDbID)) {
				StockDatabasePresentationWrapper wrapper = getOrCreateWrapper(stockDb);
				for (IMonitorableContainerListener<StockDatabasePresentationWrapper> listener : listeners) {
					listener.onElementAdded(wrapper);
				}
			}
		}
		Iterator<Map.Entry<String, StockDatabasePresentationWrapper>> iterator = wrappersByID.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, StockDatabasePresentationWrapper> entry = iterator.next();
			if (!stockDbNames.contains(entry.getKey())) {
				iterator.remove();
				for (IMonitorableContainerListener<StockDatabasePresentationWrapper> listener : listeners) {
					listener.onElementRemoved(entry.getValue());
				}
			}
		}
	}

	@Override
	public List<IContract> searchContracts(IContract criteria, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		return getMarketDataManager().searchContracts(criteria, taskMonitor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IStockDatabase> allStockDatabases() {
		return (Collection<IStockDatabase>) (Collection<?>) getElements();
	}

	@Override
	public StockDatabasePresentationWrapper findStockDatabase(IContract contract, DataType dataType, BarSize barSize, Boolean includeAfterHours) {
		IStockDatabase stockDatabase = getMarketDataManager().findStockDatabase(contract, dataType, barSize, includeAfterHours);
		if (stockDatabase != null) {
			return getOrCreateWrapper(stockDatabase);
		} else {
			return null;
		}
	}
	
	@Override
	public StockDatabasePresentationWrapper createStockDatabase(IContract contract, DataType dataType, BarSize barSize, boolean includeAfterHours, TimeZone timeZone) {
		IStockDatabase stockDatabase = getMarketDataManager().createStockDatabase(contract, dataType, barSize, includeAfterHours, timeZone);
		StockDatabasePresentationWrapper wrapper = getOrCreateWrapper(stockDatabase);
		for (IMonitorableContainerListener<StockDatabasePresentationWrapper> listener : listeners) {
			listener.onElementAdded(wrapper);
		}
		return wrapper;
	}

	@Override
	public void removeStockDatabase(IStockDatabase stockDb) throws ConnectException, RequestFailedException {
		if (stockDb instanceof StockDatabasePresentationWrapper) {
			StockDatabasePresentationWrapper wrapper = (StockDatabasePresentationWrapper) stockDb;
			stockDb = wrapper.getInner();
			wrappersByID.remove(wrapper.getPersistentID());
			for (IMonitorableContainerListener<StockDatabasePresentationWrapper> listener : listeners) {
				listener.onElementRemoved(wrapper);
			}
		}
		getMarketDataManager().removeStockDatabase(stockDb);
	}

	@Override
	public void fillHistoricalData(IStockDatabase stockDb, Date startDate, Date endDate, ITaskMonitor taskMonitor) throws ConnectException,
			RequestFailedException {
		if (stockDb instanceof StockDatabasePresentationWrapper) {
			stockDb = ((StockDatabasePresentationWrapper) stockDb).getInner();
		}
		getMarketDataManager().fillHistoricalData(stockDb, startDate, endDate, taskMonitor);
	}

	@Override
	public int numberOfStockDatabases() {
		return wrappersByID.size();
	}

	@Override
	public Collection<StockDatabasePresentationWrapper> getElements() {
		Collection<StockDatabasePresentationWrapper> result = new LinkedList<StockDatabasePresentationWrapper>();
		result.addAll(wrappersByID.values());
		return result;
	}

	@Override
	public void addListener(IMonitorableContainerListener<StockDatabasePresentationWrapper> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(IMonitorableContainerListener<StockDatabasePresentationWrapper> listener) {
		listeners.remove(listener);
	}

	@Override
	public StockDatabasePresentationWrapper getStockDatabase(String ID) {
		return getOrCreateWrapper(getMarketDataManager().getStockDatabase(ID));
	}

	@Override
	public ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> getSeries(String ID) {
		return getStockDatabase(ID).getVirtualTimeSeries();
	}
	
	private synchronized StockDatabasePresentationWrapper getOrCreateWrapper(IStockDatabase stockDatabase) {
		StockDatabasePresentationWrapper wrapper = wrappersByID.get(stockDatabase.getPersistentID());
		if (wrapper == null) {
			wrapper = new StockDatabasePresentationWrapper(stockDatabase, this);
			wrappersByID.put(wrapper.getPersistentID(), wrapper);
		}
		return wrapper;
	}

	@Override
	public void dispose() {
		wrappersByID.clear();
		listeners.clear();
	}
}
