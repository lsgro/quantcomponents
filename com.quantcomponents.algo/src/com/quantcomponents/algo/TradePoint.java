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

import java.io.Serializable;
import java.util.Date;


/**
 * Trade series data-point
 */
public class TradePoint implements ITradePoint, Serializable {
	private static final long serialVersionUID = 3643626849222723547L;
	private final Date index;
	private final ITrade trade;

	public TradePoint(Date index, ITrade trade) {
		this.index = index;
		this.trade = trade;
	}

	@Override
	public Date getStartIndex() {
		return getIndex();
	}

	@Override
	public Date getEndIndex() {
		return getIndex();
	}

	@Override
	public Date getIndex() {
		return index;
	}

	@Override
	public Double getBottomValue() {
		return getValue();
	}

	@Override
	public Double getTopValue() {
		return getValue();
	}

	@Override
	public Double getValue() {
		return getTrade().getExecutionPrice();
	}

	@Override
	public ITrade getTrade() {
		return trade;
	}
	
	@Override
	public String toString() {
		return "[" + getIndex() + "; " + getTrade() + "]";
	}

}
