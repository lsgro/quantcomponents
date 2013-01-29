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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.algo.ICommissionCalculator;
import com.quantcomponents.algo.IOrder;

public class ConfigurableLinearCommissionCalculator implements ICommissionCalculator {
	private static final Logger logger = Logger.getLogger(ConfigurableLinearCommissionCalculator.class.getName());
	public static final String FIXED_FACTOR_KEY = "factor.fixed";
	public static final String LINEAR_SIZE_FACTOR_KEY = "factor.size";
	public static final String LINEAR_VALUE_FACTOR_KEY = "factor.value";
	private volatile ICommissionCalculator inner;
	
	public void deactivate() { }

	@Override
	public double calculateCommission(IOrder order, int amount, double executionPrice) {
		return inner.calculateCommission(order, amount, executionPrice);
	}

	void modify(Map<?,?> properties) {
		configure(properties);
	}
	
	void activate(Map<?,?> properties) {
		configure(properties);
	}
	
	private void configure(Map<?,?> properties) {
		Double fixedFactor = 0.0;
		Double linearSizeFactor = 0.0;
		Double linearValueFactor = 0.0;
		if (properties != null) {
			fixedFactor = Double.valueOf((String) properties.get(FIXED_FACTOR_KEY));
			linearSizeFactor = Double.valueOf((String) properties.get(LINEAR_SIZE_FACTOR_KEY));
			linearValueFactor = Double.valueOf((String) properties.get(LINEAR_VALUE_FACTOR_KEY));
		}
		logger.log(Level.INFO, "Updated linear commission calculator configuration: " + fixedFactor + ";" + linearSizeFactor + ";" + linearValueFactor);
		inner = new LinearCommissionCalculator(fixedFactor, linearSizeFactor, linearValueFactor);
	}
}
