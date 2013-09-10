/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.series.jdbc.derby;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.osgi.service.jdbc.DataSourceFactory;

import com.quantcomponents.marketdata.IStockDatabaseContainer;
import com.quantcomponents.marketdata.IStockDatabaseContainerFactory;
import com.quantcomponents.series.jdbc.JdbcStockDatabaseContainer;

public class DerbyStockDatabaseContainerFactory implements IStockDatabaseContainerFactory {
	public static final String DATABASE_NAME_PREFIX_KEY = "com.quantcomponents.series.db.prefix";
	public static final String DATABASE_NAME_PREFIX_DEFAULT = "stockDatabase";
	public static final String DATABASE_CREATE_KEY = "com.quantcomponents.series.db.create";
	public static final String DATABASE_CREATE_DEFAULT = "true";
	
	private final Collection<JdbcStockDatabaseContainer> activeContainers = Collections.synchronizedCollection(new LinkedList<JdbcStockDatabaseContainer>());

	private volatile String dbNamePrefix;
	private volatile String dbCreate;
	private volatile DataSourceFactory dataSourceFactory;
	private volatile Connection connection;
	
	public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
		this.dataSourceFactory = dataSourceFactory;
	}

	public void activate(Map<?,?> properties) throws SQLException {
		dbNamePrefix = (String) properties.get(DATABASE_NAME_PREFIX_KEY);
		if (dbNamePrefix == null) {
			dbNamePrefix = DATABASE_NAME_PREFIX_DEFAULT;
		}
		dbCreate = (String) properties.get(DATABASE_CREATE_KEY);
		if (dbCreate == null) {
			dbCreate = DATABASE_CREATE_DEFAULT;
		}
	}

	public void deactivate() throws SQLException {
		for (JdbcStockDatabaseContainer stockDbContainer : activeContainers) {
			stockDbContainer.stop();
		}
		activeContainers.clear();
	}
	
	@Override
	public IStockDatabaseContainer getInstance(String id) throws SQLException {
		String dbName = dbNamePrefix + "-" + id;
		String dbString = dbName + (dbCreate.equals("true") ? ";create=true" : "");
		Properties props = new Properties();
		props.put(DataSourceFactory.JDBC_DATABASE_NAME, dbString);
		
		DataSource dataSource = dataSourceFactory.createDataSource(props);
		connection = dataSource.getConnection();
		
		StockDatabaseHeaderDao stockDbHeaderDao = new StockDatabaseHeaderDao(connection);
		OHLCPointDao ohlcPointDao = new OHLCPointDao(connection);
		TickPointDao tickPointDao = new TickPointDao(connection);
		JdbcStockDatabaseContainer stockDbContainer = new JdbcStockDatabaseContainer(stockDbHeaderDao, ohlcPointDao, tickPointDao);
		stockDbContainer.start();
		activeContainers.add(stockDbContainer);
		return stockDbContainer;
	}
}
