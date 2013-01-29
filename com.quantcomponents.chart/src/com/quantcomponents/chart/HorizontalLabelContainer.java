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

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.quantcomponents.chart.HorizontalLabelContainer.LabelInfo;

/**
 * 
 * Container of axis labels.
 * This class collects all the labels that are to be displayed on an horizontal chart axis, 
 * making sure that no label overlaps another, taking into account the label width in pixel.
 * Overloading labels are removed.
 *
 * @param <T> the type of the axis values
 */
public class HorizontalLabelContainer<T> implements Iterable<LabelInfo<T>> {
	private static final int DEFAULT_LABEL_MARGIN = 5;
	private int labelMargin = DEFAULT_LABEL_MARGIN;

	/**
	 * All the information needed to render the label on a chart
	 *
	 * @param <T> type of the axis values
	 */
	public static class LabelInfo<T> {
		public String label;
		public int leftPixel;
		public int centerPixel;
		public int rightPixel;
		public IMarkScale<T> markScale;
	}
	private NavigableMap<Date, LabelInfo<T>> labels = new TreeMap<Date, LabelInfo<T>>();
	
	public int getLabelMargin() {
		return labelMargin;
	}

	public void setLabelMargin(int labelMargin) {
		this.labelMargin = labelMargin;
	}

	public void addLabel(Date date, String label, int leftPixel, int centerPixel, int rightPixel, IMarkScale<T> markScale) {
		LabelInfo<T> info = new LabelInfo<T>();
		info.label = label;
		info.leftPixel = leftPixel;
		info.centerPixel = centerPixel;
		info.rightPixel = rightPixel;
		info.markScale = markScale;
		labels.put(date, info);
		Date previousKey = labels.lowerKey(date);
		if (previousKey != null) {
			LabelInfo<T> previousInfo = labels.get(previousKey);
			if (previousInfo.rightPixel > info.leftPixel - labelMargin) {
				labels.remove(previousKey);
			}
		}
		Date nextKey = labels.higherKey(date);
		if (nextKey != null) {
			LabelInfo<T> nextInfo = labels.get(nextKey);
			if (nextInfo.leftPixel < info.rightPixel + labelMargin) {
				labels.remove(nextKey);
			}
		}
	}

	@Override
	public Iterator<LabelInfo<T>> iterator() {
		return Collections.unmodifiableCollection(labels.values()).iterator();
	}
}
