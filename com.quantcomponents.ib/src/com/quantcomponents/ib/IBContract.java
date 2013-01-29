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

import java.util.Currency;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.quantcomponents.core.model.BareDate;
import com.quantcomponents.core.model.IContractDesc;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.IdentifierType;
import com.quantcomponents.core.model.OptionRight;
import com.quantcomponents.core.model.SecurityType;
import com.quantcomponents.core.model.beans.ContractBase;

public class IBContract extends ContractBase implements IContract {
	private final Contract iBContract;
	private ContractDetails details;
	private final IBConstantTranslator constantTranslator;
	
	public IBContract(IBConstantTranslator constantTranslator) {
		this(new Contract(), constantTranslator);
	}
	
	public IBContract(Contract iBContract, IBConstantTranslator constantTranslator) {
		this.iBContract = iBContract;
		this.constantTranslator = constantTranslator;
	}
	
	public static Contract toIBContract(IContract contract, IBConstantTranslator constantTranslator) {
		IBContract contractSpecs = new IBContract(constantTranslator);
		contractSpecs.setCurrency(contract.getCurrency());
		contractSpecs.setExchange(contract.getExchange());
		contractSpecs.setPrimaryExchange(contract.getPrimaryExchange());
		contractSpecs.setExpiryDate(contract.getExpiryDate());
		contractSpecs.setIdentifier(contract.getIdentifier());
		contractSpecs.setIdentifierType(contract.getIdentifierType());
		contractSpecs.setMultiplier(contract.getMultiplier());
		contractSpecs.setOptionRight(contract.getOptionRight());
		contractSpecs.setSecurityType(contract.getSecurityType());
		contractSpecs.setStrike(contract.getStrike());
		contractSpecs.setSymbol(contract.getSymbol());
		return contractSpecs.iBContract;
	}

	public ContractDetails getDetails() {
		return details;
	}

	public void setDetails(ContractDetails details) {
		this.details = details;
	}

	@Override
	public String getSymbol() {
		return iBContract.m_symbol;
	}
	
	public void setSymbol(String symbol) {
		iBContract.m_symbol = symbol;
	}

	@Override
	public SecurityType getSecurityType() {
		if (iBContract.m_secType == null) {
			return null;
		} else {
			return constantTranslator.getSecurityType(iBContract.m_secType);
		}	
	}
	
	public void setSecurityType(SecurityType securityType) {
		if (securityType == null) {
			iBContract.m_secType = null;
		} else {
			iBContract.m_secType = constantTranslator.getCode(securityType);
		}
	}

	@Override
	public BareDate getExpiryDate() {
		if (iBContract.m_expiry == null) {
			return null;
		} else {
			return new BareDate(iBContract.m_expiry);
		}
	}
	
	public void setExpiryDate(BareDate expiryDate) {
		if (expiryDate == null) {
			iBContract.m_expiry = null;
		} else {
			iBContract.m_expiry = expiryDate.getDateRepr();
		}
	}

	@Override
	public Double getStrike() {
		return iBContract.m_strike;
	}
	
	public void setStrike(Double strike) {
		if (strike == null)
			strike = 0.0;
		iBContract.m_strike = strike;
	}

	@Override
	public OptionRight getOptionRight() {
		if (iBContract.m_right == null) {
			return null;
		} else {
			return constantTranslator.getOptionRight(iBContract.m_right);
		}
	}
	
	public void setOptionRight(OptionRight optionRight) {
		if (optionRight == null) {
			iBContract.m_right = null;
		} else {
			iBContract.m_right = constantTranslator.getCode(optionRight);
		}
	}

	@Override
	public Integer getMultiplier() {
		if (iBContract.m_multiplier == null) {
			return null;
		} else {
			return new Integer(iBContract.m_multiplier);
		}
	}
	
	public void setMultiplier(Integer multiplier) {
		if (multiplier == null) {
			iBContract.m_multiplier = null;
		} else {
			iBContract.m_multiplier = Integer.toString(multiplier);
		}
	}

	@Override
	public String getExchange() {
		return iBContract.m_exchange;
	}

	public void setExchange(String exchange) {
		iBContract.m_exchange = exchange;
	}

	@Override
	public String getPrimaryExchange() {
		return iBContract.m_primaryExch;
	}
	
	public void setPrimaryExchange(String exchange) {
		iBContract.m_primaryExch = exchange;
	}

	@Override
	public Currency getCurrency() {
		if (iBContract.m_currency == null) {
			return null;
		} else {
			return Currency.getInstance(iBContract.m_currency);
		}
	}

	public void setCurrency(Currency currency) {
		if (currency == null) {
			iBContract.m_currency = null;
		} else {
			iBContract.m_currency = currency.getCurrencyCode();
		}
	}
	
	@Override
	public IdentifierType getIdentifierType() {
		if (iBContract.m_secIdType == null) {
			return null;
		} else {
			return constantTranslator.getIdentifierType(iBContract.m_secIdType);
		}
	}

	public void setIdentifierType(IdentifierType identifierType) {
		if (identifierType == null) {
			iBContract.m_secIdType = null;
		} else {
			iBContract.m_secIdType = constantTranslator.getCode(identifierType);
		}
	}
	
	@Override
	public String getIdentifier() {
		return iBContract.m_secId;
	}
	
	public void setIdentifier(String identifier) {
		iBContract.m_secId = identifier;
	}

	@Override
	public String getBrokerID() {
		return Integer.toString(iBContract.m_conId);
	}

	public void setBrokerID(String id) {
		iBContract.m_conId = Integer.parseInt(id);
	}
	
	@Override
	public IContractDesc getContractDescription() {
		return details == null ? null : new IBContractDesc(details, constantTranslator);
	}
}
