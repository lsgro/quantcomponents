/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo.ta;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.quantcomponents.algo.IOrderReceiver;
import com.quantcomponents.algo.IPosition;
import com.quantcomponents.algo.ITradingAgent;
import com.quantcomponents.algo.OrderBean;
import com.quantcomponents.core.calendar.CalendarTradingSchedule;
import com.quantcomponents.core.calendar.ITradingCalendar;
import com.quantcomponents.core.calendar.ITradingSchedule;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesAugmentable;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.model.OrderSide;
import com.quantcomponents.core.model.OrderType;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.IOHLCTimeSeries;
import com.quantcomponents.marketdata.TimeSeriesTail;

/**
 * A trivial example of a trend following trading strategy.
 * It starts folling the trend based on the crossing of a short moving average above or below a long moving average of the prices.
 * When the short moving average crosses above the long one, it takes a long position, and vice-versa.
 * Number of periods for short and long moving averages, trading calendar to be used, position size (short or long) are configurable.
 * Also among the configuration parameters there is: <emph>ignoreLastPeriod</emph>: this parameter should be set when trading in real
 * time, and not set when backtesting. In fact when trading in real time, only the completed periods must enter into the algorithm.
 * When backtesting, all the periods must be taken into account, since otherwise the last of the series will not enter the test.
 * <br>
 * <b>NOTE</b>This is only an example to show how to write trading algorithms with this framework: it has been proved consistenly
 * loss-making!
 *
 */
public class AverageCrossingTradingAgent implements ITradingAgent, ISeriesListener<Date, Double> {
	private static final Logger logger = Logger.getLogger(AverageCrossingTradingAgent.class.getName());
	public static final String TRADING_CALENDAR_NAME = "tradingCalendarName";
	public static final String SHORT_AVERAGE_PERIODS = "shortAveragePeriods";
	public static final String LONG_AVERAGE_PERIODS = "longAveragePeriods";
	public static final String POSITION_SIZE = "positionSize";
	public static final String IGNORE_LAST_PERIOD = "ignoreLastPeriod";
	// configuration values
	private final int positionSize;
	private final boolean ignoreLastPeriod;
	// tail operators
	private final TimeSeriesTail<IOHLCPoint> shortAveragingTail;
	private final TimeSeriesTail<IOHLCPoint> longAveragingTail;
	// input/output
	private volatile IOrderReceiver orderReceiver;
	private volatile IOHLCTimeSeries stockTimeSeries;
	private volatile ISeriesAugmentable<Date, Double, ISeriesPoint<Date, Double>> outputSeries;
	// status
	private volatile RunningStatus runningStatus = RunningStatus.NEW;
	private volatile int position;	
	
	public AverageCrossingTradingAgent(ITradingCalendar tradingCalendar, int shortAveragePeriods, int longAveragePeriods, int positionSize,
			boolean ignoreLastPeriod) {
		this.positionSize = positionSize;
		this.ignoreLastPeriod = ignoreLastPeriod;
		if (ignoreLastPeriod) {
			shortAveragePeriods++;
			longAveragePeriods++;
		}
		ITradingSchedule tradingSchedule = new CalendarTradingSchedule(tradingCalendar);
		shortAveragingTail = new TimeSeriesTail<IOHLCPoint>(tradingSchedule, shortAveragePeriods);
		longAveragingTail = new TimeSeriesTail<IOHLCPoint>(tradingSchedule, longAveragePeriods);
	}

	@Override
	public void wire(Map<String, ? extends ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>> inputSeries, ISeriesAugmentable<Date, Double, ISeriesPoint<Date, Double>> outputSeries) {
		this.outputSeries = outputSeries;
		ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> series = inputSeries.get(AverageCrossingTradingAgentFactory.INPUT_SERIES_NAMES[0]);
		if (!(series instanceof IOHLCTimeSeries)) {
			throw new IllegalArgumentException("Only '" + IOHLCTimeSeries.class.getName() + "' instances can be passed as input series");
		}
		stockTimeSeries = (IOHLCTimeSeries) series;
	}

	@Override
	public void unwire() {
		kill();
	}
	
	@Override
	public void setOrderReceiver(IOrderReceiver orderReceiver) {
		this.orderReceiver = orderReceiver;
	}

