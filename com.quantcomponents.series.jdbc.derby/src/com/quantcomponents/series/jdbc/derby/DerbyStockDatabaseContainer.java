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
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.osgi.service.jdbc.DataSourceFactory;

import com.quantcomponents.series.jdbc.JdbcStockDatabaseContainer;

public class DerbyStockDatabaseContainer extends JdbcStockDatabaseContainer {
	public static final String DATABASE_NAME_KEY = "com.quantcomponents.series.db.name";
	public static final String DATABASE_NAME_DEFAULT = "stockDatabase";
	public static final String DATABASE_CREATE_KEY = "com.quantcomponents.series.db.create";
	public static final String DATABASE_CREATE_DEFAULT = "true";
	
	private volatile DataSourceFactory dataSourceFactory;
	private volatile Connection connection;
	
	public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
		this.dataSourceFactory = dataSourceFactory;
	}

	public void activate(Map<?,?> properties) throws SQLException {
		String dbName = (String) properties.get(DATABASE_NAME_KEY);
		if (dbName == null) {
			dbName = DATABASE_NAME_DEFAULT;
		}
		String dbCreate = (String) properties.get(DATABASE_CREATE_KEY);
		if (dbCreate == null) {
			dbCreate = DATABASE_CREATE_DEFAULT;
		}
		String dbString = dbName + (dbCreate.equals("true") ? ";create=true" : "");
		Properties props = new Properties();
		props.put(DataSourceFactory.JDBC_DATABASE_NAME, dbString);
		
		DataSource dataSource = dataSourceFactory.createDataSource(props);
		connection = dataSource.getConnection();
		
		setStockDbHeaderDao(new StockDatabaseHeaderDao(connection));
		setOhlcPointDao(new OHLCPointDao(connection));
		setTickPointDao(new TickPointDao(connection));
		super.activate();
	}
}
