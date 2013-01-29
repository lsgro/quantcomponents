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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quantcomponents.core.calendar.ITradingCalendarManager;

public class Activator implements BundleActivator {
	private static Activator plugin;
	public static final String PLUGIN_ID = "com.quantcomponents.tradingcalendars";
	
	private TradingCalendarRegistry manager;

	public static Activator getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		manager = new TradingCalendarRegistry();
		for (Bundle bundle : context.getBundles()) {
			manager.addBundleCalendars(bundle);
		}
		context.addBundleListener(manager);
		context.registerService(ITradingCalendarManager.class, manager, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
	}
}
