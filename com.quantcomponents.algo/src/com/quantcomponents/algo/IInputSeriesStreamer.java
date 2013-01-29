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
import java.util.Map;

import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Simulator of realtime series.
 * Implementors simulate a realtime series by reading historical data and filling a slave
 * series, initially empty.
 * The iteration must be carried out synchronously from a single thread, to give the client
 * a way to react synchronously during a simulation.
 * At each iteration, clients can read the timestamp of the last item added to the series:
 * this timestamp can be used as an approximated current time within the simulation.
 */
public interface IInputSeriesStreamer extends Runnable {
	Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> getStreamingSeries();
	Date getLastTimestamp();
}
