/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.series;

import java.io.Serializable;
import java.util.Date;

import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Simple data point with one index and one data value
 */
public class SimplePoint implements ISeriesPoint<Date, Double>, Serializable {
	private static final long serialVersionUID = -4442165749544610075L;
	private Date index;
	private Double value;
	
	public SimplePoint() {}
	
	public SimplePoint(Date index, Double value) {
		this.index = index;
		this.value = value;
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

	public Double getValue() {
		return value;
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
