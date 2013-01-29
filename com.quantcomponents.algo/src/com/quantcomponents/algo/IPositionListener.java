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

import com.quantcomponents.core.model.IContract;

/**
 * Listener for position updates
 * @see IExecutionService
 */
public interface IPositionListener {
	/**
	 * Called when a position is updated
	 * Timings depend on the implementation of the execution service
	 * @param contract the contract of the position
	 * @param position updated position information
	 */
	void onPositionUpdate(IContract contract, IPosition position);
}
