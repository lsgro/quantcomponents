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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.quantcomponents.core.model.DataType;
import com.quantcomponents.marketdata.ITickPoint;
import com.quantcomponents.marketdata.TickPoint;
import com.quantcomponents.series.jdbc.ITickPointDao;

public class TickPointDao implements ITickPointDao {
	public static final String TABLE_NAME = "TICK";
	public static final String SELECT_FIELDS = "DATE_TIME, DATA_TYPE, PRICE, SIZE";
	public static final String INSERT_FIELDS = "SDB_ID, " + SELECT_FIELDS;
	private final Connection connection;
	
	public TickPointDao(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void initDb() throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM SYS.SYSTABLES WHERE TABLENAME = '" + TABLE_NAME + "'");
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int numberOfTables = rs.getInt(1);
		if (numberOfTables == 0) {
			stmt = connection.prepareStatement("CREATE TABLE " + TABLE_NAME +
					" (ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," + 
					"SDB_ID VARCHAR(200)," +
					"DATE_TIME TIMESTAMP," +
					"DATA_TYPE VARCHAR(50)," +
					"PRICE DECIMAL(30,10)," +
					"SIZE INT)");
			stmt.execute();
			connection.commit();
		}
		rs.close();
	}
	
	@Override
	public void save(String stockDatabaseId, ITickPoint item) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME +
				" (" + INSERT_FIELDS + ") VALUES (?, ?, ?, ?, ?)");
		stmt.setString(1, stockDatabaseId);
		stmt.setTimestamp(2, new Timestamp(item.getIndex().getTime()));
		stmt.setString(3, item.getDataType().name());
		stmt.setDouble(4, item.getValue());
		stmt.setInt(5, item.getSize());
		stmt.execute();
	}

	@Override
	public List<ITickPoint> find(String stockDatabaseId) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("SELECT " + SELECT_FIELDS + " FROM " + TABLE_NAME + " WHERE SDB_ID = ? ORDER BY DATE_TIME");
		stmt.setString(1, stockDatabaseId);
		List<ITickPoint> result = new LinkedList<ITickPoint>();
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Date date = new Date(rs.getTimestamp(1).getTime());
			DataType dataType = DataType.valueOf(rs.getString(2));
			Double price = rs.getDouble(3);
			Integer size = rs.getInt(4);
			TickPoint point = new TickPoint(date, dataType, price, size);
			result.add(point);
		}
		rs.close();
		return result;
	}

	@Override
	public void deleteAll(String stockDatabaseId) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE SDB_ID = ?");
		stmt.setString(1, stockDatabaseId);
		stmt.execute();
	}
	
	@Override
	public void flush() throws SQLException {
		connection.commit();
	}
}
