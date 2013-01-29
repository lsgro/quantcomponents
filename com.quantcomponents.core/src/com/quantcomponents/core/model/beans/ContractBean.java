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

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.quantcomponents.core.model.BareDate;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.IdentifierType;
import com.quantcomponents.core.model.OptionRight;
import com.quantcomponents.core.model.SecurityType;
import com.quantcomponents.core.xml.XmlBareDateAdapter;
import com.quantcomponents.core.xml.XmlCurrencyAdapter;

/**
 * 
 * Bean for {@link com.quantcomponents.core.model.IContract}
 */
public class ContractBean extends ContractBase implements IContract, Serializable {
	private static final long serialVersionUID = 1369624344765667928L;
	private String symbol;
	private SecurityType securityType;
	private BareDate expiryDate;
	private Double strike;
	private OptionRight optionRight;
	private Integer multiplier;
	private String exchange;
	private String primaryExchange;
	private Currency currency;
	private IdentifierType identifierType;
	private String identifier;
	private ContractDescBean contractDescription;
	private String brokerID;
	
	public static ContractBean copyOf(IContract contract) {
		ContractBean bean = new ContractBean();
		bean.setSymbol(contract.getSymbol());
		bean.setSecurityType(contract.getSecurityType());
		bean.setExpiryDate(contract.getExpiryDate());
		bean.setStrike(contract.getStrike());
		bean.setOptionRight(contract.getOptionRight());
		bean.setMultiplier(contract.getMultiplier());
		bean.setExchange(contract.getExchange());
		bean.setPrimaryExchange(contract.getPrimaryExchange());
		bean.setCurrency(contract.getCurrency());
		bean.setIdentifierType(contract.getIdentifierType());
		bean.setIdentifier(contract.getIdentifier());
		if (contract.getContractDescription() != null) {
			bean.setContractDescription(ContractDescBean.copyOf(contract.getContractDescription()));
		}
		bean.setBrokerID(contract.getBrokerID());
		return bean;
	}
	
	public static ContractBean cash(Currency currency) {
		ContractBean contract = new ContractBean();
		contract.setSymbol(currency.getCurrencyCode());
		contract.setSecurityType(SecurityType.CASH);
		contract.setCurrency(currency);
		contract.setExchange("none");
		contract.setPrimaryExchange("none");
		contract.setMultiplier(1);
		return contract;
	}
	
	@Override
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	@Override
	public SecurityType getSecurityType() {
		return securityType;
	}
	public void setSecurityType(SecurityType securityType) {
		this.securityType = securityType;
	}
	@XmlJavaTypeAdapter(XmlBareDateAdapter.class)
	@Override
	public BareDate getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(BareDate expiryDate) {
		this.expiryDate = expiryDate;
	}
	@Override
	public Double getStrike() {
		return strike;
	}
	public void setStrike(Double strike) {
		this.strike = strike;
	}
	@Override
	public OptionRight getOptionRight() {
		return optionRight;
	}
	public void setOptionRight(OptionRight optionRight) {
		this.optionRight = optionRight;
	}
	@Override
	public Integer getMultiplier() {
		return multiplier;
	}
	public void setMultiplier(Integer multiplier) {
		this.multiplier = multiplier;
	}
	@Override
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	@Override
	public String getPrimaryExchange() {
		return primaryExchange;
	}
	public void setPrimaryExchange(String primaryExchange) {
		this.primaryExchange = primaryExchange;
	}
	@XmlJavaTypeAdapter(XmlCurrencyAdapter.class)
	@Override
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	@Override
	public IdentifierType getIdentifierType() {
		return identifierType;
	}
	public void setIdentifierType(IdentifierType identifierType) {
		this.identifierType = identifierType;
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	@Override
	public ContractDescBean getContractDescription() {
		return contractDescription;
	}
	public void setContractDescription(ContractDescBean contractDescription) {
		this.contractDescription = contractDescription;
	}
	@Override
	public String getBrokerID() {
		return brokerID;
	}
	public void setBrokerID(String brokerID) {
		this.brokerID = brokerID;
	}
}
