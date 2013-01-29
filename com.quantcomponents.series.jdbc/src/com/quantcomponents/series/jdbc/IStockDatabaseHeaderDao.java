/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.series.jdbc;

import java.sql.SQLException;
import java.util.Set;

import com.quantcomponents.core.model.IContract;

public interface IStockDatabaseHeaderDao extends IFlushable, IDbInitializable {
	void save(StockDatabaseHeader item) throws SQLException;
	void delete(String id) throws SQLException;
	StockDatabaseHeader get(String id) throws SQLException;
	Set<StockDatabaseHeader> findAll() throws SQLException;
	Set<StockDatabaseHeader> findByContract(IContract contract) throws SQLException;
	int countAll() throws SQLException;
}
