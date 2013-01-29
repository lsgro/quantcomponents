/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.tradingcalendars;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import com.quantcomponents.core.calendar.ITradingDay;
import com.quantcomponents.core.model.beans.TradingPeriodBean;

public class TradingDayBean implements ITradingDay, Serializable {
	private static final long serialVersionUID = 9028008826822468972L;
	private TradingPeriodBean[] tradingPeriods;
	
	@Override
	public TradingPeriodBean[] getTradingPeriods() {
		return tradingPeriods;
	}

	@XmlElement(name = "tradingPeriod")
	public void setTradingPeriods(TradingPeriodBean[] tradingPeriods) {
		this.tradingPeriods = tradingPeriods;
	}

}
