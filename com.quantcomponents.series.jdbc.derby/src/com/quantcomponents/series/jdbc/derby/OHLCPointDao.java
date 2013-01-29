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
import java.sql.Types;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.OHLCPoint;
import com.quantcomponents.series.jdbc.IOHLCPointDao;

public class OHLCPointDao implements IOHLCPointDao {
	public static final String TABLE_NAME = "OHLC";
	public static final String SELECT_FIELDS = "BAR_SIZE, DATE_TIME," +
			" P_OPEN, P_HIGH, P_LOW, P_CLOSE, VOLUME, P_WAP, TICK_COUNT, LAST_UPDATE";
	public static final String INSERT_FIELDS = "SDB_ID, " + SELECT_FIELDS;
	private final Connection connection;
	
	public OHLCPointDao(Connection connection) {
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
					"BAR_SIZE VARCHAR(20)," +
					"DATE_TIME TIMESTAMP," +
					"P_OPEN DECIMAL(30,10)," +
					"P_HIGH DECIMAL(30,10)," +
					"P_LOW DECIMAL(30,10)," +
					"P_CLOSE DECIMAL(30,10)," +
					"VOLUME BIGINT," +
					"P_WAP DECIMAL(30,10)," +
					"TICK_COUNT INT," +
					"LAST_UPDATE TIMESTAMP)");
			stmt.execute();
			connection.commit();
		}
		rs.close();
	}

	@Override
	public void save(String stockDatabaseId, IOHLCPoint item) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME +
				" (" + INSERT_FIELDS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		stmt.setString(1, stockDatabaseId);
		stmt.setString(2, item.getBarSize().name());
		stmt.setTimestamp(3, new Timestamp(item.getIndex().getTime()));
		stmt.setDouble(4, item.getOpen());
		stmt.setDouble(5, item.getHigh());
		stmt.setDouble(6, item.getLow());
		stmt.setDouble(7, item.getClose());
		stmt.setLong(8, item.getVolume());
		stmt.setDouble(9, item.getWAP());
		stmt.setInt(10, item.getCount());
		if (item.getLastUpdate() != null) {
			stmt.setTimestamp(11, new Timestamp(item.getLastUpdate().getTime()));
		} else {
			stmt.setNull(11, Types.TIMESTAMP);
		}
		stmt.execute();
	}

	@Override
	public void update(String stockDatabaseId, IOHLCPoint existingItem, IOHLCPoint newItem) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("UPDATE " + TABLE_NAME + " SET " +
				"P_OPEN = ?, P_HIGH = ?, P_LOW = ?, P_CLOSE = ?, VOLUME = ?, P_WAP = ?, TICK_COUNT = ?, LAST_UPDATE = ?" +
				" WHERE SDB_ID = ? AND DATE_TIME = ?");
		// updated fields
		stmt.setDouble(1, newItem.getOpen());
		stmt.setDouble(2, newItem.getHigh());
		stmt.setDouble(3, newItem.getLow());
		stmt.setDouble(4, newItem.getClose());
		stmt.setLong(5, newItem.getVolume());
		stmt.setDouble(6, newItem.getWAP());
		stmt.setInt(7, newItem.getCount());
		if (newItem.getLastUpdate() != null) {
			stmt.setTimestamp(8, new Timestamp(newItem.getLastUpdate().getTime()));
		} else {
			stmt.setNull(8, Types.TIMESTAMP);
		}
		// selection criteria
		stmt.setString(9, stockDatabaseId);
		stmt.setTimestamp(10, new Timestamp(newItem.getIndex().getTime()));
		int rows = stmt.executeUpdate();
		if (rows == 0) {
			throw new IllegalArgumentException("This item is not stored in the database: " + stockDatabaseId + ";" + newItem.getIndex());
		}
		if (rows > 1) {
			throw new IllegalArgumentException("Multiple items for these parameters: " + stockDatabaseId + ";" + newItem.getIndex());
		}
	}

	@Override
	public List<IOHLCPoint> find(String stockDatabaseId) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("SELECT " + SELECT_FIELDS + " FROM " + TABLE_NAME + " WHERE SDB_ID = ? ORDER BY DATE_TIME");
		stmt.setString(1, stockDatabaseId);
		List<IOHLCPoint> result = new LinkedList<IOHLCPoint>();
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			BarSize barSize = BarSize.valueOf(rs.getString(1));
			Date date = new Date(rs.getTimestamp(2).getTime());
			Double open = rs.getDouble(3);
			Double high = rs.getDouble(4);
			Double low = rs.getDouble(5);
			Double close = rs.getDouble(6);
			Long volume = rs.getLong(7);
			Double wap = rs.getDouble(8);
			Integer count = rs.getInt(9);
			OHLCPoint point = new OHLCPoint(barSize, date, open, high, low, close, volume, wap, count);
			Timestamp lastUpdateTs = rs.getTimestamp(10);
			if (lastUpdateTs != null) {
				point.setLastUpdate(new Date(lastUpdateTs.getTime()));
			}
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
