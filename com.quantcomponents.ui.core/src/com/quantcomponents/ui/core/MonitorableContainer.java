/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ui.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class MonitorableContainer<T> implements IMutableMonitorableContainer<T, T> {
	private final Set<T> elements = new CopyOnWriteArraySet<T>(); 
	private final Set<IMonitorableContainerListener<T>> listeners = new CopyOnWriteArraySet<IMonitorableContainerListener<T>>(); 

	@Override
	public Collection<T> getElements() {
		return Collections.unmodifiableCollection(elements);
	}

	@Override
	public void addElement(T element) {
		elements.add(element);
		for (IMonitorableContainerListener<T> listener : listeners) {
			listener.onElementAdded(element);
		}
	}

	@Override
	public boolean removeElement(T element) {
		boolean removed = elements.remove(element);
		for (IMonitorableContainerListener<T> listener : listeners) {
			listener.onElementRemoved(element);
		}
		return removed;
	}

	@Override
	public boolean removeWrapper(T element) {
		return removeElement(element);
	}

	@Override
	public void addListener(IMonitorableContainerListener<T> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(IMonitorableContainerListener<T> listener) {
		listeners.remove(listener);
	}

	@Override
	public void dispose() {
		elements.clear();
		listeners.clear();
	}

}
