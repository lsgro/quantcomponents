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

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.IPrettyNamed;
import com.quantcomponents.core.remote.ITaskMonitorHost;
import com.quantcomponents.core.remote.ServiceHandle;

public interface IRealTimeMarketDataManagerHost extends IMarketDataManagerHost, IPrettyNamed {
	void startRealtimeUpdate(ServiceHandle<IStockDatabaseHost> stockDbHandle, boolean fillHistoricalGap, ServiceHandle<ITaskMonitorHost> taskMonitorHandle) throws ConnectException, RequestFailedException;
	void stopRealtimeUpdate(ServiceHandle<IStockDatabaseHost> stockDbHandle) throws ConnectException, RequestFailedException;
	boolean isRealtimeUpdate(ServiceHandle<IStockDatabaseHost> stockDbHandle) throws ConnectException, RequestFailedException;
}
