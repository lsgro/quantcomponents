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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ib.client.Execution;
import com.quantcomponents.algo.IOrder;
import com.quantcomponents.algo.ITrade;
import com.quantcomponents.algo.TradeBean;

public class IBTradeInfo implements ITrade {
	private final Execution iBExecution;
	private final IOrder order;
	private final SimpleDateFormat timestampDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	public IBTradeInfo(Execution iBExecution, IOrder order) {
		this.iBExecution = iBExecution;
		this.order = order;
	}

	@Override
	public IOrder getOrder() {
		return order;
	}

	@Override
	public String getExchange() {
		return iBExecution.m_exchange;
	}

	@Override
	public Date getExecutionTime() {
		try {
			return timestampDateFormat.parse(iBExecution.m_time);
		} catch (ParseException e) {
			return null;
		}
	}

	@Override
	public int getAmount() {
		return iBExecution.m_shares;
	}

	@Override
	public double getExecutionPrice() {
		return iBExecution.m_price;
	}

	@Override
	public double getAveragePrice() {
		return iBExecution.m_avgPrice;
	}

	@Override
	public String toString() {
		return TradeBean.stringRepr(this);
	}

}
