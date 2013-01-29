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

import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.remote.ServiceHandle;

public class SeriesListenerProxy<A extends Comparable<A>, O extends Comparable<O>, P extends ISeriesPoint<A, O>> implements ISeriesListener<A, O> {
	private final ISeriesListenerHost<A, O, P> seriesListenerHost;
	private final ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle;
	
	public SeriesListenerProxy(ISeriesListenerHost<A, O, P> seriesListenerHost, ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle) {
		this.seriesListenerHost = seriesListenerHost;
		this.seriesHostHandle = seriesHostHandle;
	}

	@Override
	public void onItemUpdated(ISeriesPoint<A, O> existingItem, ISeriesPoint<A, O> updatedItem) {
		seriesListenerHost.onItemUpdated(seriesHostHandle, existingItem, updatedItem);
	}

	@Override
	public void onItemAdded(ISeriesPoint<A, O> newItem) {
		seriesListenerHost.onItemAdded(seriesHostHandle, newItem);
	}
}
