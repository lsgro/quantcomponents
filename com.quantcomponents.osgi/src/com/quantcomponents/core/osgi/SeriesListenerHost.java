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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.remote.ServiceHandle;

public class SeriesListenerHost<A extends Comparable<A>, O extends Comparable<O>, P extends ISeriesPoint<A, O>> implements ISeriesListenerHost<A, O, P>, ISeriesListenerHostLocal<A, O, P> {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SeriesListenerHost.class.getName());
	private final Map<ServiceHandle<ISeriesHost<A, O, P>>, Set<ISeriesListener<A, O>>> allListeners = new ConcurrentHashMap<ServiceHandle<ISeriesHost<A, O, P>>, Set<ISeriesListener<A, O>>>();
	
	public void deactivate() {
		allListeners.clear();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized void addListener(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle, ISeriesListener<A, O> listener) {
		Set<ISeriesListener<A, O>> listenerSet = allListeners.get((ServiceHandle<ISeriesHost<A, O, P>>) seriesHostHandle);
		if (listenerSet == null) {
			listenerSet = new CopyOnWriteArraySet<ISeriesListener<A, O>>();
			allListeners.put((ServiceHandle<ISeriesHost<A, O, P>>) seriesHostHandle, listenerSet);
		}
		listenerSet.add(listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void removeListener(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle, ISeriesListener<A, O> listener) {
		Set<ISeriesListener<A, O>> listenerSet = allListeners.get((ServiceHandle<ISeriesHost<A, O, P>>) seriesHostHandle);
		if (listenerSet != null) {
			listenerSet.remove(listener);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onItemUpdated(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle, ISeriesPoint<A, O> existingItem, ISeriesPoint<A, O> updatedItem) {
		Set<ISeriesListener<A, O>> listenerSet = allListeners.get((ServiceHandle<ISeriesHost<A, O, P>>) seriesHostHandle);
		if (listenerSet != null) {
			for (ISeriesListener<A, O> listener : listenerSet) {
				listener.onItemUpdated(existingItem, updatedItem);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemAdded(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle, ISeriesPoint<A, O> newItem) {
		Set<ISeriesListener<A, O>> listenerSet = allListeners.get((ServiceHandle<ISeriesHost<A, O, P>>) seriesHostHandle);
		if (listenerSet != null) {
			for (ISeriesListener<A, O> listener : listenerSet) {
				listener.onItemAdded(newItem);
			}
		}
	}
}
