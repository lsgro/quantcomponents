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

import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.UnderComp;


public class IBWrapperHelper implements EWrapper {
	private static final Logger logger = Logger.getLogger(IBWrapperHelper.class.getName());
	
	private boolean trace;
	
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public void error(Exception e) {
		if (trace) logger.log(Level.INFO, "error(Exception e)");
	}

	@Override
	public void error(String str) {
		if (trace) logger.log(Level.INFO, "error(String str)");
	}

	@Override
	public void error(int id, int errorCode, String errorMsg) {
		if (trace) logger.log(Level.INFO, "error(int id, int errorCode, String errorMsg)");
	}

	@Override
	public void connectionClosed() {
		if (trace) logger.log(Level.INFO, "connectionClosed()");
	}

	@Override
	public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
		if (trace) logger.log(Level.INFO, "tickPrice(int tickerId, int field, double price, int canAutoExecute)");
	}

	@Override
	public void tickSize(int tickerId, int field, int size) {
		if (trace) logger.log(Level.INFO, "tickSize(int tickerId, int field, int size)");
	}

	@Override
	public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
		if (trace) logger.log(Level.INFO, "tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice)");
	}

	@Override
	public void tickGeneric(int tickerId, int tickType, double value) {
		if (trace) logger.log(Level.INFO, "tickGeneric(int tickerId, int tickType, double value)");
	}

	@Override
	public void tickString(int tickerId, int tickType, String value) {
		if (trace) logger.log(Level.INFO, "tickString(int tickerId, int tickType, String value)");
	}

	@Override
	public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureExpiry, double dividendImpact, double dividendsToExpiry) {
		if (trace) logger.log(Level.INFO, "tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureExpiry, double dividendImpact, double dividendsToExpiry)");
	}

	@Override
	public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
		if (trace) logger.log(Level.INFO, "orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld)");
	}

	@Override
	public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
		if (trace) logger.log(Level.INFO, "openOrder(int orderId, Contract contract, Order order, OrderState orderState)");
	}

	@Override
	public void openOrderEnd() {
		if (trace) logger.log(Level.INFO, "openOrderEnd()");
	}

	@Override
	public void updateAccountValue(String key, String value, String currency, String accountName) {
		if (trace) logger.log(Level.INFO, "updateAccountValue(String key, String value, String currency, String accountName)");
	}

	@Override
	public void updatePortfolio(Contract contract, int position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
		if (trace) logger.log(Level.INFO, "updatePortfolio(Contract contract, int position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName)");
	}

	@Override
	public void updateAccountTime(String timeStamp) {
		if (trace) logger.log(Level.INFO, "updateAccountTime(String timeStamp)");
	}

	@Override
	public void accountDownloadEnd(String accountName) {
		if (trace) logger.log(Level.INFO, "accountDownloadEnd(String accountName)");
	}

	@Override
	public void nextValidId(int orderId) {
		if (trace) logger.log(Level.INFO, "nextValidId(int orderId)");
	}

	@Override
	public void contractDetails(int reqId, ContractDetails contractDetails) {
		if (trace) logger.log(Level.INFO, "contractDetails(int reqId, ContractDetails contractDetails)");
	}

	@Override
	public void bondContractDetails(int reqId, ContractDetails contractDetails) {
		if (trace) logger.log(Level.INFO, "bondContractDetails(int reqId, ContractDetails contractDetails)");
	}

	@Override
	public void contractDetailsEnd(int reqId) {
		if (trace) logger.log(Level.INFO, "contractDetailsEnd(int reqId)");
	}

	@Override
	public void execDetails(int reqId, Contract contract, Execution execution) {
		if (trace) logger.log(Level.INFO, "execDetails(int reqId, Contract contract, Execution execution)");
	}

	@Override
	public void execDetailsEnd(int reqId) {
		if (trace) logger.log(Level.INFO, "execDetailsEnd(int reqId)");
	}

	@Override
	public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) {
		if (trace) logger.log(Level.INFO, "updateMktDepth(int tickerId, int position, int operation, int side, double price, int size)");
	}

	@Override
	public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size) {
		if (trace) logger.log(Level.INFO, "updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size)");
	}

	@Override
	public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
		if (trace) logger.log(Level.INFO, "updateNewsBulletin(int msgId, int msgType, String message, String origExchange)");
	}

	@Override
	public void managedAccounts(String accountsList) {
		if (trace) logger.log(Level.INFO, "managedAccounts(String accountsList)");
	}

	@Override
	public void receiveFA(int faDataType, String xml) {
		if (trace) logger.log(Level.INFO, "receiveFA(int faDataType, String xml)");
	}

	@Override
	public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
		if (trace) logger.log(Level.INFO, "historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps)");
	}

	@Override
	public void scannerParameters(String xml) {
		if (trace) logger.log(Level.INFO, "scannerParameters(String xml)");
	}

	@Override
	public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
		if (trace) logger.log(Level.INFO, "scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr)");
	}

	@Override
	public void scannerDataEnd(int reqId) {
		if (trace) logger.log(Level.INFO, "scannerDataEnd(int reqId)");
	}

	@Override
	public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {
		if (trace) logger.log(Level.INFO, "realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count)");
	}

	@Override
	public void currentTime(long time) {
		if (trace) logger.log(Level.INFO, "currentTime(long time)");
	}

	@Override
	public void fundamentalData(int reqId, String data) {
		if (trace) logger.log(Level.INFO, "fundamentalData(int reqId, String data)");
	}

	@Override
	public void deltaNeutralValidation(int reqId, UnderComp underComp) {
		if (trace) logger.log(Level.INFO, "deltaNeutralValidation(int reqId, UnderComp underComp)");
	}

	@Override
	public void tickSnapshotEnd(int reqId) {
		if (trace) logger.log(Level.INFO, "tickSnapshotEnd(int reqId)");
	}

	@Override
	public void marketDataType(int reqId, int marketDataType) {
		if (trace) logger.log(Level.INFO, "marketDataType(int reqId, int marketDataType)");
	}

	@Override
	public void commissionReport(CommissionReport commissionReport) {
		if (trace) logger.log(Level.INFO, "commissionReport(CommissionReport commissionReport)");
	}
}
