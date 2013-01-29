/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.model;

import java.util.Currency;

import com.quantcomponents.core.model.beans.ContractBase;

/**
 * Financial contract specification. 
 * This interface is used as key in many places within the framework.
 * Implementors should inherit from {@link ContractBase}
 * or use equals/hashCode static methods provided by that class
 * to make sure that the equality contract is consistent with the
 * original implementations. 
 * @see ContractBase
 * @see com.quantcomponents.core.model.beans.ContractBean
 */
public interface IContract {
	/**
	 * The contract ticker (e.g. "AAPL")
	 */
	String getSymbol();
	/**
	 * The type of security
	 */
	SecurityType getSecurityType();
	/**
	 * This is the expiration date in case of SecurityType.OPT or SecurityType.FUT
	 * @return A date or null if not relevant
	 */
	BareDate getExpiryDate();
	/**
	 * The option strike, in case of SecurityType.OPT
	 * @return An option strike or null if not relevant
	 */
	Double getStrike();
	/**
	 * The option right type: CALL, PUT in case of SecurityType.OPT
	 * @return The option right or null if not relevant
	 */
	OptionRight getOptionRight();
	/**
	 * The multiplier for derivatives
	 * @return A multiplier or null if not relevant
	 */
	Integer getMultiplier();
	/**
	 * The default exchange for the contract. This does not correspond, in most cases, to the
	 * most specific exchange, e.g. the venue originating the contract
	 * @return The name of the default exchange for the contract
	 */
	String getExchange();
	/**
	 * The primary exchange for the contract. Although it could not be the preferential venue
	 * to trade this contract, it might be the most specific venue, or the originating one.
	 * This value is used to build the contract identity, when used as key.
	 * @return The name of the primary exchange for this contract
	 */
	String getPrimaryExchange();
	/**
	 * The trading currency
	 * @return A {@link java.util.Currency} object
	 */
	Currency getCurrency();
	/**
	 * Some contract specification include an identifier, e.g. ISIN or CUSIP
	 * @return The contract identifier type if getIdentifier() is not null, null otherwise
	 */
	IdentifierType getIdentifierType();
	/**
	 * The contract identifier, if present
	 * @return A String representing the contract in a specific scheme, or null
	 */
	String getIdentifier();
	/**
	 * Longer description of the contract
	 * @return an {@link IContractDesc} or null
	 */
	IContractDesc getContractDescription();
	/**
	 * A broker-specific ID for the contrat
	 * @return A broker-specific ID or null
	 */
	String getBrokerID();
	/**
	 * This method must satisfy the contract for this inteface, as implemented in {@link com.quantcomponents.core.model.beans.ContractBase}
	 * @see com.quantcomponents.core.model.beans.ContractBase
	 */
	boolean equals(Object o);
	/**
	 * This method must satisfy the contract for this inteface, as implemented in {@link com.quantcomponents.core.model.beans.ContractBase}
	 * @see com.quantcomponents.core.model.beans.ContractBase
	 */
	int hashCode();
}
