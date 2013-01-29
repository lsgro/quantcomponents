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

import java.util.TimeZone;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IConstantTranslator;
import com.quantcomponents.core.model.IdentifierType;
import com.quantcomponents.core.model.OptionRight;
import com.quantcomponents.core.model.OrderSide;
import com.quantcomponents.core.model.OrderType;
import com.quantcomponents.core.model.SecurityType;
import com.quantcomponents.core.model.UnitOfTime;
import com.quantcomponents.core.utils.LangUtils;

public class IBConstantTranslator implements IConstantTranslator {
	private static final BarSize[] BAR_SIZE_CONSTANTS = new BarSize[] { BarSize.ONE_SEC, BarSize.FIVE_SECS, BarSize.TEN_SECS, BarSize.FIFTEEN_SECS, BarSize.THIRTY_SECS, BarSize.ONE_MIN, BarSize.TWO_MINS, BarSize.THREE_MINS, BarSize.FIVE_MINS, BarSize.TEN_MINS, BarSize.FIFTEEN_MINS, BarSize.TWENTY_MINS, BarSize.THIRTY_MINS, BarSize.ONE_HOUR, BarSize.FOUR_HOURS, BarSize.ONE_DAY};
	private static final String[] BAR_SIZE_CODES = new String[] {"1 secs","5 secs","10 secs","15 secs","30 secs","1 min","2 mins","3 mins","5 mins","10 mins","15 mins","20 mins","30 mins","1 hour","4 hours","1 day"};

	private static final UnitOfTime[] TIME_UNIT_CONSTANTS = new UnitOfTime[] {UnitOfTime.SECOND,UnitOfTime.DAY,UnitOfTime.WEEK,UnitOfTime.MONTH,UnitOfTime.YEAR};
	private static final String[] TIME_UNIT_CODES = new String[] {"S","D","W","M","Y"};
	
	@Override
	public BarSize getBarSize(String code) {
		return BAR_SIZE_CONSTANTS[LangUtils.indexInArray(BAR_SIZE_CODES, code)];
	}

	@Override
	public String getCode(BarSize barSize) {
		return BAR_SIZE_CODES[LangUtils.indexInArray(BAR_SIZE_CONSTANTS, barSize)];
	}

	@Override
	public DataType getDataType(String code) {
		return DataType.valueOf(code);
	}

	@Override
	public String getCode(DataType dataType) {
		return dataType.name();
	}

	@Override
	public UnitOfTime getUnitOfTime(String code) {
		return TIME_UNIT_CONSTANTS[LangUtils.indexInArray(TIME_UNIT_CODES, code)];
	}

	@Override
	public String getCode(UnitOfTime unitOfTime) {
		return TIME_UNIT_CODES[LangUtils.indexInArray(TIME_UNIT_CONSTANTS, unitOfTime)];
	}

	@Override
	public SecurityType getSecurityType(String code) {
		return SecurityType.valueOf(code);
	}

	@Override
	public String getCode(SecurityType securityType) {
		return securityType.name();
	}

	@Override
	public OptionRight getOptionRight(String code) {
		if ("C".equals(code)) {
			return OptionRight.CALL;
		} else if ("P".equals(code)) {
			return OptionRight.PUT;
		} else
			return null;
	}

	@Override
	public String getCode(OptionRight optionRight) {
		return OptionRight.CALL.equals(optionRight) ? "C" : "P";
	}

	@Override
	public IdentifierType getIdentifierType(String code) {
		return IdentifierType.valueOf(code);
	}

	@Override
	public String getCode(IdentifierType identifierType) {
		return identifierType.name();
	}

	@Override
	public OrderSide getOrderSide(String code) {
		if ("BUY".equals(code) || "BOT".equals(code)) {
			return OrderSide.BUY;
		} else if ("SELL".equals(code) || "SSHORT".equals(code) || "SLD".equals(code)) {
			return OrderSide.SELL;
		}
		return null;
	}

	@Override
	public String getCode(OrderSide orderSide) {
		switch (orderSide) {
		case BUY:
			return "BUY";
		case SELL:
			return "SELL";
		}
		return null;
	}

	@Override
	public OrderType getOrderType(String code) {
		if ("LMT".equals(code)) {
			return OrderType.LIMIT;
		} else if ("MKT".equals(code)) {
			return OrderType.MARKET;
		} else if ("STP".equals(code)) {
			return OrderType.STOP;
		}
		return null;
	}

	@Override
	public String getCode(OrderType orderType) {
		switch (orderType) {
		case LIMIT:
			return "LMT";
		case MARKET:
			return "MKT";
		case STOP:
			return "STP";
		}
		return null;
	}

	@Override
	public TimeZone getTimeZone(String code) {
		if (code.equals("CTT")) { // 3-char IDs are not unique - CTT means China Taiwan Time - IB has been contacted about it
			code = "America/Chicago";
		}
		return TimeZone.getTimeZone(code);
	}

	@Override
	public String getCode(TimeZone timeZone) {
		throw new UnsupportedOperationException();
	}
}
