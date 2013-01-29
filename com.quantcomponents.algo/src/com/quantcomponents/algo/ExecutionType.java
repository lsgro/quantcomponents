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

/**
 * 
 * Type of algorithm execution
 *
 */
public enum ExecutionType {
	/**
	 * Backtesting of an algorithm on historical data-series
	 */
	BACKTEST, 
	/**
	 * Realtime algo-trading on paper account
	 * <b>NOTE: not available with some brokers (e.g. Interactive Brokers), where the type of account is chosen directly on the proprietary client</b>
	 */
	PAPER,
	/**
	 * Realtime algo-trading on live account
	 */
	LIVE
}
