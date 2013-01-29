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
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.quantcomponents.core.model.BareDate;
import com.quantcomponents.core.model.beans.TradingPeriodBean;
import com.quantcomponents.tradingcalendars.TradingCalendarFactoryBean;
import com.quantcomponents.tradingcalendars.TradingCalendarFactoryBean.SpecificTradingDay;
import com.quantcomponents.tradingcalendars.TradingCalendarFactoryBean.WeekTradingDay;
import com.quantcomponents.tradingcalendars.TradingDayBean;

public class DemoMarshal {

	/**
	 * @param args
	 * @throws JAXBException 
	 */
	public static void main(String[] args) throws JAXBException {
		Calendar cal = Calendar.getInstance();
		TimeZone timeZone = TimeZone.getDefault();
		cal.setTimeZone(timeZone);
		TradingCalendarFactoryBean bean = new TradingCalendarFactoryBean();
		bean.setName("Example!");
		bean.setDescription("An example bean");
		bean.setTimeZoneId(timeZone.getID());
		cal.set(2000, 11, 31, 23, 0, 0);
		bean.setStartDate(cal.getTime());
		cal.set(2100, 11, 31, 0, 0, 0);
		bean.setEndDate(cal.getTime());
		Set<BareDate> closingDays = new HashSet<BareDate>();
		closingDays.add(new BareDate(2012, 12, 25));
		closingDays.add(new BareDate(2012, 12, 31));
		closingDays.add(new BareDate(2011, 1, 1));
		bean.setClosingDays(closingDays);
		Set<WeekTradingDay> weekTradingDays = new HashSet<WeekTradingDay>();
		TradingPeriodBean tpBeanUnique = new TradingPeriodBean();
		tpBeanUnique.setStartHour(10);
		tpBeanUnique.setStartMinute(0);
		tpBeanUnique.setEndHour(22);
		tpBeanUnique.setEndMinute(30);
		TradingPeriodBean tpBeanMorning = new TradingPeriodBean();
		tpBeanMorning.setStartHour(10);
		tpBeanMorning.setStartMinute(0);
		tpBeanMorning.setEndHour(13);
		tpBeanMorning.setEndMinute(30);
		TradingPeriodBean tpBeanAfternoon = new TradingPeriodBean();
		tpBeanAfternoon.setStartHour(14);
		tpBeanAfternoon.setStartMinute(0);
		tpBeanAfternoon.setEndHour(23);
		tpBeanAfternoon.setEndMinute(30);
		TradingDayBean tdBean1 = new TradingDayBean();
		tdBean1.setTradingPeriods(new TradingPeriodBean[] { tpBeanUnique });
		TradingDayBean tdBean2 = new TradingDayBean();
		tdBean2.setTradingPeriods(new TradingPeriodBean[] { tpBeanMorning, tpBeanAfternoon });
		weekTradingDays.add(new WeekTradingDay(Calendar.MONDAY, tdBean1));
		weekTradingDays.add(new WeekTradingDay(Calendar.TUESDAY, tdBean1));
		weekTradingDays.add(new WeekTradingDay(Calendar.WEDNESDAY, tdBean2));
		weekTradingDays.add(new WeekTradingDay(Calendar.THURSDAY, tdBean1));
		weekTradingDays.add(new WeekTradingDay(Calendar.FRIDAY, tdBean1));
		bean.setWeekTradingDays(weekTradingDays);
		Set<SpecificTradingDay> specialTradingDays = new HashSet<SpecificTradingDay>();
		TradingDayBean stdBean1 = new TradingDayBean();
		stdBean1.setTradingPeriods(new TradingPeriodBean[] { tpBeanMorning });
		specialTradingDays.add(new SpecificTradingDay("20121224", stdBean1));
		bean.setSpecialTradingDays(specialTradingDays);
		bean.setIncludeAfterHours(true);
		bean.setExchanges(new String[] { "GLOBEX", "CBOT" } );
		
		JAXBContext context = JAXBContext.newInstance(TradingCalendarFactoryBean.class, WeekTradingDay.class, SpecificTradingDay.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(bean, new File("sample/output.xml"));
	}

}
