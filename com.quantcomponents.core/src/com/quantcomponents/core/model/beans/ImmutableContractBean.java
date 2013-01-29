/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.model.beans;

import java.io.Serializable;
import java.util.Currency;

import com.quantcomponents.core.model.BareDate;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.IdentifierType;
import com.quantcomponents.core.model.OptionRight;
import com.quantcomponents.core.model.SecurityType;

/**
 * 
 * Immutable version of a contract bean
 */
public final class ImmutableContractBean implements IContract, Serializable {
	private static final long serialVersionUID = -8122157418460477205L;
	private final ContractBean inner;
	
	public ImmutableContractBean(IContract bean) {
		inner = ContractBean.copyOf(bean);
	}

	@Override
	public boolean equals(Object o) {
		return inner.equals(o);
	}

	@Override
	public int hashCode() {
		return inner.hashCode();
	}

	@Override
	public String toString() {
		return inner.toString();
	}

	@Override
	public String getSymbol() {
		return inner.getSymbol();
	}

	@Override
	public SecurityType getSecurityType() {
		return inner.getSecurityType();
	}

	@Override
	public BareDate getExpiryDate() {
		return inner.getExpiryDate();
	}

	@Override
	public Double getStrike() {
		return inner.getStrike();
	}

	@Override
	public OptionRight getOptionRight() {
		return inner.getOptionRight();
	}

	@Override
	public Integer getMultiplier() {
		return inner.getMultiplier();
	}

	@Override
	public String getExchange() {
		return inner.getExchange();
	}

	@Override
	public String getPrimaryExchange() {
		return inner.getPrimaryExchange();
	}

	@Override
	public Currency getCurrency() {
		return inner.getCurrency();
	}

	@Override
	public IdentifierType getIdentifierType() {
		return inner.getIdentifierType();
	}

	@Override
	public String getIdentifier() {
		return inner.getIdentifier();
	}

	@Override
	public ContractDescBean getContractDescription() {
		return inner.getContractDescription();
	}

	@Override
	public String getBrokerID() {
		return inner.getBrokerID();
	}
	
}
