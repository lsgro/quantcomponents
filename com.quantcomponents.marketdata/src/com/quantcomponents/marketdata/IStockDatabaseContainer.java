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

import java.util.Collection;

import com.quantcomponents.core.model.IContract;

/**
 * Container for stock databases.
 * Used to decouple the {@link IMarketDataManager} from the storage of stock databases.
 * Implementations can use database persistence of other methods.
 * The key to stock databases is their persistent ID.
 * 
 * @see com.quantcomponents.core.model.IPersistentIdentifiable
 */
public interface IStockDatabaseContainer {
	/**
	 * Add a stock database to the storage
	 */
	void addStockDatabase(IStockDatabase stockDatabase);
	/**
	 * Retrieve all the stock databases from the storage
	 */
	Collection<IStockDatabase> allStockDatabases();
	/**
	 * Find all the stock databases for a specific contract
	 */
	Collection<IStockDatabase> findStockDatabases(IContract contract);
	/**
	 * Get a specific stock database by its persistent ID
	 * @return a stock database, or null if none is found
	 */
	IStockDatabase getStockDatabase(String ID);
	/**
	 * Remove a stock database from storage
	 * @return true if the database was found, and removed, false otherwise
	 */
	boolean removeStockDatabase(IStockDatabase stockDatabase);
	/**
	 * Return the number of stock databases found in the container
	 */
	int size();
}
