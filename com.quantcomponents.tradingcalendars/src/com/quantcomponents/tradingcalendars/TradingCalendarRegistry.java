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

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.quantcomponents.core.calendar.FlatCalendar;
import com.quantcomponents.core.calendar.ITradingCalendar;
import com.quantcomponents.core.calendar.ITradingCalendarManager;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.tradingcalendars.TradingCalendarFactoryBean.SpecificTradingDay;
import com.quantcomponents.tradingcalendars.TradingCalendarFactoryBean.WeekTradingDay;

public class TradingCalendarRegistry implements ITradingCalendarManager, BundleListener {
	private static final ITradingCalendar FLAT_CALENDAR = new FlatCalendar();
	private final Map<String, Set<URL>> urlByBundleLocation = new HashMap<String, Set<URL>>();
	private final Map<URL, ITradingCalendar> calendars = new ConcurrentHashMap<URL, ITradingCalendar>();

	@Override
	public List<ITradingCalendar> findTradingCalendars(IContract contract, Boolean includeAfterHours) {
		List<ITradingCalendar> result = new LinkedList<ITradingCalendar>();
		String exchange = contract.getPrimaryExchange();
		if (exchange == null) {
			exchange = contract.getExchange();
		}
		for (ITradingCalendar calendar : calendars.values()) {
			if (calendar.getExchanges().length > 0 && exchange != null) {
				if (Arrays.binarySearch(calendar.getExchanges(), exchange) < 0) {
					continue;
				}
			}
			if (includeAfterHours != null && calendar.isIncludeAfterHours() != null && calendar.isIncludeAfterHours() != includeAfterHours) {
				continue;
			}
			result.add(calendar);
		}
		result.add(FLAT_CALENDAR);
		return result;
	}

	@Override
	public ITradingCalendar tradingCalendarByName(String name) {
		for (ITradingCalendar calendar : calendars.values()) {
			if (calendar.getName().equals(name)) {
				return calendar;
			}
		}
		return null;
	}

	@Override
	public void bundleChanged(BundleEvent event) {
		if (event.getType() == BundleEvent.INSTALLED) {
			addBundleCalendars(event.getBundle());
		} else if (event.getType() == BundleEvent.UNINSTALLED) {
			synchronized (urlByBundleLocation) {
				String bundleLocation = event.getBundle().getLocation();
				Set<URL> bundleUrlSet = urlByBundleLocation.get(bundleLocation);
				if (bundleUrlSet != null) {
					for (URL url : bundleUrlSet) {
						calendars.remove(url);
					}
					urlByBundleLocation.remove(bundleLocation);
				}
			}
		}
	}
	
	void addBundleCalendars(Bundle bundle) {
		Enumeration<URL> urls = findCalendarData(bundle);
		while (urls != null && urls.hasMoreElements()) {
			URL url = urls.nextElement();
			try {
				TradingCalendarFactoryBean factory = createFactoryFromCalendarData(url);
				calendars.put(url, factory.createTradingCalendar());
				synchronized (urlByBundleLocation) {
					Set<URL> bundleUrlSet = urlByBundleLocation.get(bundle.getLocation());
					if (bundleUrlSet == null) {
						bundleUrlSet = new HashSet<URL>();
						urlByBundleLocation.put(bundle.getLocation(), bundleUrlSet);
					}
					bundleUrlSet.add(url);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private TradingCalendarFactoryBean createFactoryFromCalendarData(URL calendarData) throws JAXBException, IOException {
		TradingCalendarFactoryBean result = null;
		JAXBContext context = JAXBContext.newInstance(TradingCalendarFactoryBean.class, WeekTradingDay.class, SpecificTradingDay.class);
		Unmarshaller m = context.createUnmarshaller();
		Object o = null;
		o = m.unmarshal(calendarData.openStream());
		result = (TradingCalendarFactoryBean) o;
		return result;
	}

	private Enumeration<URL> findCalendarData(Bundle bundle) {
		return bundle.findEntries("/tradingCalendars", "*.xml", false);
	}
}
