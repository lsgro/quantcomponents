/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.service;

import com.quantcomponents.algo.ICommissionCalculator;
import com.quantcomponents.algo.IOrder;



public class LinearCommissionCalculator implements ICommissionCalculator {
	private final double fixedFactor;
	private final double linearSizeFactor;
	private final double linearValueFactor;

	public LinearCommissionCalculator(double fixedFactor, double linearSizeFactor, double linearValueFactor) {
		this.fixedFactor = fixedFactor;
		this.linearSizeFactor = linearSizeFactor;
		this.linearValueFactor = linearValueFactor;
	}

	@Override
	public double calculateCommission(IOrder order, int amount, double executionPrice) {
		return Math.abs(executionPrice * amount) * linearValueFactor + Math.abs(amount) * linearSizeFactor + fixedFactor;
	}

}
