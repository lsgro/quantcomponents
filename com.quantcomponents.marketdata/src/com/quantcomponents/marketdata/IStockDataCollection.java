/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata;

import com.quantcomponents.core.model.IContract;

/**
 * Generic interface for all the collection types related to a specific contract 
 * Its use in the framework is to narrow down the type of a series to a contract price series
 */
public interface IStockDataCollection {
	/**
	 * Return the contract the collection data is about
	 */
	IContract getContract();
}
