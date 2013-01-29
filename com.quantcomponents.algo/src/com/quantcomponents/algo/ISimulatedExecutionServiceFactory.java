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
 * Interface for the creation of disposable simulated execution services.
 * Contrary to {@link IExecutionService}, the simulated execution services live only for one simulation.
 * Therefore the simulation facility uses a factory to create a new execution service for each run
 */
public interface ISimulatedExecutionServiceFactory {
	/**
	 * Creates a new disposable simulated execution service
	 */
	ISimulatedExecutionService createSimulatedExecutionService();
}
