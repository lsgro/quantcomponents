/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.osgi.internal;

import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.TimeZone;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.BareDate;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.core.model.OptionRight;
import com.quantcomponents.core.model.SecurityType;
import com.quantcomponents.core.model.beans.ContractBean;

public class ShellInterpreter {
	public static final String SCOPE = "quant";
	private static final String[][] COMMMAND_USAGE = {
		{"date","returns java.util.Date","int\tyear","int\tmonth","int\tdate","int\thour","int\tmin","int\tseconds"},
		{"baredate","returns a BareDate","int\tyear","int\tmonth","int\tdate"},
		{"baredate","returns a BareDate","String date [example: baredate 20131231]"},
		{"timezone","returns a TimeZone","String ID [example: timezone \"Europe/Rome\"]"},
		{"mytimezone","returns default TimeZone"},
		{"barsize","returns a BarSize","String barsize [example: barsize FIFTEEN_MINS]"},
		{"datatype","returns a DataType","String datatype [example: datatype TRADES]"},
		{"optionright","returns a OptionRight","String ( CALL | PUT )"},
		{"contract","returns a IContract","String symbol","String sectype","String currency"},
		{"taskmonitor","returns a dummy ITaskMonitor"},
		{"serieslistener","returns a test ISeriesListener, which dumps data on console"},
		{"marketdataprovider","returns a low-level IMarketDataProvider, if available"},
		{"marketdatamanager","returns a IMarketDataManager, if available"},
		{"executionservice","returns a low-level IExecutionService, if available"},
		{"tradingmanager","returns a IStockDatabaseTradingManager, if available"}
	};
	
	private String[] findCommandHelp(String command) {
		for (String[] hlp : COMMMAND_USAGE) {
			if (hlp[0].equals(command)) {
				return hlp;
			}
		}
		return null;
	}
	
	private static final String buildHelp(String[] hlp) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(" ").append(hlp[0]).append("\n");
		buffer.append("  scope: ").append(SCOPE).append("\n");
		buffer.append(" ").append(hlp[1]).append("\n");
		buffer.append("  parameters:\n");
		for (int i = 2; i < hlp.length; i++) {
			buffer.append("    ").append(hlp[i]).append("\n");
		}
		return buffer.toString();
	}
	
	public String help() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("use: help <command> to get specific help\n");
		for (String[] cmd : COMMMAND_USAGE) {
			buffer.append(cmd[0]).append("\n");
		}
		return buffer.toString();
	}
	
	public String help(String command) {
		String[] hlp = findCommandHelp(command);
		if (hlp == null) {
			return "command not found: " + command;
		}
		return buildHelp(hlp);
	}
	
	public TimeZone mytimezone() {
		return TimeZone.getDefault();
	}
	public TimeZone timezone(String code) {
		return TimeZone.getTimeZone(code);
	}
	public BareDate baredate(String arg) {
		return new BareDate(arg);
	}
	public BareDate baredate(int year, int month, int day) {
		return new BareDate(year, month, day);
	}
	public Date date(int year, int month, int day, int hour, int minute, int second) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(year, month - 1, day, hour, minute, second);
		return cal.getTime();
	}
	public BarSize barsize(String name) {
		return BarSize.valueOf(name);
	}
	public DataType datatype(String name) {
		return DataType.valueOf(name);
	}
	public SecurityType sectype(String name) {
		return SecurityType.valueOf(name);
	}
	public OptionRight optionright(String name) {
		return OptionRight.valueOf(name);
	}
	public ContractBean contract(String symbol, String secTypeCode, String curCode) {
		ContractBean c = new ContractBean();
		c.setSymbol(symbol);
		c.setCurrency(Currency.getInstance(curCode));
		c.setSecurityType(SecurityType.valueOf(secTypeCode));
		return c;
	}
	public ITaskMonitor taskmonitor() {
		return new DummyTaskMonitor();
	}
	public ISeriesListener<Date, Double> serieslistener() {
		return new DummyTimeSeriesListener();
	}
}
