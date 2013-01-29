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

import java.util.Date;

import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Interface to embed trade information within a {@link com.quantcomponents.core.model.ISeries}
 */
public interface ITradePoint extends ISeriesPoint<Date, Double> {
	/**
	 * Trade information
	 */
	ITrade getTrade();
}
