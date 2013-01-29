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

import com.quantcomponents.core.model.IContract;

/**
 * Abstract class to be used as base for every implementation of {@link com.quantcomponents.core.model.IContract}
 * This class defines important methods than ensure that a contract definition can be used as key and
 * it can be consistently represented within the framework
 */
public abstract class ContractBase implements IContract {

	@Override
	public int hashCode() {
		return hashCode(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IContract)) {
			return false;
		}
		return equals(this, (IContract) o);
	}
	
	@Override
	public String toString() {
		return stringRepr(this);
	}
	
	/**
	 * Static helper method to build the hash code of an IContract
	 * @param contract an {@link com.quantcomponents.core.model.IContract}
	 * @return the hash code consistent with the other implementors
	 */
	public static int hashCode(IContract contract) {
		return contract.getSymbol().hashCode();
	}

	/**
	 * Static helper method to check the equality with an IContract
	 * @param contract1 an {@link com.quantcomponents.core.model.IContract}
	 * @param contract2 another {@link com.quantcomponents.core.model.IContract}
	 * @return true if the objects represent the same contract, based on
	 * the contract properties, false otherwise
	 * @see ContractBase#shortStringRepr
	 */
	public static boolean equals(IContract contract1, IContract contract2) {
		return shortStringRepr(contract1).equals(shortStringRepr(contract2));
	}
	
	/**
	 * Static helper method to create a unique readable representation of the
	 * contract. 
	 * @param contract an {@link com.quantcomponents.core.model.IContract} 
	 * @return a String representing the contract
	 */
	public static String stringRepr(IContract contract) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("contract [");
		buffer.append(shortStringRepr(contract));
		buffer.append("]");
		return buffer.toString();
	}
	
	/**
	 * Static helper method to create a unique, readable representation.
	 * The mandatory properties entering this representation are:
	 * <ul>
	 * <li>symbol</li>
	 * <li>security type</li>
	 * <li>currency code</li>
	 * </ul>
	 * The optional properties are used only if not null:
	 * <ul>
	 * <li>option right</li>
	 * <li>expiration date</li>
	 * <li>strike</li>
	 * <li>primary exchange</li>
	 * </ul>
	 * Since this representation strive to use all and only the properties
	 * that identify the contract, this is used for equality tests.
	 * 
	 * @param contract an {@link com.quantcomponents.core.model.IContract} 
	 * @return a readable representation of the input parameter
	 */
	public static String shortStringRepr(IContract contract) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(contract.getSymbol());
		buffer.append("; ").append(contract.getSecurityType().name());
		buffer.append("; ").append(contract.getCurrency().getCurrencyCode());
		if (contract.getOptionRight() != null)
			buffer.append("; ").append(contract.getOptionRight().name());
		if (contract.getExpiryDate() != null)
			buffer.append("; ").append(contract.getExpiryDate());
		if (contract.getStrike() != null)
			buffer.append("; ").append(contract.getStrike());
		if (contract.getPrimaryExchange() != null)
			buffer.append("; ").append(contract.getPrimaryExchange());
		return buffer.toString();
	}
}
