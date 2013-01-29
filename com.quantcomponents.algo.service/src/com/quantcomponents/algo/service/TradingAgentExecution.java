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
import java.util.Date;
import java.util.logging.Level;

import com.quantcomponents.algo.IExecutionService;
import com.quantcomponents.algo.ITradingAgent;
import com.quantcomponents.algo.ITradingAgentExecution;



public class TradingAgentExecution extends AbstractTradingAgentExecution implements ITradingAgentExecution {
	public TradingAgentExecution(ITradingAgent tradingAgent, IExecutionService executionService) {
		super(tradingAgent, executionService);
	}

	@Override
	public void run() {
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
		tradingAgent.wire(inputSeriesMap, outputSeries);
		tradingAgent.setOrderReceiver(this);
		tradingAgent.run();
		tradingAgent.unwire();
		try {
			executionService.removeOrderStatusListener(this);
		} catch (ConnectException e) {
			logger.log(Level.SEVERE, "Exception while removing order status listener", e);
		}
		try {
			executionService.removePositionListener(this);
		} catch (ConnectException e) {
			logger.log(Level.SEVERE, "Exception while removing position listener", e);
		}
	}

	@Override
	protected Date getCurrentTime() {
		return new Date();
	}
}
