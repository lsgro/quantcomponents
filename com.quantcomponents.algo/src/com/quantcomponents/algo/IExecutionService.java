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

import java.net.ConnectException;
import java.util.Deque;

import com.quantcomponents.core.exceptions.RequestFailedException;

/**
 * Low-level interface to be implemented by broker adapters, to execute trades and gather order, trade and position information
 * Implementors of this interface are brokers or simulation engines.
 * Implementation should be thread-safe.
 *
 */
public interface IExecutionService extends IOrderReceiver {
	/**
	 * Adds a status listener to the execution service
	 */
	void addOrderStatusListener(IOrderStatusListener listener) throws ConnectException;
	/**
	 * Remove a status listener from the execution service
	 */
	void removeOrderStatusListener(IOrderStatusListener listener) throws ConnectException;
	/**
	 * Adds a position listener to the execution service
	 */
	void addPositionListener(IPositionListener listener) throws ConnectException;
	/**
	 * Removes a position listener from the execution service
	 */
	void removePositionListener(IPositionListener listener) throws ConnectException;
	/**
	 * Retrieves a list of all executed trades from the execution service
	 * Exactly which trades are returned is depending on the implementation.
	 * The only requirement is that they are ordered based on execution time.
	 * @return a double-linked list of executed trades
	 */
	Deque<ITrade> getTrades() throws ConnectException, RequestFailedException;
}
