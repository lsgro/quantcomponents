/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ui.core;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class MonitorableContainerOsgiDecorator<S, T> implements IMonitorableContainer<T> {
	private static final Logger logger = Logger.getLogger(MonitorableContainerOsgiDecorator.class.getName());
	private final IMutableMonitorableContainer<S, T> innerContainer;
	private ServiceTracker<S, S> marketDataServiceTracker;

	public MonitorableContainerOsgiDecorator(IMutableMonitorableContainer<S, T> innerContainer, Filter filter, final BundleContext context) throws InvalidSyntaxException {
		this.innerContainer = innerContainer;
		marketDataServiceTracker = new ServiceTracker<S, S>(context, filter, new ServiceTrackerCustomizer<S, S>() {

			@Override
			public S addingService(ServiceReference<S> reference) {
				S service = context.getService(reference);
				logger.log(Level.INFO, "Adding service: " + service);
				MonitorableContainerOsgiDecorator.this.innerContainer.addElement(service);
				return service;
			}

			@Override
			public void modifiedService(ServiceReference<S> reference, S service) {}

			@Override
			public void removedService(ServiceReference<S> reference, S service) {
				logger.log(Level.INFO, "Removing service: " + service);
				MonitorableContainerOsgiDecorator.this.innerContainer.removeElement(service);
			}});
		marketDataServiceTracker.open();
	}

	@Override
	public Collection<T> getElements() {
		return innerContainer.getElements();
	}

	@Override
	public void addListener(IMonitorableContainerListener<T> listener) {
		innerContainer.addListener(listener);
	}

	@Override
	public void removeListener(IMonitorableContainerListener<T> listener) {
		innerContainer.removeListener(listener);
	}

	@Override
	public void dispose() {
		marketDataServiceTracker.close();
	}
}