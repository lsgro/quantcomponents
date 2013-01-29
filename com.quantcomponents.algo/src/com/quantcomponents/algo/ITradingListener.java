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

import java.util.Date;

import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Listener to trade executions and price updates
 * This interface is implemented by services willing to maintain positions during simulations.
 */
public interface ITradingListener {
	/**
	 * Called whenever a new trade is executed
	 * @param trade the trade been executed
	 */
	void onTrade(ITrade trade);
	/**
	 * Called whenever a market price for a relevant contract changes.
	 * Typical implementations require a list of contract to monitor for price changes.
	 * @param contract the contract for which the market price has changed
	 * @param price new market price
	 */
	void onPriceUpdate(IContract contract, ISeriesPoint<Date, Double> price);
}
