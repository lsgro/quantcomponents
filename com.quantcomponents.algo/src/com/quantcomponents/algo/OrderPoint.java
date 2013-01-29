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
 * Order series data-point
 */
public class OrderPoint implements IOrderPoint, Serializable {
	private static final long serialVersionUID = 6275886522289561129L;
	private final Date index;
	private final IOrder order;
	
	public OrderPoint(Date index, IOrder order) {
		this.index = index;
		this.order = order;
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
		return order.getLimitPrice();
	}

	@Override
	public IOrder getOrder() {
		return order;
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
	public String toString() {
		return "[" + getIndex() + ": " + getOrder().toString() + "]";
	}
}
