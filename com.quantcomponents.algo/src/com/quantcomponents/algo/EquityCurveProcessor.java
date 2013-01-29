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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesAugmentable;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.model.ISeriesProcessor;
import com.quantcomponents.core.series.SimplePoint;

/**
 * Provides the equity curve for an algorithm run, based on the position data-points in the algo execution output series
 */
public class EquityCurveProcessor implements ISeriesProcessor<Date, Double>, ISeriesListener<Date, Double> {
	public static final String INPUT_SERIES_NAME = "POSITIONS";
	private final Map<IContract, IPosition> positions = new ConcurrentHashMap<IContract, IPosition>();
	private volatile ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> inputSeries;
	private volatile ISeriesAugmentable<Date, Double, ISeriesPoint<Date, Double>> outputSeries;

	@Override
	public void wire(Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> inputSeriesMap, ISeriesAugmentable<Date, Double, ISeriesPoint<Date, Double>> outputSeries) {
		this.inputSeries = inputSeriesMap.get(INPUT_SERIES_NAME);
		this.outputSeries = outputSeries;
		if (!inputSeries.isEmpty()) {
			for (ISeriesPoint<Date, Double> point : this.inputSeries) {
				onItemAdded(point);
			}
		}
		this.inputSeries.addSeriesListener(this);
	}

	@Override
	public void unwire() {
		if (inputSeries != null) {
			inputSeries.removeSeriesListener(this);
			inputSeries = null;
		}
		outputSeries = null;
	}

	private void onPositionUpdate(IContract contract, IPosition position) {
		positions.put(contract, position);
		double pnl = calculatePnl();
		SimplePoint point = new SimplePoint(position.getTimestamp(), pnl);
		if (outputSeries != null) {
			outputSeries.insertFromTail(point);
		}
	}
	
	private double calculatePnl() {
		double pnl = 0.0;
		for (IPosition position : positions.values()) {
			pnl += position.getUnrealizedPnl();
			pnl += position.getRealizedPnl();
		}
		return pnl;
	}

	@Override
	public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) { } 
	
	@Override
	public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
		if (newItem instanceof IPositionPoint) {
			IPositionPoint positionPoint = (IPositionPoint) newItem;
			onPositionUpdate(positionPoint.getContract(), positionPoint.getPosition());
		}
	}
}
