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

import java.net.ConnectException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.IPrettyNamed;
import com.quantcomponents.core.remote.ITaskMonitorHost;
import com.quantcomponents.core.remote.ServiceHandle;

public interface IMarketDataManagerHost extends IPrettyNamed {
	DataType[] availableDataTypes();
	BarSize[] availableBarSizes();
	List<IContract> searchContracts(IContract criteria, ServiceHandle<ITaskMonitorHost> taskMonitorHandle) throws ConnectException, RequestFailedException;
	Collection<ServiceHandle<IStockDatabaseHost>> getAllStockDatabases();
	ServiceHandle<IStockDatabaseHost> findStockDatabase(IContract contract, DataType dataType, BarSize barSize, Boolean includeAfterHours);
	ServiceHandle<IStockDatabaseHost> getStockDatabase(String ID) throws ConnectException, RequestFailedException;
	int numberOfStockDatabases();
	ServiceHandle<IStockDatabaseHost> createStockDatabase(IContract contract, DataType dataType, BarSize barSize, boolean includeAfterHours, TimeZone timeZone);
	void removeStockDatabase(ServiceHandle<IStockDatabaseHost> stockDbHandle) throws ConnectException, RequestFailedException;
	void fillHistoricalData(ServiceHandle<IStockDatabaseHost> stockDbHandle, Date startDate, Date endDate, ServiceHandle<ITaskMonitorHost> taskMonitorHandle) throws ConnectException, RequestFailedException;
}
