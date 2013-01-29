/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo;

import com.quantcomponents.core.model.IPrettyNamed;

/**
 * Specialized trading manager that only deals with algorithm operating on {@link com.quantcomponents.marketdata.IStockDatabase}
 * @see ITradingManager
 */
public interface IStockDatabaseTradingManager extends ITradingHierarchyManager, ITradingFactoryManager, ITradingAgentConfigurationManager, IStockDatabaseTradingAgentBindingManager, ITradingAgentExecutionManager, IPrettyNamed {
}
