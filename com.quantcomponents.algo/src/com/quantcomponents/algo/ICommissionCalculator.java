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
 * Commission calculator for simulated trading and backtesting
 */
public interface ICommissionCalculator {
	/**
	 * Calculate the commission due for a trade
	 * @param order Trade order
	 * @param amount Actual execution amount
	 * @param executionPrice Actual execution price
	 * @return a commission amount
	 */
	double calculateCommission(IOrder order, int amount, double executionPrice);
}
