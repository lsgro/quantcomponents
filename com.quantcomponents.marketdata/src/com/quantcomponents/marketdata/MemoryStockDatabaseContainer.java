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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.beans.ImmutableContractBean;

/**
 * Simple non-persistent implementation class for a {@link IStockDatabaseContainer}
 * It can be used as default service when no better containerr are available
 */
public class MemoryStockDatabaseContainer implements IStockDatabaseContainer {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(MemoryStockDatabaseContainer.class.getName());
	private static Set<IStockDatabase> EMPTY_SET = Collections.emptySet();
	private final Map<IContract, Set<IStockDatabase>> stockDbMultiMap = new HashMap<IContract, Set<IStockDatabase>>();
	private final Map<String, IStockDatabase> stockDbByID = new HashMap<String, IStockDatabase>();

	public void deactivate() {
		stockDbMultiMap.clear();
	}
	
	@Override
	public synchronized void addStockDatabase(IStockDatabase stockDatabase) {
		IContract contract = stockDatabase.getContract();
		Set<IStockDatabase> set = stockDbMultiMap.get(contract);
		if (set == null) {
			set = new CopyOnWriteArraySet<IStockDatabase>();
			IContract key = new ImmutableContractBean(contract); // since IContract instances can be mutable, make a snapshot of it
			stockDbMultiMap.put(key, set);
		}
		set.add(stockDatabase);
		stockDbByID.put(stockDatabase.getPersistentID(), stockDatabase);
	}

	@Override
	public synchronized Collection<IStockDatabase> allStockDatabases() {
		Collection<IStockDatabase> allDbs = new LinkedList<IStockDatabase>();
		for (Set<IStockDatabase> set : stockDbMultiMap.values()) {
			allDbs.addAll(set);
		}
		return allDbs;
	}

	@Override
	public synchronized Collection<IStockDatabase> findStockDatabases(IContract contract) {
		Set<IStockDatabase> set = stockDbMultiMap.get(contract);
		if (set != null) {
			return set;
		} else {
			return EMPTY_SET;
		}
	}

	@Override
	public synchronized boolean removeStockDatabase(IStockDatabase stockDatabase) {
		Set<IStockDatabase> set = stockDbMultiMap.get(stockDatabase.getContract());
		if (set != null) {
			stockDbByID.remove(stockDatabase.getPersistentID());
			return set.remove(stockDatabase);
		} else {
			return false;
		}
	}

	@Override
	public int size() {
		int size = 0;
		for (Set<IStockDatabase> set : stockDbMultiMap.values()) {
			size += set.size();
		}
		return size;
	}

	@Override
	public IStockDatabase getStockDatabase(String ID) {
		return stockDbByID.get(ID);
	}
}
