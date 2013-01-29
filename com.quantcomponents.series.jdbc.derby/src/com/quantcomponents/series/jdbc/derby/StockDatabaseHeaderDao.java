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
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.BareDate;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContractDesc;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.IdentifierType;
import com.quantcomponents.core.model.OptionRight;
import com.quantcomponents.core.model.SecurityType;
import com.quantcomponents.core.model.beans.ContractDescBean;
import com.quantcomponents.core.model.beans.ContractBean;
import com.quantcomponents.series.jdbc.IStockDatabaseHeaderDao;
import com.quantcomponents.series.jdbc.StockDatabaseHeader;

public class StockDatabaseHeaderDao implements IStockDatabaseHeaderDao {
	public static final String TABLE_NAME = "STOCK_DATABASE";
	public static final String FIELDS = "ID, TIME_STAMP, TIMEZONE, DATA_TYPE, BAR_SIZE, AFTER_HOURS," +
			" C_SYMBOL, C_SEC_TYPE, C_EXP_DATE, C_STRIKE, C_OPTION_RIGHT, C_MULTIPLIER, C_EXCHANGE," +
			" C_PRIM_EXCH, C_CURRENCY, C_ID_TYPE, C_ID, C_BROKER_ID, C_DESC_LONG_NAME, C_DESC_TIMEZONE";
	private final Connection connection;
	
