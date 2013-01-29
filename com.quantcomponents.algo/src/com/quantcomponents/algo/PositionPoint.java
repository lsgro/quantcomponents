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

import com.quantcomponents.core.model.IContract;

/**
 * Position series data-point
 */
public class PositionPoint implements IPositionPoint, Serializable {
	private static final long serialVersionUID = -1401025442537545247L;
	private final IContract contract;
	private final IPosition position;

	public PositionPoint(IContract contract, IPosition position) {
		this.contract = contract;
		this.position = position;
	}

	@Override
	public Date getIndex() {
		return position.getTimestamp();
	}

	@Override
	public IContract getContract() {
		return contract;
	}

	@Override
	public IPosition getPosition() {
		return position;
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
		return (double) position.getSignedAmount();
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
		return "[" + getIndex() + ": " + getContract().toString() + "; " + getPosition().toString() + "]";
	}
}
