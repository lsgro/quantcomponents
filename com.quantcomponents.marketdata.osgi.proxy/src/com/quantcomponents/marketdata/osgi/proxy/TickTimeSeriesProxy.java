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
import java.util.TimeZone;

import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.osgi.ISeriesIteratorHost;
import com.quantcomponents.core.osgi.ISeriesListenerHostLocal;
import com.quantcomponents.core.osgi.ImmutableSeriesProxy;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IMutableTickTimeSeries;
import com.quantcomponents.marketdata.ITickPoint;
import com.quantcomponents.marketdata.osgi.IMutableTickTimeSeriesHost;

public class TickTimeSeriesProxy extends ImmutableSeriesProxy<java.util.Date, Double, ITickPoint> implements IMutableTickTimeSeries {

	public TickTimeSeriesProxy(IMutableTickTimeSeriesHost seriesHost, ISeriesIteratorHost<Date, Double, ITickPoint> seriesIteratorHost,
			ISeriesListenerHostLocal<Date, Double, ITickPoint> listenerHost, ServiceHandle<IMutableTickTimeSeriesHost> seriesHostHandle) {
		super(seriesHost, seriesIteratorHost, listenerHost, seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IContract getContract() {
		return ((IMutableTickTimeSeriesHost) seriesHost).getContract((ServiceHandle<IMutableTickTimeSeriesHost>) seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataType getDataType() {
		return ((IMutableTickTimeSeriesHost) seriesHost).getDataType((ServiceHandle<IMutableTickTimeSeriesHost>) seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public long getInterval() {
		return ((IMutableTickTimeSeriesHost) seriesHost).getInterval((ServiceHandle<IMutableTickTimeSeriesHost>) seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TimeZone getTimeZone() {
		return ((IMutableTickTimeSeriesHost) seriesHost).getTimeZone((ServiceHandle<IMutableTickTimeSeriesHost>) seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addLast(ITickPoint tick) {
		((IMutableTickTimeSeriesHost) seriesHost).addLastItem((ServiceHandle<IMutableTickTimeSeriesHost>) seriesHostHandle, tick);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addFirst(ITickPoint tick) {
		((IMutableTickTimeSeriesHost) seriesHost).addFirstItem((ServiceHandle<IMutableTickTimeSeriesHost>) seriesHostHandle, tick);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addLastIfNotExists(ITickPoint item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addFirstIfNotExists(ITickPoint item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insertFromTail(ITickPoint item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateTail(ITickPoint item) {
		throw new UnsupportedOperationException();
	}
}
