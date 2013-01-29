/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ui.marketdata;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.quantcomponents.core.calendar.ITradingCalendarManager;
import com.quantcomponents.marketdata.IMarketDataManager;
import com.quantcomponents.ui.core.IMonitorableContainer;
import com.quantcomponents.ui.core.MonitorableContainer;

/**
 * The activator class controls the plug-in life cycle
 */
public class MarketDataPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.quantcomponent.ui.marketdata"; //$NON-NLS-1$
	public static final String MARKET_DATA_MANAGER_IMAGE_KEY = "marketDataManager";
	public static final String MARKET_DATA_MANAGER_IMAGE_PATH = "icons/marketDataManager.png";
	public static final String STOCK_DATABASE_IMAGE_KEY = "stockDatabase";
	public static final String STOCK_DATABASE_IMAGE_PATH = "icons/stockDatabase.png";
	public static final String UNKNOWN_OBJECT_IMAGE_KEY = "unknownObject";
	public static final String UNKNOWN_OBJECT_IMAGE_PATH = "icons/unknownObject.png";
	public static final String DECORATOR_AUTOUPDATE_IMAGE_KEY = "decoratorAutoupdate";
	public static final String DECORATOR_AUTOUPDATE_IMAGE_PATH = "icons/decoratorAutoupdate.png";

	// The shared instance
	private static MarketDataPlugin plugin;
	
	private IMarketDataManagerContainer marketDataManagerContainer;
	private volatile ITradingCalendarManager tradingCalendarManager;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		Filter filter = context.createFilter("(&(objectClass=" + IMarketDataManager.class.getName() + ")(!(service.imported=*)))");
		marketDataManagerContainer = new MarketDataManagerContainer(new MarketDataManagerContainerWrappingDecorator(new MonitorableContainer<MarketDataManagerPresentationWrapper>()), filter, context);
		plugin = this;
		ServiceTracker<ITradingCalendarManager, ITradingCalendarManager> calendarManagerTracker = new ServiceTracker<ITradingCalendarManager, ITradingCalendarManager>(
				context, ITradingCalendarManager.class, new ServiceTrackerCustomizer<ITradingCalendarManager, ITradingCalendarManager>() {

					@Override
					public ITradingCalendarManager addingService(ServiceReference<ITradingCalendarManager> reference) {
						if (tradingCalendarManager == null) {
							tradingCalendarManager = context.getService(reference);
						}
						return null;
					}

					@Override
					public void modifiedService(ServiceReference<ITradingCalendarManager> reference, ITradingCalendarManager service) {}

					@Override
					public void removedService(ServiceReference<ITradingCalendarManager> reference, ITradingCalendarManager service) {
						if (service == tradingCalendarManager) {
							tradingCalendarManager = null;
						}
					}
				});
		calendarManagerTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		marketDataManagerContainer.dispose();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static MarketDataPlugin getDefault() {
		return plugin;
	}

	public IMonitorableContainer<MarketDataManagerPresentationWrapper>  getMarketDataManagerContainer() {
		return marketDataManagerContainer;
	}
	
	public ITradingCalendarManager getTradingCalendarManager() {
		return tradingCalendarManager;
	}

	@Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        super.initializeImageRegistry(registry);
        registry.put(MARKET_DATA_MANAGER_IMAGE_KEY, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(MARKET_DATA_MANAGER_IMAGE_PATH), null)));
        registry.put(STOCK_DATABASE_IMAGE_KEY, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(STOCK_DATABASE_IMAGE_PATH), null)));
        registry.put(UNKNOWN_OBJECT_IMAGE_KEY, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(UNKNOWN_OBJECT_IMAGE_PATH), null)));
        registry.put(DECORATOR_AUTOUPDATE_IMAGE_KEY, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(DECORATOR_AUTOUPDATE_IMAGE_PATH), null)));
    }
	
}