	public StockDatabaseHeaderDao(Connection connection) {
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
					" (ID VARCHAR(200) PRIMARY KEY," + 
					"TIME_STAMP TIMESTAMP," +
					"TIMEZONE VARCHAR(100)," +
					"DATA_TYPE VARCHAR(50)," +
					"BAR_SIZE VARCHAR(20)," +
					"AFTER_HOURS SMALLINT," +
					"C_SYMBOL VARCHAR(10)," +
					"C_SEC_TYPE VARCHAR(10)," +
					"C_EXP_DATE CHAR(8)," +
					"C_STRIKE DECIMAL(30,10)," +
					"C_OPTION_RIGHT CHAR(4)," +
					"C_MULTIPLIER INTEGER," +
					"C_EXCHANGE VARCHAR(100)," +
					"C_PRIM_EXCH VARCHAR(100)," +
					"C_CURRENCY CHAR(3)," +
					"C_ID_TYPE VARCHAR(10)," +
					"C_ID VARCHAR(100)," +
					"C_BROKER_ID VARCHAR(100)," +
					"C_DESC_LONG_NAME VARCHAR(100)," +
					"C_DESC_TIMEZONE VARCHAR(100))");
			stmt.execute();
			connection.commit();
		}
		rs.close();
	}

	@Override
	public void save(StockDatabaseHeader item) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME +
				" (" + FIELDS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, item.id);
		if (item.timestamp != null) {
			stmt.setTimestamp(2, new Timestamp(item.timestamp));
		} else  {
			stmt.setNull(2, Types.TIMESTAMP);
		}
		if (item.timeZone != null) {
			stmt.setString(3, item.timeZone.getID());
		} else {
			stmt.setNull(3, Types.VARCHAR);
		}
		stmt.setString(4, item.dataType.name());
		stmt.setString(5, item.barSize.name());
		stmt.setBoolean(6, item.includeAfterHours);
		stmt.setString(7, item.contract.getSymbol());
		stmt.setString(8, item.contract.getSecurityType().name());
		if (item.contract.getExpiryDate() != null) {
			stmt.setString(9, item.contract.getExpiryDate().getDateRepr());
		} else {
			stmt.setNull(9, Types.VARCHAR);
		}
		if (item.contract.getStrike() != null) {
			stmt.setDouble(10, item.contract.getStrike()); 
		} else {
			stmt.setNull(10, Types.DECIMAL);
		}
		if (item.contract.getOptionRight() != null) {
			stmt.setString(11, item.contract.getOptionRight().name());
		} else {
			stmt.setNull(11, Types.VARCHAR);
		}
		if (item.contract.getMultiplier() != null) {
			stmt.setInt(12, item.contract.getMultiplier());
		} else {
			stmt.setNull(12, Types.INTEGER);
		}
		stmt.setString(13, item.contract.getExchange());
		stmt.setString(14, item.contract.getPrimaryExchange());
		stmt.setString(15, item.contract.getCurrency().getCurrencyCode());
		if (item.contract.getIdentifierType() != null) {
			stmt.setString(16, item.contract.getIdentifierType().name());
		} else {
			stmt.setNull(16, Types.VARCHAR);
		}
		stmt.setString(17, item.contract.getIdentifier());
		stmt.setString(18, item.contract.getBrokerID());
		IContractDesc desc = item.contract.getContractDescription();
		if (desc != null) {
			stmt.setString(19, desc.getLongName());
			stmt.setString(20, desc.getTimeZone().getID());
		} else {
			stmt.setNull(19, Types.VARCHAR);
			stmt.setNull(20, Types.VARCHAR);
		}
		stmt.execute();
	}

	@Override
	public void delete(String id) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE ID = ?");
		stmt.setString(1, id);
		stmt.execute();
	}

	@Override
	public StockDatabaseHeader get(String id) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("SELECT " + FIELDS + " FROM " + TABLE_NAME + " WHERE ID = ?");
		stmt.setString(1, id);
		ResultSet rs = stmt.executeQuery();
		StockDatabaseHeader result = null;
		if (rs.next()) {
			result = buildHeaderFromResultSet(rs);
		} 
		rs.close();
		return result;
	}

	@Override
	public int countAll() throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM " + TABLE_NAME);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int number = rs.getInt(1);
		rs.close();
		return number;
	}

	@Override
	public Set<StockDatabaseHeader> findAll() throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("SELECT " + FIELDS + " FROM " + TABLE_NAME);
		return find(stmt);
	}

	@Override
	public Set<StockDatabaseHeader> findByContract(IContract contract) throws SQLException {
		StringBuilder buffer = new StringBuilder();
		buffer.append("SELECT " + FIELDS + 
				" FROM " + TABLE_NAME + 
				" WHERE C_SYMBOL = ? AND C_SEC_TYPE = ? AND C_CURRENCY = ?");
		if (contract.getExchange() != null) {
			buffer.append(" AND C_EXCHANGE = ?");
		}
		if (contract.getPrimaryExchange() != null) {
			buffer.append(" AND C_PRIM_EXCH = ?");
		}
		if (contract.getExpiryDate() != null) {
			buffer.append(" AND C_EXP_DATE = ?");
		}
		if (contract.getOptionRight() != null) {
			buffer.append(" AND C_OPTION_RIGHT = ?");
		}
		if (contract.getStrike() != null) {
			buffer.append(" AND C_STRIKE = ?");
		}
		PreparedStatement stmt = connection.prepareStatement(buffer.toString());
		stmt.setString(1, contract.getSymbol());
		stmt.setString(2, contract.getSecurityType().name());
		stmt.setString(3, contract.getCurrency().getCurrencyCode());
		int fieldNo = 4;
		if (contract.getExchange() != null) {
			stmt.setString(fieldNo++, contract.getExchange());
		}
		if (contract.getPrimaryExchange() != null) {
			stmt.setString(fieldNo++, contract.getPrimaryExchange());
		}
		if (contract.getExpiryDate() != null) {
			stmt.setString(fieldNo++, contract.getExpiryDate().getDateRepr());
		}
		if (contract.getOptionRight() != null) {
			stmt.setString(fieldNo++, contract.getOptionRight().name());
		}
		if (contract.getStrike() != null) {
			stmt.setDouble(fieldNo, contract.getStrike());
		}
		return find(stmt);
	}
	
	@Override
	public void flush() throws SQLException {
		connection.commit();
	}

	private Set<StockDatabaseHeader> find(PreparedStatement stmt) throws SQLException {
		Set<StockDatabaseHeader> result = new HashSet<StockDatabaseHeader>();
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			result.add(buildHeaderFromResultSet(rs));
		}
		rs.close();
		return result;
	}
	
	private StockDatabaseHeader buildHeaderFromResultSet(ResultSet rs) throws SQLException {
		StockDatabaseHeader header = new StockDatabaseHeader();
		header.id = rs.getString(1);
		Timestamp timestamp = rs.getTimestamp(2);
		if (timestamp != null) {
			header.timestamp = timestamp.getTime();
		}
		String tzId = rs.getString(3);
		if (tzId != null) {
			header.timeZone = TimeZone.getTimeZone(tzId);
		} else {
			header.timeZone = TimeZone.getDefault();
		}
		header.dataType = DataType.valueOf(rs.getString(4));
		header.barSize = BarSize.valueOf(rs.getString(5));
		header.includeAfterHours = rs.getBoolean(6);
		
		ContractBean bean = new ContractBean();
		bean.setSymbol(rs.getString(7));
		bean.setSecurityType(SecurityType.valueOf(rs.getString(8)));
		String expiryDateRepr = rs.getString(9);
		if (expiryDateRepr != null) {
			bean.setExpiryDate(new BareDate(expiryDateRepr));
		}
		bean.setStrike(rs.getDouble(10));
		String optRight = rs.getString(11);
		if (optRight != null) {
			bean.setOptionRight(OptionRight.valueOf(optRight));
		}
		bean.setMultiplier(rs.getInt(12));
		bean.setExchange(rs.getString(13));
		bean.setPrimaryExchange(rs.getString(14));
		String curCode = rs.getString(15);
		if (curCode != null) {
			bean.setCurrency(Currency.getInstance(curCode));
		}
		String idType = rs.getString(16);
		if (idType != null) {
			bean.setIdentifierType(IdentifierType.valueOf(idType));
		}
		bean.setIdentifier(rs.getString(17));
		bean.setBrokerID(rs.getString(18));
		String descLongName = rs.getString(19);
		String descTzId = rs.getString(20);
		if (descLongName != null || descTzId != null) {
			ContractDescBean desc = new ContractDescBean();
			desc.setLongName(descLongName);
			if (descTzId != null) {
				desc.setTimeZone(TimeZone.getTimeZone(descTzId));
			}
			bean.setContractDescription(desc);
		}
		header.contract = bean;
			
		return header;
	}
}
