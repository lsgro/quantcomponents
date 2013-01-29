/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.osgi;

import java.util.Iterator;

import com.quantcomponents.core.model.IMutableSeries;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.remote.ServiceHandle;

public class ImmutableSeriesProxy<A extends Comparable<A>, O extends Comparable<O>, P extends ISeriesPoint<A, O>> implements ISeries<A, O, P> {
	protected final ISeriesHost<A, O, P> seriesHost;
	private final ISeriesIteratorHost<A, O, P> seriesIteratorHost;
	private final ISeriesListenerHostLocal<A, O, P> listenerHost;
	protected final ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle;
	
	public ImmutableSeriesProxy(ISeriesHost<A, O, P> seriesHost, ISeriesIteratorHost<A, O, P> seriesIteratorHost, ISeriesListenerHostLocal<A, O, P> listenerHost,
			ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		this.seriesHost = seriesHost;
		this.seriesIteratorHost = seriesIteratorHost;
		this.listenerHost = listenerHost;
		this.seriesHostHandle = seriesHostHandle;
	}

	@Override
	public int size() {
		return seriesHost.size(seriesHostHandle);
	}

	@Override
	public boolean isEmpty() {
		return seriesHost.isEmpty(seriesHostHandle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<P> iterator() {
		return new SeriesIteratorProxy<A, O, P>(seriesIteratorHost, (ServiceHandle<ISeriesIteratorHost<A, O, P>>) seriesHost.iterator(seriesHostHandle));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<P> descendingIterator() {
		return new SeriesIteratorProxy<A, O, P>(seriesIteratorHost, (ServiceHandle<ISeriesIteratorHost<A, O, P>>) seriesHost.descendingIterator(seriesHostHandle));
	}

	@Override
	public P getFirst() {
		return seriesHost.getFirst(seriesHostHandle);
	}

	@Override
	public P getLast() {
		return seriesHost.getLast(seriesHostHandle);
	}

	@Override
	public P getMinimum() {
		return seriesHost.getMinimum(seriesHostHandle);
	}

	@Override
	public P getMaximum() {
		return seriesHost.getMaximum(seriesHostHandle);
	}

	@Override
	public void addSeriesListener(ISeriesListener<A, O> listener) {
		listenerHost.addListener(seriesHostHandle, listener);
	}

	@Override
	public void removeSeriesListener(ISeriesListener<A, O> listener) {
		listenerHost.removeListener(seriesHostHandle, listener);
	}

	@Override
	public long getTimestamp() {
		return seriesHost.getTimestamp(seriesHostHandle);
	}

	@Override
	public boolean isEnforceStrictSequence() {
		return seriesHost.isEnforceStrictSequence(seriesHostHandle);
	}

	@Override
	public String getPersistentID() {
		return seriesHost.getPersistentID(seriesHostHandle);
	}

	@Override
	public IMutableSeries<A, O, P> createEmptyMutableSeries(String ID) {
		throw new UnsupportedOperationException();
	}
}
