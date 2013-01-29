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
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.quantcomponents.core.calendar.CalendarTradingSchedule;
import com.quantcomponents.core.calendar.ITradingCalendar;
import com.quantcomponents.core.calendar.ITradingSchedule;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesOperator;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.marketdata.TimeSeriesBetween;
import com.quantcomponents.marketdata.TimeSeriesSince;
import com.quantcomponents.marketdata.TimeSeriesTail;

/**
 * Implementation of {@link ITimeSeriesChartModel} for double charts indexed by {@link java.util.Date}
 *
 * @param <P> the type of the data points
 */
public class TimeSeriesChartModel<P extends ISeriesPoint<Date, Double>> implements ITimeSeriesChartModel<P>, ISeriesListener<Date, Double> {
	private static final int DEFAULT_NUM_POINTS = 200;
	
	private final ISeries<Date, Double, P> timeSeries;
	private final Set<ITimeSeriesChartModelListener<P>> listeners = new CopyOnWriteArraySet<ITimeSeriesChartModelListener<P>>();
	private ITradingCalendar tradingCalendar;
	private ITradingSchedule tradingSchedule;
	private ISeriesOperator<Date, Double, P> currentOperator;
	private ISeries<Date, Double, P> data;
	private boolean movingWindow;
	private volatile boolean suspendUpdates;
		
	public TimeSeriesChartModel(ISeries<Date, Double, P> timeSeries, ITradingCalendar tradingCalendar) {
		setTradingCalendar(tradingCalendar);
		this.timeSeries = timeSeries;
		timeSeries.addSeriesListener(this);
		currentOperator = new TimeSeriesTail<P>(tradingSchedule, DEFAULT_NUM_POINTS);
	}

	@Override
	public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) {
		if (!suspendUpdates) {
			recalculateAndNotify();
		}
	}

	@Override
	public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
		if (!suspendUpdates) {
			recalculateAndNotify();
		}
	}
	
	public void dispose() {
		timeSeries.removeSeriesListener(this);
	}
	
	@Override
	public synchronized void setFixedWindow(Date startDate, Date endDate, ITradingCalendar tradingCalendar) {
		setTradingCalendar(tradingCalendar);
		currentOperator = new TimeSeriesBetween<P>(tradingSchedule, startDate, endDate);
		movingWindow = false;
		data = null;
	}

	@Override
	public synchronized void setFixedStartWindow(Date startDate, ITradingCalendar tradingCalendar) {
		setTradingCalendar(tradingCalendar);
		currentOperator = new TimeSeriesSince<P>(tradingSchedule, startDate);
		movingWindow = true;
		data = null;
	}

	@Override
	public synchronized void setFixedDurationWindow(Date startDate, ITradingCalendar tradingCalendar) {
		setTradingCalendar(tradingCalendar);
		TimeSeriesSince<P> tmpOperator = new TimeSeriesSince<P>(tradingSchedule, startDate);
		ISeries<Date, Double, P> tmpWindow = tmpOperator.transform(timeSeries);
		currentOperator = new TimeSeriesTail<P>(tradingSchedule, tmpWindow.size());
		movingWindow = true;
		data = null;
	}

	@Override
	public synchronized void setFixedDurationWindow(int points, ITradingCalendar tradingCalendar) {
		setTradingCalendar(tradingCalendar);
		currentOperator = new TimeSeriesTail<P>(tradingSchedule, points);
		movingWindow = true;
		data = null;
	}

	public void setSuspendUpdates(boolean suspendUpdates) {
		this.suspendUpdates = suspendUpdates;
	}

	@Override
	public synchronized ISeries<Date, Double, P> data() {
		if (data == null) {
			recalculate();
		}	
		return data;
	}

	@Override
	public void addListener(ITimeSeriesChartModelListener<P> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(ITimeSeriesChartModelListener<P> listener) {
		listeners.remove(listener);
	}
	
	private synchronized ISeries<Date, Double, P> recalculate() {
		int oldSize = 0;
		data = currentOperator.transform(timeSeries);
		if (data.size() != oldSize) {
			oldSize = data.size();
		}
		return data;
	}	
	
	private void recalculateAndNotify() {
		ISeries<Date, Double, P> snapshot = recalculate();
		for (ITimeSeriesChartModelListener<P> listener : listeners) {
			listener.onModelUpdated(snapshot);
		}
	}

	@Override
	public synchronized Date getStartDate() {
		if (data != null && !data.isEmpty()) {
			return data.getFirst().getIndex();
		} else {
			return null;
		}
	}

	@Override
	public synchronized Date getEndDate() {
		if (data != null && !data.isEmpty()) {
			return data.getLast().getIndex();
		} else {
			return null;
		}
	}

	@Override
	public synchronized boolean isMovingWindow() {
		return movingWindow;
	}

	@Override
	public ITradingCalendar getTradingCalendar() {
		return tradingCalendar;
	}	

	private void setTradingCalendar(ITradingCalendar tradingCalendar) {
		this.tradingCalendar = tradingCalendar;
		tradingSchedule = new CalendarTradingSchedule(tradingCalendar);
	}
}
