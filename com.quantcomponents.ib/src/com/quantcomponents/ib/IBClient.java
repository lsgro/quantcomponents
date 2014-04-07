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

import com.ib.client.EClientSocket;



public abstract class IBClient extends IBWrapperHelper {
	private static final Logger logger = Logger.getLogger(IBClient.class.getName());
	private EClientSocket sender;
	private int pendingReqId;
	private volatile boolean requestInProgress;
	private Exception exception;
	private String errorMessage;
	
	abstract protected void doSendRequest();
	
	public void sendRequest(int reqId) {
		requestInProgress = true;
		pendingReqId = reqId;
		doSendRequest();
	}
	
	public int getPendingReqId() {
		return pendingReqId;
	}

	public boolean isRequestInProgress() {
		return requestInProgress;
	}
	
	public boolean hasErrors() {
		return exception != null || errorMessage != null;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	@Override
	public void error(Exception e) {
		exception = e;
		errorMessage = e.getMessage();
		setRequestComplete();
		e.printStackTrace();
	}

	@Override
	public void error(String str) {
		errorMessage = str;
		setRequestComplete();
		logger.log(Level.WARNING, "Message from IB: " + str);
	}

	@Override
	public void error(int id, int errorCode, String errorMsg) {
		if (id == pendingReqId) {
			errorMessage = "id: " + id + "; code: " + errorCode + " [" + errorMsg + "]";
			setRequestComplete();
		}
		if (errorCode == 1101) //Connectivity between IB and TWS has been lost - data lost
		{
			errorMessage = "id: " + id + "; code: " + errorCode + " [" + errorMsg + "]";
			setRequestComplete();
			sendRequest(pendingReqId + 1000000); //resubmit the request
		}
		logger.log(Level.WARNING, "Message from IB: id: " + id + "; code: " + errorCode + " [" + errorMsg + "]");
	}
	
	protected EClientSocket getSender() {
		return sender;
	}
	
	protected void setRequestComplete() {
		this.requestInProgress = false;
	}
	
	/* package */ void registerSender(EClientSocket sender) {
		this.sender = sender;
	}
	
	/* package */ void unregisterSender() {
		this.sender = null;
	}
}
