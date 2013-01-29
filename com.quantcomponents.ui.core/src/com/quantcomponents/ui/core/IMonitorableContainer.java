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

public interface IMonitorableContainer<T> {
	Collection<T> getElements();
	void addListener(IMonitorableContainerListener<T> listener);
	void removeListener(IMonitorableContainerListener<T> listener);
	void dispose();
}
