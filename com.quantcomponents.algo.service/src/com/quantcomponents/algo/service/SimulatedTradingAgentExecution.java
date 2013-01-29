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

import java.net.ConnectException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.quantcomponents.algo.IInputSeriesStreamer;
import com.quantcomponents.algo.IManagedRunnable;
import com.quantcomponents.algo.ISimulatedExecutionService;
import com.quantcomponents.algo.ITradingAgent;
import com.quantcomponents.algo.ITradingAgentExecution;
import com.quantcomponents.algo.InputSeriesStreamer;
import com.quantcomponents.core.model.IMutableSeries;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;

public class SimulatedTradingAgentExecution extends AbstractTradingAgentExecution implements ITradingAgentExecution, IManagedRunnable {
	private static final long ALGO_START_WAIT_QUANTUM = 100;
	volatile IInputSeriesStreamer inputSeriesStreamer;

	public SimulatedTradingAgentExecution(ITradingAgent tradingAgent, ISimulatedExecutionService executionService) {
		super(tradingAgent, executionService);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		Map<String, IMutableSeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> targetSeriesMap = new HashMap<String, IMutableSeries<Date, Double, ? extends ISeriesPoint<Date, Double>>>();
		for (Map.Entry<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> entry : inputSeriesMap.entrySet()) {
			targetSeriesMap.put(entry.getKey(), entry.getValue().createEmptyMutableSeries(entry.getValue().getPersistentID()));
		}
		this.inputSeriesStreamer = new InputSeriesStreamer(inputSeriesMap, targetSeriesMap);
		Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> simulatedInputSeries = inputSeriesStreamer.getStreamingSeries();
		((ISimulatedExecutionService) executionService).setInputSeries((Collection<ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>>) simulatedInputSeries.values());
		try {
			executionService.addPositionListener(this);
		} catch (ConnectException e) {
			logger.log(Level.SEVERE, "Exception while adding position listener", e);
		}
		try {
			executionService.addOrderStatusListener(this);
		} catch (ConnectException e) {
			logger.log(Level.SEVERE, "Exception while adding order status listener", e);
		}
		tradingAgent.wire(simulatedInputSeries, outputSeries);
		tradingAgent.setOrderReceiver(this);
		Thread algoThread = new Thread(new Runnable() {
			@Override
			public void run() {
				tradingAgent.run();
				try {
					executionService.removeOrderStatusListener(SimulatedTradingAgentExecution.this);
				} catch (ConnectException e) {
					logger.log(Level.SEVERE, "Exception while removing order status listener", e);
				}
				try {
					executionService.removePositionListener(SimulatedTradingAgentExecution.this);
				} catch (ConnectException e) {
					logger.log(Level.SEVERE, "Exception while removing position listener", e);
				}
			}
		});
		algoThread.start();
		while (tradingAgent.getRunningStatus() == RunningStatus.NEW) {
			try {
				Thread.sleep(ALGO_START_WAIT_QUANTUM);
			} catch (InterruptedException e) {
				logger.log(Level.WARNING, "Interrupted while waiting for algo to start", e);
			}
		}
		inputSeriesStreamer.run();
		tradingAgent.inputComplete();
		try {
			algoThread.join();
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Algo thread interrupted", e);
		}
		tradingAgent.unwire();
	}

	@Override
	protected Date getCurrentTime() {
		return inputSeriesStreamer.getLastTimestamp();
	}
}
