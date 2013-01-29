/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.calendar;

/**
 * 
 * Specification of a trading day, in terms of uninterrupted trading periods
 */
public interface ITradingDay {
	/**
	 * @return zero or more trading periods for this day
	 */
	ITradingPeriod[] getTradingPeriods();
}
