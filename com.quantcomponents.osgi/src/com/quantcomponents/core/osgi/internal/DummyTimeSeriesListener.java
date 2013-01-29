/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.osgi.internal;

import java.util.Date;

import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;

public class DummyTimeSeriesListener implements ISeriesListener<Date, Double> {

	@Override
	public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) {
		System.out.println("Update: " + updatedItem);
	}

	@Override
	public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
		System.out.println("Added: " + newItem);
	}

}
