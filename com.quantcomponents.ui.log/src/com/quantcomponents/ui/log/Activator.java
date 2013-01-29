package com.quantcomponents.ui.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.eclipse.core.runtime.ILog;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {
	// The shared instance
	private static Activator plugin;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		ILog eclipseLogger = this.getLog();
		EclipseLogAdapterHandler eclipseLogAdapterHandler = new EclipseLogAdapterHandler(eclipseLogger);
		Logger rootLogger = Logger.getLogger("");
		rootLogger.addHandler(eclipseLogAdapterHandler);
		Handler[] handlers = rootLogger.getHandlers();
		ConsoleHandler consoleHandler = null;
		for (Handler handler : handlers) {
			if (handler instanceof ConsoleHandler) {
				consoleHandler = (ConsoleHandler) handler;
			}
		}
		rootLogger.removeHandler(consoleHandler);
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
	public static Activator getDefault() {
		return plugin;
	}
}
