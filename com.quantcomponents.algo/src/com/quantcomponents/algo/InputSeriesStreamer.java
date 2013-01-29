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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.quantcomponents.core.model.IMutableSeries;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.series.LinkedListSeries;

/**
 * Simple synchronous input series streamer
 */
public class InputSeriesStreamer implements IInputSeriesStreamer {
	private final Map<String, IMutableSeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> streamingSeries = new ConcurrentHashMap<String, IMutableSeries<Date, Double, ? extends ISeriesPoint<Date, Double>>>();
	private final Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> sourceSeries;
	private volatile Date lastTimestamp;
	private Map<String, Iterator<ISeriesPoint<Date, Double>>> iteratorMap;
	private Map<String, ISeriesPoint<Date, Double>> lastValueMap;

	public InputSeriesStreamer(Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> sourceSeries, Map<String, ? extends IMutableSeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> targetSeries) {
		this.sourceSeries = sourceSeries;
		for (String key : sourceSeries.keySet()) {
			streamingSeries.put(key, targetSeries.get(key));
		}
	}

	@Override
	public Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> getStreamingSeries() {
		return Collections.unmodifiableMap(streamingSeries);
	}

	@Override
	public Date getLastTimestamp() {
		return lastTimestamp;
	}

	private void fetchNextPoints() {
		for (Map.Entry<String, ISeriesPoint<Date, Double>> lastValueEntry : lastValueMap.entrySet()) {
			if (lastValueEntry.getValue() == null) {
				Iterator<ISeriesPoint<Date, Double>> pointIterator = iteratorMap.get(lastValueEntry.getKey());
				if (pointIterator.hasNext()) {
					lastValueEntry.setValue(pointIterator.next());
				}
			}
		}
	}
	
	private Map.Entry<String, ISeriesPoint<Date, Double>> findFirstPoint() {
		Map.Entry<String, ISeriesPoint<Date, Double>> firstPointEntry = null;
		for (Map.Entry<String, ISeriesPoint<Date, Double>> lastValueEntry : lastValueMap.entrySet()) {
			if (firstPointEntry == null || (lastValueEntry.getValue() != null && lastValueEntry.getValue().getEndIndex().before(firstPointEntry.getValue().getEndIndex()))) {
				firstPointEntry = lastValueEntry;
			}
		}
		if (firstPointEntry != null && firstPointEntry.getValue() != null) {
			lastTimestamp = firstPointEntry.getValue().getEndIndex();
		}
		return firstPointEntry;
	}
	
	private void addToStreamingSeries(Map.Entry<String, ISeriesPoint<Date, Double>> pointEntry) {
		@SuppressWarnings("unchecked")
		LinkedListSeries<Date, Double, ISeriesPoint<Date, Double>> series = (LinkedListSeries<Date, Double, ISeriesPoint<Date, Double>>) streamingSeries.get(pointEntry.getKey());
		series.addLast(pointEntry.getValue());
		pointEntry.setValue(null);
	}
	
	@Override
	public void run() {
		iteratorMap = new HashMap<String, Iterator<ISeriesPoint<Date, Double>>>();
		lastValueMap = new HashMap<String, ISeriesPoint<Date, Double>>();
		
		for (Map.Entry<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> seriesEntry : sourceSeries.entrySet()) {
			@SuppressWarnings("unchecked")
			ISeries<Date, Double, ISeriesPoint<Date, Double>> series  = (ISeries<Date, Double, ISeriesPoint<Date, Double>>) seriesEntry.getValue();
			iteratorMap.put(seriesEntry.getKey(), series.iterator());
			lastValueMap.put(seriesEntry.getKey(), null);
		}
			
		while (true) {
			fetchNextPoints();
			Map.Entry<String, ISeriesPoint<Date, Double>> nextPoint = findFirstPoint();
			if (nextPoint == null || nextPoint.getValue() == null) {
				break;
			} 
			addToStreamingSeries(nextPoint);
		}
	}
}
