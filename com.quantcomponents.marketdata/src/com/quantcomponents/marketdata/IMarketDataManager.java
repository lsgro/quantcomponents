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
import com.quantcomponents.core.model.ISeriesProvider;
import com.quantcomponents.core.model.ITaskMonitor;

/**
 * Main service interface to create and manage market data time-series
 * @see IStockDatabase
 */
public interface IMarketDataManager extends ISeriesProvider<Date, Double>, IPrettyNamed {
	/**
	 * Search contracts based on criteria
	 * @param criteria a partially filled contract bean, to be used as criteria for search
	 * @param taskMonitor a task monitor to control the task
	 * @return a list of contracts matching the criteria
	 */
	List<IContract> searchContracts(IContract criteria, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException;
	/**
	 * Returns all available stock databases
	 */
	Collection<IStockDatabase> allStockDatabases();
	/**
	 * Search available stock databases based on contract and other parameters.
	 * All parameters are optional
	 * @param contract the wanted contract, or null
	 * @param dataType the wanted data type, or null
	 * @param barSize the wanted bar size or null
	 * @param includeAfterHours true, false or null
	 * @return the first stock database instance that matches the criteria, or null
	 */
	IStockDatabase findStockDatabase(IContract contract, DataType dataType, BarSize barSize, Boolean includeAfterHours);
	/**
	 * Get a stock database by its persistent ID
	 * @param ID the persistent ID
	 * @return a stock database, or null if none could be found
	 */
	IStockDatabase getStockDatabase(String ID);
	/**
	 * Returns the number of total available stock databases
	 */
	int numberOfStockDatabases();
	/**
	 * Creates a stock database
	 * @param contract the contract for which the stock database is created
	 * @param dataType the required data type
	 * @param barSize the required bar size of the price data
	 * @param includeAfterHours true if it must include after hours trading time, false otherwise
	 * @param timeZone the time zone for this stock database, normally it should be the default time zone for the contract
	 * @return an empty stock databases 
	 */
	IStockDatabase createStockDatabase(IContract contract, DataType dataType, BarSize barSize, boolean includeAfterHours, TimeZone timeZone);
	/**
	 * Remove an historical database from the manager
	 * @param stockDb the stock database to be removed
	 */
	void removeStockDatabase(IStockDatabase stockDb) throws ConnectException, RequestFailedException;
	/**
	 * Fills the stock database with the missing historical data
	 * @param stockDb stock database to be filled
	 * @param startDate start date of the period to be filled
	 * @param endDate end date of the period to be filled
	 * @param taskMonitor task monitor to control the task
	 */
	void fillHistoricalData(IStockDatabase stockDb, Date startDate, Date endDate, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException;
}
