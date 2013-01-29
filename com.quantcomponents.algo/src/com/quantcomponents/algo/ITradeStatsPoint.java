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

import com.quantcomponents.core.model.ISeriesPoint;

/**
 * 
 * Time series point specific to convey statistics about a trade
 */
public interface ITradeStatsPoint extends ISeriesPoint<Date, Double> {
	/**
	 * The maximum favorable excursion in the P&L during this trade
	 * It can be positive or negative
	 */
	double getMaxFavorableExcursion();
	/**
	 * The maximum adverse excursion in the P&L during this trade
	 * It can be positive or negative
	 */
	double getMaxAdverseExcursion();
	/**
	 * The final P&L for this trade
	 */
	double getTradePnl();
	/**
	 * The trade itself
	 */
	ITrade getTrade();
	/**
	 * Timestamp of trade
	 */
	Date getTradeStart();
	/**
	 * Timestamp of following trade
	 */
	Date getTradeEnd();
}