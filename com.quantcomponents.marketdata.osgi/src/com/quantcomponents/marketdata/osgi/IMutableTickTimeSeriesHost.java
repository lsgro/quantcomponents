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

import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.osgi.ISeriesHost;
import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.ITickPoint;

public interface IMutableTickTimeSeriesHost extends ISeriesHost<Date, Double, ITickPoint> {
	long getInterval(ServiceHandle<? extends IMutableTickTimeSeriesHost> timeSeriesHostHandle);
	TimeZone getTimeZone(ServiceHandle<? extends IMutableTickTimeSeriesHost> timeSeriesHostHandle);
	IContract getContract(ServiceHandle<? extends IMutableTickTimeSeriesHost> timeSeriesHostHandle);
	DataType getDataType(ServiceHandle<? extends IMutableTickTimeSeriesHost> timeSeriesHostHandle);
	void addLastItem(ServiceHandle<? extends IMutableTickTimeSeriesHost> timeSeriesHostHandle, ITickPoint tick);
	void addFirstItem(ServiceHandle<? extends IMutableTickTimeSeriesHost> timeSeriesHostHandle, ITickPoint tick);
}
