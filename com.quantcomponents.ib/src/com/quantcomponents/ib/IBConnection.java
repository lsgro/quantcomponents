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

import com.ib.client.EClientSocket;


public class IBConnection {
	private String host;
	private int port;
	private int clientId;
	private EClientSocket sender;
	private IBConsumerDispatcher receiver;
	
	public IBConnection(String host, int port, int clientId) {
		this.host = host;
		this.port = port;
		this.clientId = clientId;
		receiver = new IBConsumerDispatcher();
		sender = new EClientSocket(receiver);
		sender.eConnect(host, port, clientId);
	}
	
	public void addClient(IBClient client) {
		client.registerSender(sender);
		receiver.addDelegate(client);
	}
	
	public void removeClient(IBClient client) {
		receiver.removeDelegate(client);
		client.unregisterSender();
	}
	
	public void close() {
		sender.eDisconnect();
	}
	
	public boolean isConnected() {
		return sender.isConnected();
	}
	
	public void reconnect() {
		sender.eConnect(host, port, clientId);
	}
	
	protected EClientSocket getSender() {
		return sender;
	}
}
