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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.UnderComp;

public class IBConsumerDispatcher implements EWrapper {
	private List<EWrapper> delegates = new CopyOnWriteArrayList<EWrapper>();
	
	public void addDelegate(EWrapper delegate) {
		delegates.add(delegate);
	}


	public void removeDelegate(EWrapper delegate) {
		delegates.remove(delegate);
	}

	@Override
	public void error(Exception e) {
		for (EWrapper delegate : delegates) {
			delegate.error(e);
		}
	}

	@Override
	public void error(String str) {
		for (EWrapper delegate : delegates) {
			delegate.error(str);
		}
	}

	@Override
	public void error(int id, int errorCode, String errorMsg) {
		for (EWrapper delegate : delegates) {
			delegate.error(id, errorCode, errorMsg);
		}
	}

	@Override
	public void connectionClosed() {
		for (EWrapper delegate : delegates) {
			delegate.connectionClosed();
		}
	}

	@Override
	public void tickPrice(int tickerId, int field, double price,
			int canAutoExecute) {
		for (EWrapper delegate : delegates) {
			delegate.tickPrice(tickerId, field, price, canAutoExecute);
		}
	}

	@Override
	public void tickSize(int tickerId, int field, int size) {
		for (EWrapper delegate : delegates) {
			delegate.tickSize(tickerId, field, size);
		}
	}

	@Override
	public void tickOptionComputation(int tickerId, int field,
			double impliedVol, double delta, double optPrice,
			double pvDividend, double gamma, double vega, double theta,
			double undPrice) {
		for (EWrapper delegate : delegates) {
			delegate.tickOptionComputation(tickerId, field, impliedVol, delta, optPrice, pvDividend, gamma, vega, theta, undPrice);
		}
	}

	@Override
	public void tickGeneric(int tickerId, int tickType, double value) {
		for (EWrapper delegate : delegates) {
			delegate.tickGeneric(tickerId, tickType, value);
		}
	}

	@Override
	public void tickString(int tickerId, int tickType, String value) {
		for (EWrapper delegate : delegates) {
			delegate.tickString(tickerId, tickType, value);
		}
	}

	@Override
	public void tickEFP(int tickerId, int tickType, double basisPoints,
			String formattedBasisPoints, double impliedFuture, int holdDays,
			String futureExpiry, double dividendImpact, double dividendsToExpiry) {
		for (EWrapper delegate : delegates) {
			delegate.tickEFP(tickerId, tickType, basisPoints, formattedBasisPoints, impliedFuture, holdDays, futureExpiry, dividendImpact, dividendsToExpiry);
		}
	}

	@Override
	public void orderStatus(int orderId, String status, int filled,
			int remaining, double avgFillPrice, int permId, int parentId,
			double lastFillPrice, int clientId, String whyHeld) {
		for (EWrapper delegate : delegates) {
			delegate.orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
		}
	}

	@Override
	public void openOrder(int orderId, Contract contract, Order order,
			OrderState orderState) {
		for (EWrapper delegate : delegates) {
			delegate.openOrder(orderId, contract, order, orderState);
		}
	}

	@Override
	public void openOrderEnd() {
		for (EWrapper delegate : delegates) {
			delegate.openOrderEnd();
		}
	}

	@Override
	public void updateAccountValue(String key, String value, String currency,
			String accountName) {
		for (EWrapper delegate : delegates) {
			delegate.updateAccountValue(key, value, currency, accountName);	
		}
	}

	@Override
	public void updatePortfolio(Contract contract, int position,
			double marketPrice, double marketValue, double averageCost,
			double unrealizedPNL, double realizedPNL, String accountName) {
		for (EWrapper delegate : delegates) {
			delegate.updatePortfolio(contract, position, marketPrice, marketValue, averageCost, unrealizedPNL, realizedPNL, accountName);
		}
	}

	@Override
	public void updateAccountTime(String timeStamp) {
		for (EWrapper delegate : delegates) {
			delegate.updateAccountTime(timeStamp);
		}
	}

	@Override
	public void accountDownloadEnd(String accountName) {
		for (EWrapper delegate : delegates) {
			delegate.accountDownloadEnd(accountName);
		}
	}

	@Override
	public void nextValidId(int orderId) {
		for (EWrapper delegate : delegates) {
			delegate.nextValidId(orderId);
		}
	}

