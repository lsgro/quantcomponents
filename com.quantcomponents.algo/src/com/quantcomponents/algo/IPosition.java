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

/**
 * 
 * Position information within an execution service for a specific {@link com.quantcomponents.core.model.IContract}
 */
public interface IPosition {
	/**
	 * The timestamp of this position.
	 * It can correspond to the instant when the position has last change, but this depends on the implementation of the position service
	 */
	Date getTimestamp();
	/**
	 * A signed amount, positive is the position is long, negative if the position is short.
	 * In case of {@link com.quantcomponents.core.model.SecurityType#CASH} positions, the amount correspond to the money amount, and it can be non integer
	 */
	double getSignedAmount();
	/**
	 * The market price of the contract at the time of creation of this position
	 */
	double getMarketPrice();
	/**
	 * The value of the total position at market price; positive for long positions, negative for short positions
	 */
	double getMarketValue();
	/**
	 * The average price of the position
	 * based on execution prices and amounts while building the position from zero;
	 * positive for long positions, negative for short positions
	 */
	double getAveragePrice();
	/**
	 * Market value minus the average price of the position times the current amount
	 */
	double getUnrealizedPnl();
	/**
	 * The realized profit or loss through the closing of previously open positions
	 */
	double getRealizedPnl();
}
