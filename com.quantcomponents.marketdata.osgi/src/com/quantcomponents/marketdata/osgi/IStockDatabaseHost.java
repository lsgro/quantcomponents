/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata.osgi;

import java.util.TimeZone;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.remote.ServiceHandle;

public interface IStockDatabaseHost {
	ServiceHandle<IMutableOHLCTimeSeriesHost> getOHLCTimeSeries(ServiceHandle<IStockDatabaseHost> stockDbHandle);
	ServiceHandle<IMutableTickTimeSeriesHost> getTickTimeSeries(ServiceHandle<IStockDatabaseHost> stockDbHandle);
	ServiceHandle<IOHLCTimeSeriesHost> getVirtualTimeSeries(ServiceHandle<IStockDatabaseHost> stockDbHandle);
	long getTimestamp(ServiceHandle<IStockDatabaseHost> stockDbHandle);
	IContract getContract(ServiceHandle<IStockDatabaseHost> stockDbHandle);
	BarSize getBarSize(ServiceHandle<IStockDatabaseHost> stockDbHandle);
	DataType getDataType(ServiceHandle<IStockDatabaseHost> stockDbHandle);
	boolean isIncludeAfterHours(ServiceHandle<IStockDatabaseHost> stockDbHandle);
	TimeZone getTimeZone(ServiceHandle<IStockDatabaseHost> stockDbHandle);
	String getPersistentID(ServiceHandle<IStockDatabaseHost> stockDbHandle);
}
