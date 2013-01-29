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

import java.util.Map;

import com.quantcomponents.core.model.IContract;

/**
 * A service capable of delivering position information.
 * Typical implementors are execution services.
 */
public interface IPositionProvider {
	/**
	 * A snapshot map of the current positions within the specific execution service
	 */
	Map<IContract, IPosition> getPositions();
}
