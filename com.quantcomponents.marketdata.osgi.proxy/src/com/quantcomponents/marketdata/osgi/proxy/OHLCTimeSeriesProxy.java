/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata.osgi.proxy;

import java.util.Date;

import com.quantcomponents.core.osgi.ISeriesIteratorHost;
import com.quantcomponents.core.osgi.ISeriesListenerHostLocal;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IMutableOHLCTimeSeries;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.osgi.IMutableOHLCTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.ImmutableOHLCTimeSeriesProxy;

public class OHLCTimeSeriesProxy extends ImmutableOHLCTimeSeriesProxy implements IMutableOHLCTimeSeries {

	public OHLCTimeSeriesProxy(IMutableOHLCTimeSeriesHost seriesHost, ISeriesIteratorHost<Date, Double, IOHLCPoint> seriesIteratorHost,
			ISeriesListenerHostLocal<Date, Double, IOHLCPoint> listenerHost, ServiceHandle<IMutableOHLCTimeSeriesHost> seriesHostHandle) {
		super(seriesHost, seriesIteratorHost, listenerHost, seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addLast(IOHLCPoint item) {
		((IMutableOHLCTimeSeriesHost) seriesHost).addLastItem((ServiceHandle<IMutableOHLCTimeSeriesHost>) seriesHostHandle, item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addFirst(IOHLCPoint item) {
		((IMutableOHLCTimeSeriesHost) seriesHost).addFirstItem((ServiceHandle<IMutableOHLCTimeSeriesHost>) seriesHostHandle, item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addLastIfNotExists(IOHLCPoint item) {
		((IMutableOHLCTimeSeriesHost) seriesHost).addLastItemIfNotExists((ServiceHandle<IMutableOHLCTimeSeriesHost>) seriesHostHandle, item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addFirstIfNotExists(IOHLCPoint item) {
		((IMutableOHLCTimeSeriesHost) seriesHost).addFirstItemIfNotExists((ServiceHandle<IMutableOHLCTimeSeriesHost>) seriesHostHandle, item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTail(IOHLCPoint item) {
		((IMutableOHLCTimeSeriesHost) seriesHost).updateItem((ServiceHandle<IMutableOHLCTimeSeriesHost>) seriesHostHandle, item);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void insertFromTail(IOHLCPoint item) {
		((IMutableOHLCTimeSeriesHost) seriesHost).insertFromTail((ServiceHandle<IMutableOHLCTimeSeriesHost>) seriesHostHandle, item);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

}
