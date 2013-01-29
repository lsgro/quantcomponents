/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ui.algo;

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

import com.quantcomponents.algo.IStockDatabaseTradingManager;
import com.quantcomponents.core.calendar.ITradingCalendarManager;
import com.quantcomponents.ui.core.IMonitorableContainer;
import com.quantcomponents.ui.core.MonitorableContainer;

/**
 * The activator class controls the plug-in life cycle
 */
public class TradingAgentPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.quantcomponent.ui.algo"; //$NON-NLS-1$
	public static final String TRADING_AGENT_MANAGER_IMAGE_KEY = "tradingAgentManager";
	public static final String TRADING_AGENT_MANAGER_IMAGE_PATH = "icons/tradingAgentManager.png";
	public static final String TRADING_AGENT_FACTORY_IMAGE_KEY = "tradingAgentFactory";
	public static final String TRADING_AGENT_FACTORY_IMAGE_PATH = "icons/tradingAgentFactory.png";
	public static final String TRADING_AGENT_IMAGE_KEY = "tradingAgent";
	public static final String TRADING_AGENT_IMAGE_PATH = "icons/tradingAgent.png";
	public static final String TRADING_AGENT_BINDING_IMAGE_KEY = "tradingAgentBinding";
	public static final String TRADING_AGENT_BINDING_IMAGE_PATH = "icons/tradingAgentBinding.png";
	public static final String TRADING_AGENT_EXECUTION_IMAGE_KEY = "tradingAgentExecution";
	public static final String TRADING_AGENT_EXECUTION_IMAGE_PATH = "icons/tradingAgentExecution.png";
	public static final String UNKNOWN_OBJECT_IMAGE_KEY = "unknownObject";
	public static final String UNKNOWN_OBJECT_IMAGE_PATH = "icons/unknownObject.png";
	public static final String DECORATOR_RUNNING_IMAGE_KEY = "decoratorRunning";
	public static final String DECORATOR_RUNNING_IMAGE_PATH = "icons/decoratorRunning.png";
	public static final String DECORATOR_PAUSED_IMAGE_KEY = "decoratorPaused";
	public static final String DECORATOR_PAUSED_IMAGE_PATH = "icons/decoratorPaused.png";

	// The shared instance
	private static TradingAgentPlugin plugin;
	
	private IStockDatabaseTradingManagerContainer tradingAgentManagerContainer;
	private volatile ITradingCalendarManager tradingCalendarManager;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		Filter filter = context.createFilter("(&(objectClass=" + IStockDatabaseTradingManager.class.getName() + ")(!(service.imported=*)))");
		tradingAgentManagerContainer = new StockDatabaseTradingManagerContainer(new MonitorableContainer<IStockDatabaseTradingManager>(), filter, context);
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
		tradingAgentManagerContainer.dispose();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TradingAgentPlugin getDefault() {
		return plugin;
	}

	public IMonitorableContainer<IStockDatabaseTradingManager>  getTradingAgentManagerContainer() {
		return tradingAgentManagerContainer;
	}
	
	public ITradingCalendarManager getTradingCalendarManager() {
		return tradingCalendarManager;
	}

	@Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        super.initializeImageRegistry(registry);
        registry.put(TRADING_AGENT_MANAGER_IMAGE_KEY, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(TRADING_AGENT_MANAGER_IMAGE_PATH), null)));
        registry.put(TRADING_AGENT_FACTORY_IMAGE_KEY, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(TRADING_AGENT_FACTORY_IMAGE_PATH), null)));
        registry.put(TRADING_AGENT_IMAGE_KEY, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(TRADING_AGENT_IMAGE_PATH), null)));
        registry.put(TRADING_AGENT_BINDING_IMAGE_KEY, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(TRADING_AGENT_BINDING_IMAGE_PATH), null)));
        registry.put(TRADING_AGENT_EXECUTION_IMAGE_KEY, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(TRADING_AGENT_EXECUTION_IMAGE_PATH), null)));
        registry.put(UNKNOWN_OBJECT_IMAGE_KEY, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(UNKNOWN_OBJECT_IMAGE_PATH), null)));
        registry.put(DECORATOR_RUNNING_IMAGE_KEY, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(DECORATOR_RUNNING_IMAGE_PATH), null)));
        registry.put(DECORATOR_PAUSED_IMAGE_KEY, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(DECORATOR_PAUSED_IMAGE_PATH), null)));
    }
	
}
