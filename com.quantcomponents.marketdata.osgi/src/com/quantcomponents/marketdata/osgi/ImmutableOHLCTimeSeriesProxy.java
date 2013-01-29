/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata.osgi;

import java.util.Date;
import java.util.TimeZone;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.osgi.ISeriesIteratorHost;
import com.quantcomponents.core.osgi.ISeriesListenerHostLocal;
import com.quantcomponents.core.osgi.ImmutableSeriesProxy;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.IOHLCTimeSeries;

public class ImmutableOHLCTimeSeriesProxy extends ImmutableSeriesProxy<Date, Double, IOHLCPoint> implements IOHLCTimeSeries {

	public ImmutableOHLCTimeSeriesProxy(IOHLCTimeSeriesHost seriesHost, ISeriesIteratorHost<Date, Double, IOHLCPoint> seriesIteratorHost,
			ISeriesListenerHostLocal<Date, Double, IOHLCPoint> listenerHost, ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle) {
		super(seriesHost, seriesIteratorHost, listenerHost, seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IContract getContract() {
		return ((IOHLCTimeSeriesHost) seriesHost).getContract((ServiceHandle<IOHLCTimeSeriesHost>) seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BarSize getBarSize() {
		return ((IOHLCTimeSeriesHost) seriesHost).getBarSize((ServiceHandle<IOHLCTimeSeriesHost>) seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataType getDataType() {
		return ((IOHLCTimeSeriesHost) seriesHost).getDataType((ServiceHandle<IOHLCTimeSeriesHost>) seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isIncludeAfterHours() {
		return ((IOHLCTimeSeriesHost) seriesHost).isIncludeAfterHours((ServiceHandle<IOHLCTimeSeriesHost>) seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public long getInterval() {
		return ((IOHLCTimeSeriesHost) seriesHost).getInterval((ServiceHandle<IOHLCTimeSeriesHost>) seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TimeZone getTimeZone() {
		return ((IOHLCTimeSeriesHost) seriesHost).getTimeZone((ServiceHandle<IOHLCTimeSeriesHost>) seriesHostHandle);
	}

}
