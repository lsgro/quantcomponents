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

import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.remote.ServiceHandle;

public interface ISeriesHost<A extends Comparable<A>, O extends Comparable<O>, P extends ISeriesPoint<A, O>> {
	int size(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle);
	boolean isEmpty(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle);
	ServiceHandle<? extends ISeriesIteratorHost<A, O, P>> iterator(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle);
	ServiceHandle<? extends ISeriesIteratorHost<A, O, P>> descendingIterator(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle);
	P getFirst(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle);
	P getLast(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle);
	P getMinimum(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle);
	P getMaximum(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle);
	long getTimestamp(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle);
	void close(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle);
	boolean isEnforceStrictSequence(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle);
	String getPersistentID(ServiceHandle<? extends ISeriesHost<A, O, P>> seriesHostHandle);
}
