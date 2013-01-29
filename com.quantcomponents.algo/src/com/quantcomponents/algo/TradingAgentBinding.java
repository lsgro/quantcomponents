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

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Algorithm configured and bound to its inputs
 */
public class TradingAgentBinding implements Serializable {
	private static final long serialVersionUID = -3470935129104396589L;
	private final TradingAgentConfiguration configuration;
	private final Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> inputSeries;
	
	public TradingAgentBinding(TradingAgentConfiguration configuration, Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> inputSeries) {
		this.configuration = configuration;
		this.inputSeries = inputSeries;
	}

	public TradingAgentConfiguration getConfiguration() {
		return configuration;
	}

	public Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> getInputSeries() {
		return inputSeries;
	}

}
