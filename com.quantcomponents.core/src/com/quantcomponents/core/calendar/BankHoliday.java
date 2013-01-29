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
 * Utility class for emtpy trading days
 */
public class BankHoliday implements ITradingDay {
	private static final ITradingPeriod[] EMPTY_ARRAY = new ITradingPeriod[0];
	
	@Override
	public ITradingPeriod[] getTradingPeriods() {
		return EMPTY_ARRAY;
	}

}