	@Override
	public void pause() {
		synchronized (runningStatus) {
			if (runningStatus == RunningStatus.RUNNING) {
				runningStatus = RunningStatus.PAUSED;
			}
		}
	}

	@Override
	public void resume() {
		synchronized (runningStatus) {
			if (runningStatus == RunningStatus.PAUSED) {
				runningStatus = RunningStatus.RUNNING;
			}
		}
	}

	@Override
	public RunningStatus getRunningStatus() {
		return runningStatus;
	}

	@Override
	public void inputComplete() {
		kill();
	}

	@Override
	public void run() {
		synchronized (runningStatus) {
			if (runningStatus != RunningStatus.NEW) {
				throw new IllegalStateException("Could not run from running status: " + runningStatus.name());
			}
			runningStatus = RunningStatus.RUNNING;
		}
		stockTimeSeries.addSeriesListener(this);
		try {
			while (!Thread.interrupted() && runningStatus != RunningStatus.TERMINATED) {
				updatePosition();
				synchronized(this) {
					wait();
				}
			}
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, "Interrupted. Exit", e);
		} finally {
			synchronized (runningStatus) {
				runningStatus = RunningStatus.TERMINATED;
			}
		}
		stockTimeSeries.removeSeriesListener(this);
	}

	@Override
	public synchronized void kill() {
		synchronized (runningStatus) {
			runningStatus = RunningStatus.TERMINATED;
		}
		notify();
	}

	@Override
	public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) {
		if (runningStatus == RunningStatus.RUNNING) {
			updatePosition();
		}
	}

	@Override
	public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
		if (runningStatus == RunningStatus.RUNNING) {
			if (outputSeries != null) {
				outputSeries.insertFromTail(newItem);
			}
			updatePosition();
		}
	}

	@Override
	public void onOrderSubmitted(String orderId, boolean active) { }

	@Override
	public void onOrderFilled(String orderId, int filled, boolean full, double averagePrice) { }

	@Override
	public void onOrderCancelled(String orderId) { }

	@Override
	public void onOrderStatus(String orderId, String status) { }

	@Override
	public void onPositionUpdate(IContract contract, IPosition position) { }

	private double calculateMovingAverage(ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> timeSeries, boolean ignoreLastPeriod) {
		double result;
		int actualNumberOfPeriods = timeSeries.size();
		if (ignoreLastPeriod) {
			actualNumberOfPeriods--;
		}
		double totalSum = 0.0;
		int currentPeriod = 0;
		for (ISeriesPoint<Date, Double> point : timeSeries) {
			if (currentPeriod >= actualNumberOfPeriods) {
				break;
			}
			IOHLCPoint bar = (IOHLCPoint) point;
			totalSum += bar.getClose();
			currentPeriod++;
		}
		result = totalSum / currentPeriod;
		return result; // it must never be called with 0 periods
	}
	
	private void updatePosition() {
		if (stockTimeSeries != null && orderReceiver != null) {
			if (stockTimeSeries.size() < 2) {
			if (stockTimeSeries.size() < 1 || ignoreLastPeriod) {
				return;
			}
		}
		ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> shortTail = (ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>) shortAveragingTail.transform(stockTimeSeries);
		ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>> longTail = (ISeries<Date, Double, ? extends ISeriesPoint<Date, Double>>) longAveragingTail.transform(stockTimeSeries);
		double shortAverageResult = calculateMovingAverage(shortTail, ignoreLastPeriod);
		double longAverageResult = calculateMovingAverage(longTail, ignoreLastPeriod);
		int deltaPosition = 0;
		if (shortAverageResult < longAverageResult) {
			deltaPosition = -positionSize - position;
		} else if (shortAverageResult > longAverageResult) {
			deltaPosition = positionSize - position;
		}
		if (deltaPosition != 0) {
			try {
				OrderBean order = new OrderBean(stockTimeSeries.getContract(), deltaPosition > 0 ? OrderSide.BUY : OrderSide.SELL, OrderType.MARKET, Math.abs(deltaPosition), 0.0, 0.0);
					String orderId = orderReceiver.sendOrder(order);
					order.setId(orderId);
					position += deltaPosition;
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Error while adding delta position: " + deltaPosition, e);
				}
			}
		}
	}
}
