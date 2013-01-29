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

import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.remote.ServiceHandle;

public class SeriesIteratorProxy<A extends Comparable<A>, O extends Comparable<O>, P extends ISeriesPoint<A, O>> implements Iterator<P> {
	private final ISeriesIteratorHost<A, O, P> seriesIteratorHost;
	private final ServiceHandle<ISeriesIteratorHost<A, O, P>> seriesIteratorHostHandle;

	public SeriesIteratorProxy(ISeriesIteratorHost<A, O, P> seriesIteratorHost, ServiceHandle<ISeriesIteratorHost<A, O, P>> seriesIteratorHostHandle) {
		this.seriesIteratorHost = seriesIteratorHost;
		this.seriesIteratorHostHandle = seriesIteratorHostHandle;
	}

	@Override
	public boolean hasNext() {
		return seriesIteratorHost.hasNext(seriesIteratorHostHandle);
	}

	@Override
	public P next() {
		return seriesIteratorHost.next(seriesIteratorHostHandle);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
