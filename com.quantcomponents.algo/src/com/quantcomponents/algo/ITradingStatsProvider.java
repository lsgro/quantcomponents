/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo;

import com.quantcomponents.core.series.SimplePoint;

/**
 * Provider of trading statistics
 */
public interface ITradingStatsProvider {
	/**
	 * Returns the highest equity reached during the trading agent execution up to this moment
	 * @return a value point, or null if no data has yet been produced
	 */
	SimplePoint getHighestEquityPoint();
	/**
	 * Returns the lowest equity reached during the trading agent execution up to this moment
	 * @return a value point, or null if no data has yet been produced
	 */
	SimplePoint getLowestEquityPoint();
	/**
	 * Returns the worst trade in terms of P&L during the trading agent execution up to this moment
	 * @return a trade statistics point, or null if no data has yet been produced
	 */
	ITradeStatsPoint getWorstTrade();
	/**
	 * Returns the best trade in terms of P&L during the trading agent execution up to this moment
	 * @return a trade statistics point, or null if no data has yet been produced
	 */
	ITradeStatsPoint getBestTrade();
	/**
	 * Returns time and value of equity at the start of the maximum draw-down in equity up to this moment
	 * @return a value point, or null if no data has yet been produced
	 */
	SimplePoint getStartOfMaxDrawdown();
	/**
	 * Returns time and value at of equity the end of the maximum draw-down in equity up to this moment
	 * @return a value point, or null if no data has yet been produced
	 */
	SimplePoint getEndOfMaxDrawdown();
	/**
	 * Returns time and value at of equity the start of the maximum run-up in equity up to this moment
	 * @return a value point, or null if no data has yet been produced
	 */
	SimplePoint getStartOfMaxRunup();
	/**
	 * Returns time and value at of equity the end of the maximum run-up in equity up to this moment
	 * @return a value point, or null if no data has yet been produced
	 */
	SimplePoint getEndOfMaxRunup();
}