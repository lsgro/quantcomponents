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

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;


/**
 * Time series containing only OHLC points
 */
public interface IOHLCTimeSeries extends ITimeSeries<IOHLCPoint>, IStockDataCollection {
	/**
	 * The bar size of the contained points
	 */
	BarSize getBarSize();
	/**
	 * The data type of the contained points
	 */
	DataType getDataType();
	/**
	 * Returns true if the data series contains bars from after hours trading period, false otherwise
	 */
	boolean isIncludeAfterHours();
}
