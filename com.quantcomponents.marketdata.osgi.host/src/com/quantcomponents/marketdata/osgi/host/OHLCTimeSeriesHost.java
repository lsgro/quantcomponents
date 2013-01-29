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

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.osgi.SeriesHost;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.IOHLCTimeSeries;
import com.quantcomponents.marketdata.osgi.IOHLCTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.IOHLCTimeSeriesHostLocal;

public class OHLCTimeSeriesHost extends SeriesHost<Date, Double, IOHLCPoint> implements IOHLCTimeSeriesHost, IOHLCTimeSeriesHostLocal {

	public OHLCTimeSeriesHost() {}
	
	@Override
	public long getInterval(ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle) {
		return ((IOHLCTimeSeries) retrieveSeriesInfo(seriesHostHandle).series).getInterval();
	}

	@Override
	public TimeZone getTimeZone(ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle) {
		return ((IOHLCTimeSeries) retrieveSeriesInfo(seriesHostHandle).series).getTimeZone();
	}

	@Override
	public IContract getContract(ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle) {
		return ((IOHLCTimeSeries) retrieveSeriesInfo(seriesHostHandle).series).getContract();
	}

	@Override
	public BarSize getBarSize(ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle) {
		return ((IOHLCTimeSeries) retrieveSeriesInfo(seriesHostHandle).series).getBarSize();
	}

	@Override
	public DataType getDataType(ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle) {
		return ((IOHLCTimeSeries) retrieveSeriesInfo(seriesHostHandle).series).getDataType();
	}

	@Override
	public boolean isIncludeAfterHours(ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle) {
		return ((IOHLCTimeSeries) retrieveSeriesInfo(seriesHostHandle).series).isIncludeAfterHours();
	}
	
}
