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

import com.quantcomponents.core.model.ISeriesProcessor;

/**
 * Trading algorithm instance
 * Instances are created by implementors of {@link ITradingAgentFactory}, and are operated by means of a hierarchy
 * of handles.
 * @see ITradingHierarchyManager
 * @see ITradingManager
 * @see IManagedRunnable
 */
public interface ITradingAgent extends ISeriesProcessor<Date, Double>, IManagedRunnable, IOrderStatusListener, IPositionListener {
	/**
	 * Set the execution service before starting the algorithm
	 * @param orderReceiver an execution service
	 */
	void setOrderReceiver(IOrderReceiver orderReceiver);
	/**
	 * Communicates to the trading algorithm that the input is terminated.
	 * Method useful when simulating, to finalise the status of the agent, and return from the execution thread
	 */
	void inputComplete();
}
