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

import java.util.TimeZone;

/**
 * Implementors are used to translate from API objects to String
 * codes to be used with a specific broker.
 *
 */
public interface IConstantTranslator {
	
	BarSize getBarSize(String code);
	String getCode(BarSize barSize);
	
	DataType getDataType(String code);
	String getCode(DataType dataType);
	
	IdentifierType getIdentifierType(String code);
	String getCode(IdentifierType identifierType);
	
	OptionRight getOptionRight(String code);
	String getCode(OptionRight optionRight);
	
	SecurityType getSecurityType(String code);
	String getCode(SecurityType securityType);
	
	UnitOfTime getUnitOfTime(String code);
	String getCode(UnitOfTime unitOfTime);
	
	OrderSide getOrderSide(String code);
	String getCode(OrderSide orderSide);
	
	OrderType getOrderType(String code);
	String getCode(OrderType orderType);
	
	TimeZone getTimeZone(String code);
	String getCode(TimeZone timeZone);
}
