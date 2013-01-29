/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata;

import java.io.Serializable;
import java.util.Date;

import com.quantcomponents.core.model.DataType;

/**
 * Implementation of {@link ITickPoint}
 */
public class TickPoint implements ITickPoint, Serializable {
	private static final long serialVersionUID = 1420587711067980277L;
	private final Date index;
	private final DataType dataType;
	private final double price;
	private final int size;

	public TickPoint(Date index, DataType dataType, double price, int size) {
		this.index = index;
		this.dataType = dataType;
		this.price = price;
		this.size = size;
	}

	@Override
	public Date getIndex() {
		return index;
	}

	@Override
	public DataType getDataType() {
		return dataType;
	}

	@Override
	public Double getBottomValue() {
		return price;
	}

	@Override
	public Double getTopValue() {
		return price;
	}

	@Override
	public Double getValue() {
		return price;
	}

	@Override
	public Integer getSize() {
		return size;
	}

	@Override
	public Date getStartIndex() {
		return getIndex();
	}

	@Override
	public Date getEndIndex() {
		return getIndex();
	}
}
