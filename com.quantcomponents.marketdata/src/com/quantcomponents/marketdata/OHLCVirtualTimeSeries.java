/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.IMutableSeries;
import com.quantcomponents.core.model.ISeriesListener;

/**
 * Virtual OHLC time series.
 * This class consolidates information from OHLC and tick series, presenting a dynamic view of the data
 * in terms of OHLC points.
 * For each update, the listeners are advised with the latest values, effectively conveying both OHLC and real time tick data.
 */
public class OHLCVirtualTimeSeries implements IOHLCTimeSeries {
	private static final Logger logger = Logger.getLogger(OHLCVirtualTimeSeries.class.getName());
	private final OHLCTimeSeries innerTimeSeries;
	private final Calendar calendar;
	private final List<ISeriesListener<Date, Double>> listeners = new CopyOnWriteArrayList<ISeriesListener<Date, Double>>();

	public OHLCVirtualTimeSeries(String ID, IContract contract, DataType dataType, BarSize barSize, boolean includeAfterHours, TimeZone timeZone) {
		innerTimeSeries = new OHLCTimeSeries(ID, contract, barSize, dataType, includeAfterHours, timeZone);
		calendar = Calendar.getInstance(timeZone);
	}
	
	private Date alignToBeginningOfBar(Date date) {
		calendar.setTime(date);
		getBarSize().adjustCalendarToBarBeginning(calendar);
		return calendar.getTime();
	}
	
	@Override
	public synchronized int size() {
		if (isEmpty()) {
			return 0;
		}
 		long span = innerTimeSeries.getLast().getIndex().getTime() - innerTimeSeries.getFirst().getIndex().getTime();
		return (int) (span / innerTimeSeries.getInterval()) + 1;
	}

	@Override
	public boolean isEmpty() {
		return innerTimeSeries.isEmpty();
	}
	
	public void addTick(ITickPoint tick) {
		Date barDate = alignToBeginningOfBar(tick.getIndex());
		IOHLCPoint oldBar = null;
		OHLCPointAccumulator newBar = null;
		synchronized (this) {
			if (innerTimeSeries.isEmpty()) {
				newBar = new OHLCPointAccumulator(innerTimeSeries.getBarSize(), innerTimeSeries.getDataType(), barDate);
				newBar.addTick(tick);
				innerTimeSeries.addLast(newBar);
			} else {
				OHLCPointAccumulator existingBar = null;
				if (barDate.equals(innerTimeSeries.getFirst().getIndex())) {
					existingBar = (OHLCPointAccumulator) innerTimeSeries.getFirst();
				} else if (barDate.equals(innerTimeSeries.getLast().getIndex())) {
					existingBar = (OHLCPointAccumulator) innerTimeSeries.getLast();
				}
				if (existingBar != null) {
					oldBar = OHLCPoint.copy(existingBar);
					existingBar.addTick(tick);
					newBar = existingBar;
				} else {
					if (barDate.before(innerTimeSeries.getFirst().getIndex())) {
						newBar = new OHLCPointAccumulator(innerTimeSeries.getBarSize(), innerTimeSeries.getDataType(), barDate);
						newBar.addTick(tick);
						innerTimeSeries.addFirst(newBar);
					} else if (barDate.after(innerTimeSeries.getLast().getIndex())) {
						newBar = new OHLCPointAccumulator(innerTimeSeries.getBarSize(), innerTimeSeries.getDataType(), barDate);
						newBar.addTick(tick);
						innerTimeSeries.addLast(newBar);
					} else {
						newBar = null; // ignoring already present historical data
					}
				}
			}
		}
		if (oldBar != null) {
			notifyBarUpdated(oldBar, newBar);
		} else if (newBar != null) {
			notifyBarAdded(newBar);
		}
	}
	
