/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata;

import com.quantcomponents.core.model.DataType;

/**
 * Time series containing only tick data points
 */
public interface ITickTimeSeries extends ITimeSeries<ITickPoint>, IStockDataCollection {
	/**
	 * Returns the data type of the ticks
	 */
	DataType getDataType();
}
