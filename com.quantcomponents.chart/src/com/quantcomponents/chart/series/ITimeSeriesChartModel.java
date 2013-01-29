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

import com.quantcomponents.core.calendar.ITradingCalendar;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Model for rendering of time series on a chart
 * @param <P>
 */
public interface ITimeSeriesChartModel<P extends ISeriesPoint<Date, Double>> {
	/**
	 * Configures the model to return a fixed period between two dates
	 * @param startDate start date of the period
	 * @param endDate end date of the period
	 * @param tradingCalendar trading calendar used
	 */
	void setFixedWindow(Date startDate, Date endDate, ITradingCalendar tradingCalendar);
	/**
	 * Configures the model to return a period with a fixed start, up to the end of the series
	 * @param startDate start date of the period
	 * @param tradingCalendar trading calendar used
	 */
	void setFixedStartWindow(Date startDate, ITradingCalendar tradingCalendar);
	/**
	 * Configures the model to return a moving period of fixed length up to the end of the series, setup with an initial start date.
	 * @param startDate date to be used initially to compute the number of periods
	 * @param tradingCalendar trading calendar to be used
	 */
	void setFixedDurationWindow(Date startDate, ITradingCalendar tradingCalendar);
	/**
	 * Configures the model to return a moving period of fixed length up to the end of the series, setup with a number of points.
	 * @param points number of points in the period
	 * @param tradingCalendar trading calendar to be used
	 */
	void setFixedDurationWindow(int points, ITradingCalendar tradingCalendar);
	/**
	 * The data representing the model as configured
	 * @return a snapshot time series based on a source time series according to the configuration of the model
	 */
	ISeries<Date, Double, P> data();
	/**
	 * Adds a listener to be advised of data changes in the series
	 */
	void addListener(ITimeSeriesChartModelListener<P> listener);
	/**
	 * Removes a listener
	 */
	void removeListener(ITimeSeriesChartModelListener<P> listener);
	/**
	 * Returns the start date of the current shapshot data
	 */
	Date getStartDate();
	/**
	 * Returns the end date of the current shapshot data
	 */
	Date getEndDate();
	/**
	 * Returns true if a "moving window" mode is configured: i.e. with {@link ITimeSeriesChartModel#setFixedDurationWindow(Date, ITradingCalendar)} or {@link ITimeSeriesChartModel#setFixedDurationWindow(int, ITradingCalendar)}
	 * @return
	 */
	boolean isMovingWindow();
	/**
	 * Set the suspend-updates mode on or off.
	 * The suspend-updates mode stops any update from the source time series to the model.
	 */
	void setSuspendUpdates(boolean suspendUpdates);
	/**
	 * Dispose of the model to stop listening to the source series
	 */
	void dispose(); 
	/**
	 * Returns the trading calendar used by the model
	 */
	ITradingCalendar getTradingCalendar();
}
