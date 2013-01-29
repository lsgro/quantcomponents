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

import java.util.Date;
import java.util.TimeZone;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.osgi.ISeriesHost;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IOHLCPoint;

public interface IOHLCTimeSeriesHost extends ISeriesHost<Date, Double, IOHLCPoint> {
	long getInterval(ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle);
	TimeZone getTimeZone(ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle);
	IContract getContract(ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle);
	BarSize getBarSize(ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle);
	DataType getDataType(ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle);
	boolean isIncludeAfterHours(ServiceHandle<? extends IOHLCTimeSeriesHost> seriesHostHandle);
}
