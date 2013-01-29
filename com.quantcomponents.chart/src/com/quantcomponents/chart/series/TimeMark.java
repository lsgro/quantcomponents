/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.chart.series;

import java.util.Date;

import com.quantcomponents.chart.IMarkScale;
import com.quantcomponents.chart.IMark;

/**
 * 
 * Chart ruler mark for {@link java.util.Date} values
 */
public class TimeMark implements IMark<Date> {
	private final IMarkScale<Date> markScale;
	private final Date value;

	public TimeMark(IMarkScale<Date> scale, Date value) {
		this.markScale = scale;
		this.value = value;
	}

	@Override
	public IMarkScale<Date> getScale() {
		return markScale;
	}

	@Override
	public Date getValue() {
		return value;
	}

}
