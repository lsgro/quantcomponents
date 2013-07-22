package com.quantcomponents.marketdata;

import java.net.ConnectException;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;

public interface IRealTimeMarketDataProvider extends IMarketDataProvider {

	/**
	 * Starts the collection of realtime price bars
	 * @param contract the contract 
	 * @param barSize the bar size of the data requested
	 * @param dataType the data type requested 
	 * @param includeAfterHours true if after hours trading data is requested, false otherwise
	 * @param listener the price bar listener to be notified for each bar
	 */
	public void startRealTimeBars(IContract contract, BarSize barSize,
			DataType dataType, boolean includeAfterHours,
			IRealTimeDataListener listener) throws ConnectException,
			RequestFailedException;

	/**
	 * Stops the collection of realtime price bars
	 * @param contract the contract 
	 * @param barSize the bar size of the data requested
	 * @param dataType the data type requested 
	 * @param includeAfterHours true if after hours trading data is requested, false otherwise
	 * @param listener the listener that must stop being notified for each bar
	 */
	public void stopRealTimeBars(IContract contract, BarSize barSize,
			DataType dataType, boolean includeAfterHours,
			IRealTimeDataListener listener) throws ConnectException;

	/**
	 * Start the collection of realtime price ticks
	 * @param contract the contract 
	 * @param listener the price tick listener to be notified for each tick
	 */
	public void startTicks(IContract contract, ITickListener listener)
			throws ConnectException, RequestFailedException;

	/**
	 * Stop the collection of realtime price ticks
	 * @param contract the contract
	 * @param listener the listener that must stop being notified for each tick
	 */
	public void stopTicks(IContract contract, ITickListener listener)
			throws ConnectException;

}