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

import java.util.TimeZone;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;


/**
 * 
 * Implementation of a mutable OHLC time series
 */
public class OHLCTimeSeries extends TimeSeries<IOHLCPoint> implements IMutableOHLCTimeSeries {
	private static final long serialVersionUID = 6450411366998550622L;
	private final IContract contract;
	private final BarSize barSize;
	private final DataType dataType;
	private final boolean includeAfterHours;
	
	public OHLCTimeSeries(String ID, IContract contract, BarSize barSize, DataType dataType, boolean includeAfterHours, TimeZone timeZone) {
		super(ID, timeZone, barSize.getDurationInMs(), true);
		this.contract = contract;
		this.barSize = barSize;
		this.dataType = dataType;
		this.includeAfterHours = includeAfterHours;
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
	public DataType getDataType() {
		return dataType;
	}
	
	@Override
	public boolean isIncludeAfterHours() {
		return includeAfterHours;
	}
	
	@Override
	public void addLastIfNotExists(IOHLCPoint item) {
		checkBarSize(item);
		super.addLastIfNotExists(item);
	}
	
	@Override
	public void addFirstIfNotExists(IOHLCPoint item) {
		checkBarSize(item);
		super.addFirstIfNotExists(item);
	}
	
	@Override
	public void addLast(IOHLCPoint item) {
		checkBarSize(item);
		super.addLast(item);
	}
	
	@Override
	public void addFirst(IOHLCPoint item) {
		checkBarSize(item);
		super.addFirst(item);
	}
	
	@Override
	public void updateTail(IOHLCPoint item) {
		checkBarSize(item);
		super.updateTail(item);
	}

	@Override
	public OHLCTimeSeries createEmptyMutableSeries(String ID) {
		return new OHLCTimeSeries(ID, getContract(), getBarSize(), getDataType(), isIncludeAfterHours(), getTimeZone());
	}
	
	private void checkBarSize(IOHLCPoint item) {
		if (!getBarSize().equals(item.getBarSize())) {
			throw new IllegalArgumentException("Wrong bar size: " + item.getBarSize() + " - series bar size is: " + getBarSize());
		}
	}
}
