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

import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;

/**
 * Specialized time series for tick data
 */
public class TickTimeSeries extends TimeSeries<ITickPoint> implements IMutableTickTimeSeries {
	private static final long serialVersionUID = 7342508036950877727L;
	private final IContract contract;
	private final DataType dataType;

	public TickTimeSeries(String ID, IContract contract, DataType dataType, long interval, TimeZone timeZone) {
		super(ID, timeZone, interval, false);
		this.contract = contract;
		this.dataType = dataType;
	}

	@Override
	public IContract getContract() {
		return contract;
	}

	@Override
	public DataType getDataType() {
		return dataType;
	}

	@Override
	public void addLast(ITickPoint tick) {
		super.addLast(tick);
	}
	
	@Override
	public TickTimeSeries createEmptyMutableSeries(String ID) {
		return new TickTimeSeries(ID, getContract(), getDataType(), getInterval(), getTimeZone());
	}
}
