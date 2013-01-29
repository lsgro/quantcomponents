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
import java.util.Date;
import java.util.Map;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesAugmentable;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Placeholder for persisted terminated executions
 * It maintains the basic information to be shown to the user, without reference to live services
 */
public class DummyTradingAgentExecution implements ITradingAgentExecution {
	protected volatile Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> inputSeriesMap;
	protected volatile ISeriesAugmentable<Date, Double, ISeriesPoint<Date, Double>> outputSeries;

	@Override
	public void wire(Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> input, ISeriesAugmentable<Date, Double, ISeriesPoint<Date, Double>> output) {
		this.inputSeriesMap = input;
		this.outputSeries = output;
	}

	@Override
	public void unwire() {
		this.inputSeriesMap = null;
		this.outputSeries = null;
	}

	@Override
	public void pause() { }

	@Override
	public void resume() { }

	@Override
	public void kill() { }

	@Override
	public RunningStatus getRunningStatus() {
		return RunningStatus.TERMINATED;
	}

	@Override
	public void run() { }

	@Override
	public String sendOrder(IOrder order) throws ConnectException, RequestFailedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] sendBracketOrders(IOrder parent, IOrder[] children) throws ConnectException, RequestFailedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onPositionUpdate(IContract contract, IPosition position) { }

	@Override
	public Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> getInput() {
		return inputSeriesMap;
	}

	@Override
	public ISeries<Date, Double, ISeriesPoint<Date, Double>> getOutput() {
		return outputSeries;
	}
}
