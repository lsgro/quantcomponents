/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata.osgi.host;

import java.util.Date;
import java.util.TimeZone;

import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.osgi.SeriesHost;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IMutableTickTimeSeries;
import com.quantcomponents.marketdata.ITickPoint;
import com.quantcomponents.marketdata.osgi.IMutableTickTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.ITickTimeSeriesHostLocal;

public class MutableTickTimeSeriesHost extends SeriesHost<Date, Double, ITickPoint> implements IMutableTickTimeSeriesHost, ITickTimeSeriesHostLocal {
	
	public MutableTickTimeSeriesHost() {} 

	@Override
	public long getInterval(ServiceHandle<? extends IMutableTickTimeSeriesHost> timeSeriesHostHandle) {
		return ((IMutableTickTimeSeries) retrieveSeriesInfo(timeSeriesHostHandle).series).getInterval();
	}

	@Override
	public TimeZone getTimeZone(ServiceHandle<? extends IMutableTickTimeSeriesHost> timeSeriesHostHandle) {
		return ((IMutableTickTimeSeries) retrieveSeriesInfo(timeSeriesHostHandle).series).getTimeZone();
	}

	@Override
	public IContract getContract(ServiceHandle<? extends IMutableTickTimeSeriesHost> timeSeriesHostHandle) {
		return ((IMutableTickTimeSeries) retrieveSeriesInfo(timeSeriesHostHandle).series).getContract();
	}

	@Override
	public DataType getDataType(ServiceHandle<? extends IMutableTickTimeSeriesHost> timeSeriesHostHandle) {
		return ((IMutableTickTimeSeries) retrieveSeriesInfo(timeSeriesHostHandle).series).getDataType();
	}

	@Override
	public void addLastItem(ServiceHandle<? extends IMutableTickTimeSeriesHost> timeSeriesHostHandle, ITickPoint tick) {
		((IMutableTickTimeSeries) retrieveSeriesInfo(timeSeriesHostHandle).series).addLast(tick);
	}

	@Override
	public void addFirstItem(ServiceHandle<? extends IMutableTickTimeSeriesHost> timeSeriesHostHandle, ITickPoint tick) {
		((IMutableTickTimeSeries) retrieveSeriesInfo(timeSeriesHostHandle).series).addFirst(tick);
	}
}
