package com.quantcomponents.ui.standalone;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.IStartup;

/* the real work is done by the bundle activator,
 * this class purpose is to trigger lazy activation
 * upon loading by the Eclipse framework, caused by
 * the org.eclipse.ui.startup extension declared by
 * this plugin */
public class QuantComponentsServiceStartup implements IStartup {
	private static final Logger logger = Logger.getLogger(QuantComponentsServiceStartup.class.getName());
	@Override
	public void earlyStartup() {
		logger.log(Level.INFO, "Auto-starting QuantComponents framework");
	}

}
