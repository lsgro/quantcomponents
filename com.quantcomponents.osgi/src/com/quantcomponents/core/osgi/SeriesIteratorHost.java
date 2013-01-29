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
import java.util.logging.Logger;

import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.remote.IUIDGenerator;
import com.quantcomponents.core.remote.ServiceHandle;

public class SeriesIteratorHost<A extends Comparable<A>, O extends Comparable<O>, P extends ISeriesPoint<A, O>> implements ISeriesIteratorHost<A, O, P>, ISeriesIteratorHostLocal<A, O, P> {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SeriesIteratorHost.class.getName());
	private final Map<ServiceHandle<ISeriesIteratorHost<A, O, P>>, Iterator<P>> allIterators = new ConcurrentHashMap<ServiceHandle<ISeriesIteratorHost<A, O, P>>, Iterator<P>>();
	private volatile IUIDGenerator uidGenerator;
	
	public SeriesIteratorHost() {}
	
	public void deactivate() {
		allIterators.clear();
	}
	
	public void setUidGenerator(IUIDGenerator uidGenerator) {
		this.uidGenerator = uidGenerator;
	}

	@Override
	public ServiceHandle<ISeriesIteratorHost<A, O, P>> addIterator(Iterator<P> iterator) {
		ServiceHandle<ISeriesIteratorHost<A, O, P>> handle = new ServiceHandle<ISeriesIteratorHost<A, O, P>>(uidGenerator.nextUID());
		allIterators.put(handle, iterator);
		return handle;
	}
	
	@Override
	public void removeIterator(ServiceHandle<ISeriesIteratorHost<A, O, P>> iteratorHostHandle) {
		allIterators.remove(iteratorHostHandle);
	}
	
	@Override
	public boolean hasNext(ServiceHandle<ISeriesIteratorHost<A, O, P>> iteratorHostHandle) {
		Iterator<P> iterator = allIterators.get(iteratorHostHandle);
		if (iterator == null) {
			throw new IllegalArgumentException("Iterator for handle: " + iteratorHostHandle + " not found");
		}
		return iterator.hasNext();
	}

	@Override
	public P next(ServiceHandle<ISeriesIteratorHost<A, O, P>> iteratorHostHandle) {
		Iterator<P> iterator = allIterators.get(iteratorHostHandle);
		if (iterator == null) {
			throw new IllegalArgumentException("Iterator for handle: " + iteratorHostHandle + " not found");
		}
		return iterator.next();
	}

}
