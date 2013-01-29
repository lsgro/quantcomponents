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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.remote.IUIDGenerator;
import com.quantcomponents.core.remote.ServiceHandle;

public class SeriesHost<A extends Comparable<A>, O extends Comparable<O>, P extends ISeriesPoint<A, O>> implements ISeriesHost<A, O, P>, ISeriesHostLocal<A, O, P> {
	protected class SeriesInfo {
		public ISeries<A, O, ? extends P> series;
		public ISeriesListener<A, O> listener;
	}
	
	private static final Logger logger = Logger.getLogger(SeriesHost.class.getName());
	private final Map<ServiceHandle<ISeriesHost<A, O, P>>, SeriesInfo> allSeries = new ConcurrentHashMap<ServiceHandle<ISeriesHost<A, O, P>>, SeriesInfo>();
	private volatile IUIDGenerator uidGenerator;
	private volatile ISeriesIteratorHostLocal<A, O, P> seriesIteratorHost;
	private volatile ISeriesListenerHost<A, O, P> seriesListenerHost; // optional service
	
	public SeriesHost() {}

	public void deactivate() {
		allSeries.clear();
	}
	
	public void setUidGenerator(IUIDGenerator uidGenerator) {
		this.uidGenerator = uidGenerator;
	}

	public void setSeriesIteratorHost(ISeriesIteratorHostLocal<A, O, P> seriesIteratorHost) {
		this.seriesIteratorHost = seriesIteratorHost;
	}

	public void setSeriesListenerHost(ISeriesListenerHost<A, O, P> seriesListenerHost) {
		this.seriesListenerHost = seriesListenerHost;
	}

	public void resetSeriesListenerHost(ISeriesListenerHost<A, O, P> seriesListenerHost) {
		if (this.seriesListenerHost == seriesListenerHost) {
			this.seriesListenerHost = null;
		} else {
			logger.log(Level.WARNING, " WEIRD: ------>>>>> SeriesHost: " + this + "; resetting seriesListenerHost: " + seriesListenerHost + " different from: " + this.seriesListenerHost);
		}
	}
	
	@Override
	public int size(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		return retrieveSeriesInfo(seriesHostHandle).series.size();
	}

	@Override
	public boolean isEmpty(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		return retrieveSeriesInfo(seriesHostHandle).series.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ServiceHandle<? extends ISeriesIteratorHost<A, O, P>> iterator(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		Iterator<? extends P> iterator = retrieveSeriesInfo(seriesHostHandle).series.iterator();
		ServiceHandle<ISeriesIteratorHost<A, O, P>> iteratorHostHandle = seriesIteratorHost.addIterator((Iterator<P>) iterator);
		return iteratorHostHandle;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ServiceHandle<ISeriesIteratorHost<A, O, P>> descendingIterator(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		Iterator<P> iterator = (Iterator<P>) retrieveSeriesInfo(seriesHostHandle).series.descendingIterator();
		ServiceHandle<ISeriesIteratorHost<A, O, P>> iteratorHostHandle = seriesIteratorHost.addIterator(iterator);
		return iteratorHostHandle;
	}

	@Override
	public P getFirst(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		return retrieveSeriesInfo(seriesHostHandle).series.getFirst();
	}

	@Override
	public P getLast(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		return retrieveSeriesInfo(seriesHostHandle).series.getLast();
	}

	@Override
	public P getMinimum(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		return retrieveSeriesInfo(seriesHostHandle).series.getMinimum();
	}

	@Override
	public P getMaximum(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		return retrieveSeriesInfo(seriesHostHandle).series.getMaximum();
	}

	@Override
	public long getTimestamp(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		return retrieveSeriesInfo(seriesHostHandle).series.getTimestamp();
	}

	@Override
	public void close(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		allSeries.remove(seriesHostHandle);
	}

	@Override
	public ServiceHandle<ISeriesHost<A, O, P>> addSeries(ISeries<A, O, ? extends P> series) {
		final ServiceHandle<ISeriesHost<A, O, P>> seriesHandle = new ServiceHandle<ISeriesHost<A, O, P>>(uidGenerator.nextUID());
		SeriesInfo seriesInfo = new SeriesInfo();
		seriesInfo.series = series;
		seriesInfo.listener = new ISeriesListener<A, O>() {
			@Override
			public void onItemUpdated(ISeriesPoint<A, O> existingItem, ISeriesPoint<A, O> updatedItem) {
				if (seriesListenerHost != null) {
					seriesListenerHost.onItemUpdated(seriesHandle, existingItem, updatedItem);
				}
			}
			@Override
			public void onItemAdded(ISeriesPoint<A, O> newItem) {
				if (seriesListenerHost != null) {
					seriesListenerHost.onItemAdded(seriesHandle, newItem);
				}
			}};
		allSeries.put(seriesHandle, seriesInfo);
		seriesInfo.series.addSeriesListener(seriesInfo.listener);
		return seriesHandle;
	}

	@Override
	public void removeSeries(ServiceHandle<? extends ISeriesHost<A, O, ? extends P>> seriesHostHandle) {
		SeriesInfo seriesInfo = retrieveSeriesInfo(seriesHostHandle);
		seriesInfo.series.removeSeriesListener(seriesInfo.listener);
		allSeries.remove(seriesHostHandle);
	}
	
	protected SeriesInfo retrieveSeriesInfo(ServiceHandle<? extends ISeriesHost<A, O, ? extends P>> seriesHostHandle) {
		SeriesInfo seriesInfo = allSeries.get(seriesHostHandle);
		if (seriesInfo == null) {
			throw new IllegalArgumentException("LinkedListSeries for handle: " + seriesHostHandle + " not found");
		}
		return seriesInfo;
	}

	@Override
	public boolean isEnforceStrictSequence(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		return retrieveSeriesInfo(seriesHostHandle).series.isEnforceStrictSequence();
	}

	@Override
	public String getPersistentID(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		return retrieveSeriesInfo(seriesHostHandle).series.getPersistentID();
	}
}
