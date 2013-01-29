/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.chart;


/**
 * Base implementation class for {@link IAxis}
 *
 * @param <T> type of axis values
 */
public abstract class BaseAxis<T extends Comparable<T>> implements IAxis<T> {
	private volatile T rangeLow;
	private volatile T rangeHigh;
	private volatile int pixelLow;
	private volatile int pixelHigh;

	public int getPixelLow() {
		return pixelLow;
	}

	public int getPixelHigh() {
		return pixelHigh;
	}
	
	@Override
	public T getRangeLow() {
		return rangeLow;
	}

	@Override
	public T getRangeHigh() {
		return rangeHigh;
	}

	public void setPixelLow(int pixelLow) {
		this.pixelLow = pixelLow;
	}

	public void setPixelHigh(int pixelHigh) {
		this.pixelHigh = pixelHigh;
	}

	public void setRangeLow(T rangeLow) {
		this.rangeLow = rangeLow;
	}

	public void setRangeHigh(T rangeHigh) {
		this.rangeHigh = rangeHigh;
	}

}
