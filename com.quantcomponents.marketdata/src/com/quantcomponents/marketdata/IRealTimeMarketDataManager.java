package com.quantcomponents.marketdata;

import java.net.ConnectException;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.ITaskMonitor;

public interface IRealTimeMarketDataManager extends IMarketDataManager {

	/**
	 * Starts the realtime update of prices from the market on a stock database
	 * @param stockDb the stock database to be start updating
	 * @param fillHistoricalGap true if the gap between the last market data and the current beginning of realtime data must be filled with historical data, false otherwise
	 * @param taskMonitor a task monitor to control the task
	 */
	public void startRealtimeUpdate(IStockDatabase stockDb,
			boolean fillHistoricalGap, ITaskMonitor taskMonitor)
			throws ConnectException, RequestFailedException;

	/**
	 * Stops the realtime update of prices
	 * @param stockDb stock database to stop updating
	 */
	public void stopRealtimeUpdate(IStockDatabase stockDb)
			throws ConnectException, RequestFailedException;

	/**
	 * Returns true if the stock database is currently being updated with realtime market data, false otherwise
	 * @param stockDb the stock database to query
	 */
	public boolean isRealtimeUpdate(IStockDatabase stockDb)
			throws ConnectException, RequestFailedException;

}