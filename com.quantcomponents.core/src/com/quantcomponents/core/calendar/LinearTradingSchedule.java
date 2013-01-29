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

import java.util.Date;

/**
 * Trivial trading schedule based on a continuous trading period each day.
 */
public class LinearTradingSchedule implements ITradingSchedule {
	@Override
	public long intervalBeetwen(Date from, Date to) {
		return to.getTime() - from.getTime();
	}

	@Override
	public Date firstTradingTime(Date from) {
		return from;
	}

	@Override
	public Date lastTradingTime(Date to) {
		return to;
	}

	@Override
	public boolean isTradingTime(Date time) {
		return true;
	}

}
