/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ib;

import java.net.ConnectException;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.ConfigurationException;

import com.quantcomponents.algo.IExecutionService;
import com.quantcomponents.algo.IOrder;
import com.quantcomponents.algo.IOrderStatusListener;
import com.quantcomponents.algo.IPositionListener;
import com.quantcomponents.algo.ITrade;
import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.marketdata.IMarketDataProvider;
import com.quantcomponents.marketdata.IOHLCPoint;

public class IBAdapterComponent implements IMarketDataProvider, IExecutionService {
	private static final Logger logger = Logger.getLogger(IBAdapter.class.getName());
	public static final String HOST_KEY = "host";
	public static final String PORT_KEY = "port";
	public static final String CLIENT_ID_KEY = "clientId";
	public static final String ACCOUNT_ID_KEY = "accountId";
	public static final String FIRST_REQUEST_NO_KEY = "firstRequestNo";
	public static final String NO_MKT_DATA_LINES_KEY = "noMktDataLinesKey";
	
	private IBAdapter ibAdapter;

	public void activate(Map<?,?> properties) throws ConfigurationException {
		logger.log(Level.INFO, "Received new configuration: ");
		for (Object key : properties.keySet()) {
			logger.log(Level.INFO, key + " -> " + properties.get(key));
		}

		String host = (String) properties.get(HOST_KEY);
		if (host == null) {
			throw new ConfigurationException("Property not set: " + HOST_KEY);
		}

		Object portValue = properties.get(PORT_KEY);
		if (portValue == null) {
			throw new ConfigurationException("Property not set: " + PORT_KEY);
		}
		Integer port = parsePropertyValue(portValue);

		Object clientIdValue = properties.get(CLIENT_ID_KEY);
		if (clientIdValue == null) {
			throw new ConfigurationException("Property not set: " + CLIENT_ID_KEY);
		}
		Integer clientId = parsePropertyValue(clientIdValue);

		String accountId = (String) properties.get(ACCOUNT_ID_KEY);
		if (accountId == null) {
			throw new ConfigurationException("Property not set: " + ACCOUNT_ID_KEY);
		}

		Object firstRequestNoValue = properties.get(FIRST_REQUEST_NO_KEY);
		if (firstRequestNoValue == null) {
			firstRequestNoValue = 0;
		}
		Integer firstRequestNo = parsePropertyValue(firstRequestNoValue);

		Object noMktDataLinesKeyValue = properties.get(NO_MKT_DATA_LINES_KEY);
		if (noMktDataLinesKeyValue == null) {
			noMktDataLinesKeyValue = 0;
		}
		Integer noMktDataLinesKey = parsePropertyValue(noMktDataLinesKeyValue);
	
		ibAdapter = new IBAdapter(host, port, clientId, firstRequestNo, noMktDataLinesKey, accountId);
	}
	
	public void deactivate() {
		ibAdapter.disconnect();
	}
	
	private Integer parsePropertyValue(Object propertyValue) {
		if (propertyValue instanceof Integer) {
			return (Integer) propertyValue;
		} else if (propertyValue instanceof String) {
			String str = (String) propertyValue;
			return Integer.parseInt(str);
		}
		return null;
	}
	
	@Override
	public Deque<ITrade> getTrades() throws ConnectException, RequestFailedException {
		return ibAdapter.getTrades();
	}

	@Override
	public List<IContract> searchContracts(IContract criteria, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		return ibAdapter.searchContracts(criteria, taskMonitor);
	}

	@Override
	public List<IOHLCPoint> historicalBars(IContract contract, Date startDateTime, Date endDateTime, BarSize barSize, DataType dataType,
			boolean includeAfterHours, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		return ibAdapter.historicalBars(contract, startDateTime, endDateTime, barSize, dataType, includeAfterHours, taskMonitor);
	}

	@Override
	public void startRealTimeBars(IContract contract, BarSize barSize, DataType dataType, boolean includeAfterHours, IRealTimeDataListener listener)
			throws ConnectException, RequestFailedException {
		ibAdapter.startRealTimeBars(contract, barSize, dataType, includeAfterHours, listener);
	}

	@Override
	public void stopRealTimeBars(IContract contract, BarSize barSize, DataType dataType, boolean includeAfterHours, IRealTimeDataListener listener)
			throws ConnectException {
		ibAdapter.stopRealTimeBars(contract, barSize, dataType, includeAfterHours, listener);
	}

	@Override
	public void startTicks(IContract contract, ITickListener listener) throws ConnectException, RequestFailedException {
		ibAdapter.startTicks(contract, listener);
	}

	@Override
	public void stopTicks(IContract contract, ITickListener listener) throws ConnectException {
		ibAdapter.stopTicks(contract, listener);
	}

	@Override
	public String sendOrder(IOrder order) throws ConnectException, RequestFailedException {
		return ibAdapter.sendOrder(order);
	}

	@Override
	public String[] sendBracketOrders(IOrder parent, IOrder[] children) throws ConnectException, RequestFailedException {
		return ibAdapter.sendBracketOrders(parent, children);
	}

	@Override
	public void addOrderStatusListener(IOrderStatusListener listener) throws ConnectException {
		ibAdapter.addOrderStatusListener(listener);
	}

	@Override
	public void removeOrderStatusListener(IOrderStatusListener listener) throws ConnectException {
		ibAdapter.removeOrderStatusListener(listener);
	}

	@Override
	public void addPositionListener(IPositionListener listener) throws ConnectException {
		ibAdapter.addPositionListener(listener);
	}

	@Override
	public void removePositionListener(IPositionListener listener) throws ConnectException {
		ibAdapter.removePositionListener(listener);
	}
}
