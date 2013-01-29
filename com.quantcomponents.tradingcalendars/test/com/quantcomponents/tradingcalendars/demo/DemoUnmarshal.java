/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.tradingcalendars.demo;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.quantcomponents.tradingcalendars.TradingCalendarFactoryBean;

public class DemoUnmarshal {

	/**
	 * @param args
	 * @throws JAXBException 
	 */
	public static void main(String[] args) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(TradingCalendarFactoryBean.class);
		Unmarshaller m = context.createUnmarshaller();
		TradingCalendarFactoryBean bean = (TradingCalendarFactoryBean) m.unmarshal(new File("sample/nasdaq.xml"));
		System.out.println(bean);
	}

}
