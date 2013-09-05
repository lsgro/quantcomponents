/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata.osgi.internal;

import java.net.ConnectException;
import java.util.Date;
import java.util.List;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.IRealTimeMarketDataProvider;

public class MarketDataProviderShellProxy implements IRealTimeMarketDataProvider {
	private final IRealTimeMarketDataProvider service;

	public MarketDataProviderShellProxy(IRealTimeMarketDataProvider service) {
		this.service = service;
	}

	@Override
	public DataType[] availableDataTypes() {
		return service.availableDataTypes();
	}

	@Override
	public BarSize[] availableBarSizes() {
		return service.availableBarSizes();
	}

	@Override
	public List<IContract> searchContracts(IContract criteria, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		return service.searchContracts(criteria, taskMonitor);
	}

	@Override
	public List<IOHLCPoint> historicalBars(IContract contract, Date startDateTime, Date endDateTime, BarSize barSize, DataType dataType, boolean includeAfterHours, ITaskMonitor taskMonitor)
			throws ConnectException, RequestFailedException {
		return service.historicalBars(contract, startDateTime, endDateTime, barSize, dataType, includeAfterHours, taskMonitor);
	}

	@Override
	public void startRealTimeBars(IContract contract, BarSize barSize, DataType dataType, boolean includeAfterHours, IRealTimeDataListener listener) throws ConnectException,
			RequestFailedException {
		service.startRealTimeBars(contract, barSize, dataType, includeAfterHours, listener);
	}

	@Override
	public void stopRealTimeBars(IContract contract, BarSize barSize, DataType dataType, boolean includeAfterHours, IRealTimeDataListener listener) throws ConnectException {
		service.stopRealTimeBars(contract, barSize, dataType, includeAfterHours, listener);
	}

	@Override
	public void startTicks(IContract contract, ITickListener listener) throws ConnectException, RequestFailedException {
		service.startTicks(contract, listener);
	}

	@Override
	public void stopTicks(IContract contract, ITickListener listener) throws ConnectException {
		service.stopTicks(contract, listener);
	}
}
