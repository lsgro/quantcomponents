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

import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.IPrettyNamed;
import com.quantcomponents.marketdata.IMutableOHLCTimeSeries;
import com.quantcomponents.marketdata.IMutableTickTimeSeries;
import com.quantcomponents.marketdata.IOHLCTimeSeries;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.marketdata.StockDatabase;

public class StockDatabasePresentationWrapper implements IStockDatabase, IPrettyNamed {
	private static final Logger logger = Logger.getLogger(StockDatabasePresentationWrapper.class.getName());
	private final IStockDatabase db;
	private final MarketDataManagerPresentationWrapper parent;
	private final String name;
	
	public StockDatabasePresentationWrapper(IStockDatabase db, MarketDataManagerPresentationWrapper parent) {
		this.db = db;
		this.parent = parent;
		this.name = StockDatabase.stringRepr(db);
	}

	public MarketDataManagerPresentationWrapper getParent() {
		return parent;
	}
	
	public IStockDatabase getInner() {
		return db;
	}
	
	public boolean isRealtimeUpdate() {
		try {
			return getParent().isRealtimeUpdate(this);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error while querying stock DB auto-update status: " + this.getPrettyName(), e);
			return false;
		}
	}
	
	@Override
	public IMutableOHLCTimeSeries getOHLCTimeSeries() {
		return db.getOHLCTimeSeries();
	}

	@Override
	public IMutableTickTimeSeries getTickTimeSeries() {
		return db.getTickTimeSeries();
	}

	@Override
	public IOHLCTimeSeries getVirtualTimeSeries() {
		return db.getVirtualTimeSeries();
	}

	@Override
	public long getTimestamp() {
		return db.getTimestamp();
	}

	@Override
	public TimeZone getTimeZone() {
		return db.getTimeZone();
	}

	@Override
	public IContract getContract() {
		return db.getContract();
	}

	@Override
	public DataType getDataType() {
		return db.getDataType();
	}

	@Override
	public BarSize getBarSize() {
		return db.getBarSize();
	}

	@Override
	public boolean isIncludeAfterHours() {
		return db.isIncludeAfterHours();
	}

	@Override
	public String getPersistentID() {
		return db.getPersistentID();
	}

	@Override
	public String getPrettyName() {
		return name;
	}
}
