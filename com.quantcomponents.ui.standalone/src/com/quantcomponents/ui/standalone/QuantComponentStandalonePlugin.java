/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ui.standalone;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.quantcomponents.ui.core.Configuration;

/**
 * The activator class controls the plug-in life cycle
 */
public class QuantComponentStandalonePlugin extends AbstractUIPlugin {
	private static final Logger logger = Logger.getLogger(QuantComponentStandalonePlugin.class.getName());
	// The plug-in ID
	public static final String PLUGIN_ID = "com.quantcomponents.ui.standalone"; //$NON-NLS-1$
	
	private static final String STANDALONE_BUNDLE_STARTUP_FILE = "/config/standalone_startup_bundles.properties"; //$NON-NLS-1$

	// The shared instance
	private static QuantComponentStandalonePlugin plugin;
	
	/**
	 * The constructor
	 */
	public QuantComponentStandalonePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		String frameworkConfiguration = context.getProperty(Configuration.ECLIPSE_CONFIGURATION_KEY);
		boolean standaloneConfiguration = false;
		if (frameworkConfiguration == null) {
			logger.log(Level.INFO, "No value for '" + Configuration.ECLIPSE_CONFIGURATION_KEY + "' property: starting STANDALONE configuration");
			standaloneConfiguration = true;
		} else if (frameworkConfiguration.equalsIgnoreCase(Configuration.ECLIPSE_CONFIGURATION_VALUE_STANDALONE)) {
			logger.log(Level.INFO, Configuration.ECLIPSE_CONFIGURATION_KEY + " -> " + Configuration.ECLIPSE_CONFIGURATION_VALUE_STANDALONE + ": starting STANDALONE configuration");
			standaloneConfiguration = true;
		}
		if (standaloneConfiguration) {
			Configuration.activateBundlesFromFile(context, STANDALONE_BUNDLE_STARTUP_FILE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static QuantComponentStandalonePlugin getDefault() {
		return plugin;
	}
}
