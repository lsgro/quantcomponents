/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata;

import java.util.Date;
import java.util.TimeZone;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.model.beans.ContractBase;

/**
 * Implementation of {@link IStockDatabase}
 */
public class StockDatabase implements IStockDatabase {
	private final IContract contract;
	private final BarSize barSize;
	private final DataType dataType;
	private final boolean includeAfterHours;
	private final OHLCTimeSeries ohlcTimeSeries;
	private final TickTimeSeries tickTimeSeries;
	private final OHLCVirtualTimeSeries virtualTimeSeries;
	private final TimeZone timeZone;
	private final String ID;
	
	public StockDatabase(IContract contract, DataType dataType, BarSize barSize, boolean includeAfterHours, TimeZone timeZone) {
		ID = ContractBase.stringRepr(contract) + ";" + dataType.name() + ";" + barSize.name() + ";" + Boolean.toString(includeAfterHours) + ";" + timeZone.getID();
		this.contract = contract;
		this.barSize = barSize;
		this.dataType = dataType;
		this.includeAfterHours = includeAfterHours;
		this.timeZone = timeZone;
		ohlcTimeSeries = new SlaveOHLCTimeSeries(contract, dataType, barSize, includeAfterHours);
		tickTimeSeries = new SlaveTickTimeSeries(contract, dataType, barSize.getDurationInMs());
		virtualTimeSeries = new OHLCVirtualTimeSeries(ID, contract, dataType, barSize, includeAfterHours, timeZone);
		ohlcTimeSeries.addSeriesListener(new ISeriesListener<Date, Double>() {

			@Override
			public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
				virtualTimeSeries.addOrUpdateBar((IOHLCPoint) newItem);
			}

			@Override
			public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) {
				virtualTimeSeries.addOrUpdateBar((IOHLCPoint) updatedItem);
			}});
		tickTimeSeries.addSeriesListener(new ISeriesListener<Date, Double>() {

			@Override
			public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
				virtualTimeSeries.addTick((ITickPoint) newItem);
			}

			@Override
			public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) {
				// never
			}});
	}

	@Override
	public IMutableOHLCTimeSeries getOHLCTimeSeries() {
		return ohlcTimeSeries;
	}

	@Override
	public IMutableTickTimeSeries getTickTimeSeries() {
		return tickTimeSeries;
	}

	@Override
	public IOHLCTimeSeries getVirtualTimeSeries() {
		return virtualTimeSeries;
	}

	@Override
	public long getTimestamp() {
		return Math.max(ohlcTimeSeries.getTimestamp(), tickTimeSeries.getTimestamp());
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}

	private class SlaveOHLCTimeSeries extends OHLCTimeSeries {
		private static final long serialVersionUID = 2654593577745684284L;

		public SlaveOHLCTimeSeries(IContract contract, DataType dataType, BarSize barSize, boolean includeAfterHours) {
			super(ID, contract, barSize, dataType, includeAfterHours, timeZone);
		}
	}
	
	private class SlaveTickTimeSeries extends TickTimeSeries {
		private static final long serialVersionUID = -8804580947127074390L;

		public SlaveTickTimeSeries(IContract contract, DataType dataType, long interval) {
			super(ID, contract, dataType, interval, timeZone);
		}
	}

	@Override
	public IContract getContract() {
		return contract;
	}

	@Override
	public BarSize getBarSize() {
		return barSize;
	}

	@Override
	public boolean isIncludeAfterHours() {
		return includeAfterHours;
	}

	@Override
	public DataType getDataType() {
		return dataType;
	}

	@Override
	public String getPersistentID() {
		return ID;
	}
	
	public static String stringRepr(IStockDatabase stockDatabase) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("db [");
		buffer.append(ContractBase.stringRepr(stockDatabase.getContract()));
		buffer.append("; ");
		buffer.append(stockDatabase.getDataType().name());
		buffer.append("; ");
		buffer.append(stockDatabase.getBarSize().name());
		buffer.append("; ");
		buffer.append(stockDatabase.getTimeZone().getDisplayName());
		buffer.append("; OTH= ");
		buffer.append(Boolean.toString(stockDatabase.isIncludeAfterHours()));
		buffer.append("]");
		return buffer.toString();
	}
}