	public void addOrUpdateBar(IOHLCPoint bar) {
		Date barDate = alignToBeginningOfBar(bar.getIndex());
		if (getBarSize() != BarSize.ONE_DAY && !barDate.equals(bar.getIndex())) {
			logger.log(Level.WARNING, "Bar date: " + bar.getIndex() + " not aligned with reference date: " + barDate);
		}
		OHLCPointAccumulator newBar = OHLCPointAccumulator.fromIOHLCPoint(bar, innerTimeSeries.getDataType());
		IOHLCPoint oldBar = null;
		synchronized (this) {
			if (innerTimeSeries.isEmpty()) {
				innerTimeSeries.addLast(newBar);
			} else {
				IOHLCPoint existingBar = null;
				if (barDate.equals(innerTimeSeries.getFirst().getIndex())) {
					existingBar = (IOHLCPoint) innerTimeSeries.getFirst();
				} else if (barDate.equals(innerTimeSeries.getLast().getIndex())) {
					existingBar = (IOHLCPoint) innerTimeSeries.getLast();
				}
				if (existingBar != null) {
					oldBar = OHLCPoint.copy(existingBar);
					innerTimeSeries.updateTail(newBar);
				} else {
					if (barDate.before(innerTimeSeries.getFirst().getIndex())) {
						innerTimeSeries.addFirst(newBar);
					} else if (barDate.after(innerTimeSeries.getLast().getIndex())) {
						innerTimeSeries.addLast(newBar);
					} else {
						newBar = null; // ignoring already present historical data
					}
				}
			}
		}
		if (oldBar != null) {
			notifyBarUpdated(oldBar, newBar);
		} else if (newBar != null) {
			notifyBarAdded(newBar);
		}
	}
	
	private void notifyBarAdded(IOHLCPoint bar) {
		for (ISeriesListener<Date, Double> listener : listeners) {
			listener.onItemAdded(bar);
		}
	}

	private void notifyBarUpdated(IOHLCPoint oldBar, IOHLCPoint updatedBar) {
		for (ISeriesListener<Date, Double> listener : listeners) {
			listener.onItemUpdated(oldBar, updatedBar);
		}
	}

	@Override
	public long getTimestamp() {
		return innerTimeSeries.getTimestamp();
	}
	
	@Override
	public IContract getContract() {
		return innerTimeSeries.getContract();
	}

	@Override
	public BarSize getBarSize() {
		return innerTimeSeries.getBarSize();
	}

	@Override
	public DataType getDataType() {
		return innerTimeSeries.getDataType();
	}

	@Override
	public boolean isIncludeAfterHours() {
		return innerTimeSeries.isIncludeAfterHours();
	}

	@Override
	public IOHLCPoint getFirst() {
		return (IOHLCPoint) innerTimeSeries.getFirst();
	}

	@Override
	public IOHLCPoint getLast() {
		return (IOHLCPoint) innerTimeSeries.getLast();
	}

	@Override
	public IOHLCPoint getMinimum() {
		return (IOHLCPoint) innerTimeSeries.getMinimum();
	}

	@Override
	public IOHLCPoint getMaximum() {
		return (IOHLCPoint) innerTimeSeries.getMaximum();
	}

	@Override
	public void addSeriesListener(ISeriesListener<Date, Double> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeSeriesListener(ISeriesListener<Date, Double> listener) {
		listeners.remove(listener);
	}

	@Override
	public Iterator<IOHLCPoint> iterator() {
		return innerTimeSeries.iterator();
	}

	@Override
	public Iterator<IOHLCPoint> descendingIterator() {
		return innerTimeSeries.descendingIterator();
	}

	@Override
	public TimeZone getTimeZone() {
		return innerTimeSeries.getTimeZone();
	}

	@Override
	public long getInterval() {
		return innerTimeSeries.getInterval();
	}

	@Override
	public boolean isEnforceStrictSequence() {
		return innerTimeSeries.isEnforceStrictSequence();
	}

	@Override
	public IMutableSeries<Date, Double, IOHLCPoint> createEmptyMutableSeries(String ID) {
		return innerTimeSeries.createEmptyMutableSeries(ID);
	}

	@Override
	public String getPersistentID() {
		return innerTimeSeries.getPersistentID();
	}
}
