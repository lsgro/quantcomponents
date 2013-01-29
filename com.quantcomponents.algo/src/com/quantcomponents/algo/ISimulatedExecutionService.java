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

import java.util.Collection;
import java.util.Date;

import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * 
 * The simulated execution service supplements a real execution service with
 * a method to pass the input series used for the simulation.
 * They are used by the simulator to calculate execution prices and position
 * changes.
 */
public interface ISimulatedExecutionService extends IExecutionService {
	/**
	 * This method is used to set the input series to be used for the algorithm execution
	 * @param inputSeries a set of input series
	 */
	void setInputSeries(Collection<ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> inputSeries);
}