	@Override
	public void contractDetails(int reqId, ContractDetails contractDetails) {
		for (EWrapper delegate : delegates) {
			delegate.contractDetails(reqId, contractDetails);
		}
	}

	@Override
	public void bondContractDetails(int reqId, ContractDetails contractDetails) {
		for (EWrapper delegate : delegates) {
			delegate.bondContractDetails(reqId, contractDetails);
		}
	}

	@Override
	public void contractDetailsEnd(int reqId) {
		for (EWrapper delegate : delegates) {
			delegate.contractDetailsEnd(reqId);
		}
	}

	@Override
	public void execDetails(int reqId, Contract contract, Execution execution) {
		for (EWrapper delegate : delegates) {
			delegate.execDetails(reqId, contract, execution);
		}
	}

	@Override
	public void execDetailsEnd(int reqId) {
		for (EWrapper delegate : delegates) {
			delegate.execDetailsEnd(reqId);
		}
	}

	@Override
	public void updateMktDepth(int tickerId, int position, int operation,
			int side, double price, int size) {
		for (EWrapper delegate : delegates) {
			delegate.updateMktDepth(tickerId, position, operation, side, price, size);
		}

	}

	@Override
	public void updateMktDepthL2(int tickerId, int position,
			String marketMaker, int operation, int side, double price, int size) {
		for (EWrapper delegate : delegates) {
			delegate.updateMktDepthL2(tickerId, position, marketMaker, operation, side, price, size);
		}
	}

	@Override
	public void updateNewsBulletin(int msgId, int msgType, String message,
			String origExchange) {
		for (EWrapper delegate : delegates) {
			delegate.updateNewsBulletin(msgId, msgType, message, origExchange);
		}
	}

	@Override
	public void managedAccounts(String accountsList) {
		for (EWrapper delegate : delegates) {
			delegate.managedAccounts(accountsList);
		}
	}

	@Override
	public void receiveFA(int faDataType, String xml) {
		for (EWrapper delegate : delegates) {
			delegate.receiveFA(faDataType, xml);
		}
	}

	@Override
	public void historicalData(int reqId, String date, double open,
			double high, double low, double close, int volume, int count,
			double WAP, boolean hasGaps) {
		for (EWrapper delegate : delegates) {
			delegate.historicalData(reqId, date, open, high, low, close, volume, count, WAP, hasGaps);
		}
	}

	@Override
	public void scannerParameters(String xml) {
		for (EWrapper delegate : delegates) {
			delegate.scannerParameters(xml);
		}
	}

	@Override
	public void scannerData(int reqId, int rank,
			ContractDetails contractDetails, String distance, String benchmark,
			String projection, String legsStr) {
		for (EWrapper delegate : delegates) {
			delegate.scannerData(reqId, rank, contractDetails, distance, benchmark, projection, legsStr);
		}
	}

	@Override
	public void scannerDataEnd(int reqId) {
		for (EWrapper delegate : delegates) {
			delegate.scannerDataEnd(reqId);
		}
	}

	@Override
	public void realtimeBar(int reqId, long time, double open, double high,
			double low, double close, long volume, double wap, int count) {
		for (EWrapper delegate : delegates) {
			delegate.realtimeBar(reqId, time, open, high, low, close, volume, wap, count);
		}
	}

	@Override
	public void currentTime(long time) {
		for (EWrapper delegate : delegates) {
			delegate.currentTime(time);
		}
	}

	@Override
	public void fundamentalData(int reqId, String data) {
		for (EWrapper delegate : delegates) {
			delegate.fundamentalData(reqId, data);
		}
	}

	@Override
	public void deltaNeutralValidation(int reqId, UnderComp underComp) {
		for (EWrapper delegate : delegates) {
			delegate.deltaNeutralValidation(reqId, underComp);
		}
	}

	@Override
	public void tickSnapshotEnd(int reqId) {
		for (EWrapper delegate : delegates) {
			delegate.tickSnapshotEnd(reqId);
		}
	}

	@Override
	public void marketDataType(int reqId, int marketDataType) {
		for (EWrapper delegate : delegates) {
			delegate.marketDataType(reqId, marketDataType);
		}
	}

	@Override
	public void commissionReport(CommissionReport commissionReport) {
		for (EWrapper delegate : delegates) {
			delegate.commissionReport(commissionReport);
		}
	}
}
