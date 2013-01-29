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

import java.util.Collection;

/**
 * Interface to deal with {@link ITradingAgentFactory} instances.
 * Since the trading agent factories are exposed as services, they are created and destroyed by system configuration.
 * Therefore this interface provides read-only methods.
 *
 */
public interface ITradingFactoryManager {
	/**
	 * Returns all the currently available factories
	 */
	Collection<TradingAgentFactoryHandle> getAllTradingAgentFactories();
}
