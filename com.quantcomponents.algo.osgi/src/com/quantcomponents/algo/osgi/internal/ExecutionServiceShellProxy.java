/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.osgi.internal;

import java.net.ConnectException;
import java.util.Deque;

import com.quantcomponents.algo.IExecutionService;
import com.quantcomponents.algo.IOrder;
import com.quantcomponents.algo.IOrderStatusListener;
import com.quantcomponents.algo.IPositionListener;
import com.quantcomponents.algo.ITrade;
import com.quantcomponents.core.exceptions.RequestFailedException;

public class ExecutionServiceShellProxy implements IExecutionService {
	private final IExecutionService service;

	public ExecutionServiceShellProxy(IExecutionService service) {
		this.service = service;
	}

	@Override
	public void addOrderStatusListener(IOrderStatusListener listener) throws ConnectException {
		service.addOrderStatusListener(listener);
	}

	@Override
	public void removeOrderStatusListener(IOrderStatusListener listener) throws ConnectException {
		service.removeOrderStatusListener(listener);
	}

	@Override
	public void addPositionListener(IPositionListener listener) throws ConnectException {
		service.addPositionListener(listener);
	}

	@Override
	public void removePositionListener(IPositionListener listener) throws ConnectException {
		service.removePositionListener(listener);
	}

	@Override
	public String sendOrder(IOrder order) throws ConnectException, RequestFailedException {
		return service.sendOrder(order);
	}

	@Override
	public String[] sendBracketOrders(IOrder parent, IOrder[] children) throws ConnectException, RequestFailedException {
		return service.sendBracketOrders(parent, children);
	}

	@Override
	public Deque<ITrade> getTrades() throws ConnectException, RequestFailedException {
		return service.getTrades();
	}

}
