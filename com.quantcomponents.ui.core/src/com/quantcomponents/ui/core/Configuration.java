package com.quantcomponents.ui.core;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

/**
 * Constants for Eclipse configuration of the framework
 * @see com.quantcomponents.ui.standalone.QuantComponentStandalonePlugin
 */
public class Configuration {
	private static final Logger logger = Logger.getLogger(Configuration.class.getName());
	/**
	 * Property to set the Eclipse configuration of the QuantComponents framework
	 */
	public static final String ECLIPSE_CONFIGURATION_KEY = "com.quantcomponents.ui.configuration";
	/**
	 * All functionality from an Eclipse instance - this is the default value
	 */
	public static final String ECLIPSE_CONFIGURATION_VALUE_STANDALONE = "standalone";
	/**
	 * Client only in Eclipse - it needs a server running - to be setup with -Dcom.quantcomponents.ui.configuration=client
	 */
	public static final String ECLIPSE_CONFIGURATION_VALUE_CLIENT = "client";
	
	private static Map<String, Version> readBundleActivationFile(Bundle bundle, String bundleActivationFileName) throws IOException {
		Map<String, Version> bundleMap = new HashMap<String, Version>();
		URL bundleFileUrl = bundle.getResource(bundleActivationFileName);
		Properties bundleProperties = new Properties();
		bundleProperties.load(bundleFileUrl.openStream());
		for (Map.Entry<Object, Object> entry : bundleProperties.entrySet()) {
			String symbolicName = (String) entry.getKey();
			Version version = new Version((String) entry.getValue());
			bundleMap.put(symbolicName, version);
		}
		return bundleMap;
	}

	public static void activateBundlesFromFile(BundleContext context, String bundleStartupFileName) throws IOException, BundleException {
		Map<String, Version> bundleMap = readBundleActivationFile(context.getBundle(), bundleStartupFileName);
		Map<String, Bundle> startupBundleMap = new HashMap<String, Bundle>();
		for (Bundle b : context.getBundles()) {
			String symbolicName = b.getSymbolicName();
			if (bundleMap.containsKey(symbolicName)) {
				Version version = b.getVersion();
				Version reqVersion = bundleMap.get(symbolicName);
				if (version.getMajor() == reqVersion.getMajor() && version.getMinor() >= reqVersion.getMinor()) {
					if (startupBundleMap.containsKey(symbolicName)) {
						Bundle previousBundle = startupBundleMap.get(symbolicName);
						if (version.compareTo(previousBundle.getVersion()) <= 0) {
							break;
						}
					}
					startupBundleMap.put(symbolicName, b);
				}
			}
		}
		for (Bundle startupBundle : startupBundleMap.values()) {
			logger.log(Level.INFO, "Starting bundle: " + startupBundle);
			startupBundle.start();
		}
	}

}
