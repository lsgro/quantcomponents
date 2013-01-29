/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.ta;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.quantcomponents.algo.ITradingAgentFactory;
import com.quantcomponents.core.calendar.FlatCalendar;
import com.quantcomponents.core.calendar.ITradingCalendar;
import com.quantcomponents.core.calendar.ITradingCalendarManager;

/**
 * Factory for the example algorithm {@link AverageCrossingTradingAgent}.
 * It performs configuration validation and creates executable instances of the algorithm
 *
 */
public class AverageCrossingTradingAgentFactory implements ITradingAgentFactory {
	
	private static class Configuration {
		ITradingCalendar tradingCalendar;
		Integer shortAveragePeriods;
		Integer longAveragePeriods;
		Integer positionSize;
		Boolean ignoreLastPeriod;
	}
	
	static final String[] INPUT_SERIES_NAMES = new String[] { "stock" };
	
	private static final int DEFAULT_SHORT_AVERAGE_PERIODS = 5;
	private static final int DEFAULT_LONG_AVERAGE_PERIODS = 20;
	private static final int DEFAULT_POSITION_SIZE = 1;
	private volatile ITradingCalendarManager tradingCalendarManager;
	
	public void setTradingCalendarManager(ITradingCalendarManager tradingCalendarManager) {
		this.tradingCalendarManager = tradingCalendarManager;
	}

	@Override
	public AverageCrossingTradingAgent createProcessor(Properties properties) {
		Map<String, String> messages = new HashMap<String, String>();
		Configuration config = parseConfiguration(properties, messages);
		return new AverageCrossingTradingAgent(config.tradingCalendar, config.shortAveragePeriods, config.longAveragePeriods, config.positionSize, config.ignoreLastPeriod);
	}

	@Override
	public String[] getConfigurationKeys() {
		return new String[] { AverageCrossingTradingAgent.TRADING_CALENDAR_NAME, 
				AverageCrossingTradingAgent.SHORT_AVERAGE_PERIODS, 
				AverageCrossingTradingAgent.LONG_AVERAGE_PERIODS, 
				AverageCrossingTradingAgent.POSITION_SIZE,
				AverageCrossingTradingAgent.IGNORE_LAST_PERIOD };
	}

	@Override
	public String[] getInputSeriesNames() {
		return INPUT_SERIES_NAMES;
	}

	@Override
	public boolean isConfigurationValid(Properties properties, Map<String, String> messages) {
		parseConfiguration(properties, messages);
		if (messages.size() > 0) {
			return false;
		} else {
			return true;
		}
	}

	private Configuration parseConfiguration(Properties properties, Map<String, String> messages) {
		Configuration config = new Configuration();
		String tradingCalendarName = properties.getProperty(AverageCrossingTradingAgent.TRADING_CALENDAR_NAME);
		if (tradingCalendarName != null && !tradingCalendarName.trim().equals("")) {
			config.tradingCalendar = tradingCalendarManager.tradingCalendarByName(tradingCalendarName);
			if (config.tradingCalendar == null) {
				messages.put(AverageCrossingTradingAgent.TRADING_CALENDAR_NAME, "Trading calendar not found: " + tradingCalendarName);
			}
		} else {
			config.tradingCalendar = new FlatCalendar();
		}
		config.shortAveragePeriods = DEFAULT_SHORT_AVERAGE_PERIODS;
		String tmp = properties.getProperty(AverageCrossingTradingAgent.SHORT_AVERAGE_PERIODS);
		if (tmp != null) {
			try {
				config.shortAveragePeriods = Integer.parseInt(tmp);
			} catch (Exception e) {
				messages.put(AverageCrossingTradingAgent.SHORT_AVERAGE_PERIODS, "Short average periods not parseable: " + tmp);
			}
		}
		config.longAveragePeriods = DEFAULT_LONG_AVERAGE_PERIODS;
		tmp = properties.getProperty(AverageCrossingTradingAgent.LONG_AVERAGE_PERIODS);
		if (tmp != null) {
			try {
				config.longAveragePeriods = Integer.parseInt(tmp);
			} catch (Exception e) {
				messages.put(AverageCrossingTradingAgent.LONG_AVERAGE_PERIODS, "Long average periods not parseable: " + tmp);
			}
		}
		config.positionSize = DEFAULT_POSITION_SIZE;
		tmp = properties.getProperty(AverageCrossingTradingAgent.POSITION_SIZE);
		if (tmp != null) {
			try {
				config.positionSize = Integer.parseInt(tmp);
			} catch (Exception e) {
				messages.put(AverageCrossingTradingAgent.POSITION_SIZE, "Position size not parseable: " + tmp);
			}
		}
		config.ignoreLastPeriod = false;
		tmp = properties.getProperty(AverageCrossingTradingAgent.IGNORE_LAST_PERIOD);
		if (tmp != null) {
			config.ignoreLastPeriod = Boolean.valueOf(tmp);
		}
		return config;
	}
}
