/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.osgi;

import com.quantcomponents.algo.ITradingAgentConfigurationManager;
import com.quantcomponents.algo.ITradingFactoryManager;
import com.quantcomponents.algo.ITradingHierarchyManager;
import com.quantcomponents.core.model.IPrettyNamed;
import com.quantcomponents.core.remote.IForceImport_com_quantcomponents_core_remote;

public interface IStockDatabaseTradingManagerHost extends ITradingHierarchyManager, ITradingFactoryManager, ITradingAgentConfigurationManager, IStockDatabaseTradingAgentBindingManagerHost, ITradingAgentExecutionManagerHost, IPrettyNamed, IForceImport_com_quantcomponents_core_remote {
}
