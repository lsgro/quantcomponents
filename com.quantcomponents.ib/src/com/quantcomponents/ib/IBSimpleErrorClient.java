/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ib;

import java.util.logging.Level;
import java.util.logging.Logger;

public class IBSimpleErrorClient extends IBClient {
	private static final Logger logger = Logger.getLogger(IBSimpleErrorClient.class.getName());
	
	@Override
	public void error(Exception e) {
		e.printStackTrace(System.err);
	}

	@Override
	public void error(String str) {
		logger.log(Level.WARNING , "Message received from IB: " + str);
	}

	@Override
	public void error(int id, int errorCode, String errorMsg) {
		logger.log(Level.WARNING, "Message received from IB: id=" + id + "; code=" + errorCode + "; msg=" + errorMsg);
	}
	
	@Override
	public void connectionClosed() {
		logger.log(Level.INFO, "Connection closed");
	}

	@Override
	protected void doSendRequest() {}
}
