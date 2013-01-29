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

import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.OrderSide;
import com.quantcomponents.core.model.OrderType;

/**
 * 
 * Trading order
 */
public interface IOrder {
	/**
	 * The ID should be null if the order has not been sent to the execution service
	 * @return the order ID or null
	 */
	String getId();
	/**
	 * @return the contract to be traded with this order
	 */
	IContract getContract();
	/**
	 * @return the order side
	 */
	OrderSide getSide();
	/**
	 * @return order type
	 */
	OrderType getType();
	/**
	 * @return order total amount. Some orders can be partially filled, therefore this should not be used to get the amount actually executed
	 */
	int getAmount();
	/**
	 * @return a price, if the order has a limit price, null otherwise
	 */
	Double getLimitPrice();
	/**
	 * @return a price, if the order type has an auxiliary price, e.g. a stop price, null otherwise
	 */
	Double getAuxPrice();
}
