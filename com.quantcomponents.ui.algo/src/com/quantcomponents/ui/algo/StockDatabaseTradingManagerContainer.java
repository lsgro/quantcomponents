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

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

import com.quantcomponents.algo.IStockDatabaseTradingManager;
import com.quantcomponents.ui.core.IMutableMonitorableContainer;
import com.quantcomponents.ui.core.MonitorableContainerOsgiDecorator;

public class StockDatabaseTradingManagerContainer extends MonitorableContainerOsgiDecorator<IStockDatabaseTradingManager, IStockDatabaseTradingManager> implements IStockDatabaseTradingManagerContainer {

	public StockDatabaseTradingManagerContainer(IMutableMonitorableContainer<IStockDatabaseTradingManager, IStockDatabaseTradingManager> innerContainer, Filter filter, BundleContext context) throws InvalidSyntaxException {
		super(innerContainer, filter, context);
	}

}
