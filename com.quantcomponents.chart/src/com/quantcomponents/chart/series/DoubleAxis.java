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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.quantcomponents.chart.BaseAxis;
import com.quantcomponents.chart.IAxis;
import com.quantcomponents.chart.IMark;
import com.quantcomponents.chart.IMarkScale;
import com.quantcomponents.chart.IMarkScaleSelector;

/**
 * 
 * Axis for double values.
 * Used as Y axis in charts
 *
 */
public class DoubleAxis extends BaseAxis<Double> implements IAxis<Double> {
	private final boolean reverse;
	private final List<IMark<Double>> baseMarks;
	private final List<IMark<Double>> parentMarks;
	private final IMarkScale<Double> baseMarkScale;

	public DoubleAxis(boolean reverse, IMarkScaleSelector<Double> scaleSelector, Double rangeLow, Double rangeHigh, int pixelLow, int pixelHigh, int maxMarkNumber) {
		this.reverse = reverse;
		setRangeLow(rangeLow);
		setRangeHigh(rangeHigh);
		setPixelLow(pixelLow);
		setPixelHigh(pixelHigh);
		baseMarkScale = scaleSelector.markScale(getRangeHigh() - getRangeLow(), maxMarkNumber);
		baseMarks = new LinkedList<IMark<Double>>();
		for (IMark<Double> mark = baseMarkScale.followingMark(getRangeLow()); mark != null && mark.getValue() < getRangeHigh(); mark = baseMarkScale.followingMark(mark.getValue())) {
			baseMarks.add(mark);
		}
		parentMarks = new LinkedList<IMark<Double>>();
		IMarkScale<Double> parentMarkScale = baseMarkScale.parent();
		for (IMark<Double> mark = parentMarkScale.followingMark(getRangeLow()); mark != null && mark.getValue() < getRangeHigh(); mark = parentMarkScale.followingMark(mark.getValue())) {
			parentMarks.add(mark);
		}
	}

	@Override
	public int calculatePixel(Double value) {
		int pixelDisplacement = (int) ((value - getRangeLow()) / (getRangeHigh() - getRangeLow()) * (getPixelHigh() - getPixelLow()));
		return reverse ? getPixelHigh() - pixelDisplacement : getPixelLow() + pixelDisplacement;
	}

	@Override
	public boolean isValid(Double abscissa) {
		return true;
	}

	@Override
	public List<IMark<Double>> baseMarks() {
		return Collections.unmodifiableList(baseMarks);
	}

	@Override
	public List<IMark<Double>> parentMarks() {
		return Collections.unmodifiableList(parentMarks);
	}

	@Override
	public IMarkScale<Double> baseMarkScale() {
		return baseMarkScale;
	}

	@Override
	public int getPointSize() {
		return 0;
	}
}
