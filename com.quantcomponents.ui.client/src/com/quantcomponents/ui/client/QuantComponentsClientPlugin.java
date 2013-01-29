package com.quantcomponents.ui.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.quantcomponents.ui.core.Configuration;

/**
 * The activator class controls the plug-in life cycle
 */
public class QuantComponentsClientPlugin extends AbstractUIPlugin {
	private static final Logger logger = Logger.getLogger(QuantComponentsClientPlugin.class.getName());

	// The plug-in ID
	public static final String PLUGIN_ID = "com.quantcomponents.ui.client"; //$NON-NLS-1$

	private static final String CLIENT_BUNDLE_STARTUP_FILE = "/config/client_startup_bundles.properties"; //$NON-NLS-1$
	
	// The shared instance
	private static QuantComponentsClientPlugin plugin;
	
	/**
	 * The constructor
	 */
	public QuantComponentsClientPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		String frameworkConfiguration = context.getProperty(Configuration.ECLIPSE_CONFIGURATION_KEY);
		if (frameworkConfiguration != null && frameworkConfiguration.equalsIgnoreCase(Configuration.ECLIPSE_CONFIGURATION_VALUE_CLIENT)) {
			logger.log(Level.INFO, Configuration.ECLIPSE_CONFIGURATION_KEY + " -> " + Configuration.ECLIPSE_CONFIGURATION_VALUE_STANDALONE + ": starting CLIENT configuration");
			Configuration.activateBundlesFromFile(context, CLIENT_BUNDLE_STARTUP_FILE);
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
	public static QuantComponentsClientPlugin getDefault() {
		return plugin;
	}

}
