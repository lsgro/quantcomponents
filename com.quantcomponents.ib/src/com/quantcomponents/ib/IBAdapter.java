/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ib;

import java.net.ConnectException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EWrapperMsgGenerator;
import com.ib.client.Execution;
import com.ib.client.ExecutionFilter;
import com.ib.client.Order;
import com.ib.client.TickType;
import com.quantcomponents.algo.IExecutionService;
import com.quantcomponents.algo.IOrder;
import com.quantcomponents.algo.IOrderStatusListener;
import com.quantcomponents.algo.IPosition;
import com.quantcomponents.algo.IPositionListener;
import com.quantcomponents.algo.ITrade;
import com.quantcomponents.algo.PositionBean;
import com.quantcomponents.algo.TradeBean;
import com.quantcomponents.core.exceptions.NoDataReturnedException;
import com.quantcomponents.core.exceptions.PacingViolationException;
import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.core.model.TimePeriod;
import com.quantcomponents.core.model.beans.ContractBean;
import com.quantcomponents.core.model.beans.ImmutableContractBean;
import com.quantcomponents.marketdata.IMarketDataProvider;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.ITickPoint;
import com.quantcomponents.marketdata.OHLCPoint;
import com.quantcomponents.marketdata.TickPoint;

public class IBAdapter implements IMarketDataProvider, IExecutionService {
	private static final Logger logger = Logger.getLogger(IBAdapter.class.getName());
	
	public static final String[] MKT_DATA_LINES = { "Less than 499","500 - 749","750 - 999","more than 999" };
	
	public static final long DEFAULT_MIN_ELAPSED_BETWEEN_HIST_DATA_REQ_BATCHES_MS = 10000;
	public static final long HIST_DATA_REQUEST_SLEEP_QUANTUM = 500;
	public static final long CONNECTION_SLEEP_QUANTUM = 100;
	public static final long HANDLE_REQUEST_SLEEP_QUANTUM = 100;
	public static final int DEFAULT_NUM_OF_REQS_IN_HIST_DATA_REQ_BATCHES = 5;
	public static final int DEFAULT_MIN_HIST_DATA_PERIOD_MS = 60000;
	public static final int DEFAULT_MAX_BARS_PER_REQUEST = 1999;
	public static final int DEFAULT_MAX_WAIT_TO_CONNECT_MS = 10000;
	public static final int DEFAULT_MAX_WAIT_RESPONSE_MS = 30000;
	
	public static final String DEFAULT_HOST = "";
	public static final Integer DEFAULT_PORT = 7496;
	public static final Integer DEFAULT_CLIENT_ID = 0;
	public static final Integer DEFAULT_START_REQ_ID = 0;
	public static final Integer DEFAULT_MKT_DATA_LINES_INDEX = 0;

	private static final Pattern HIST_DATA_LAST_UPDATE_MESSAGE = Pattern.compile("finished-\\d{8}\\s+\\d\\d:\\d\\d:\\d\\d-(\\d{8}\\s+\\d\\d:\\d\\d:\\d\\d)");
	
	// a separate thread for clients to process asynchronously IB events
	// due to IB client single thread architecture, a call to IB from an event handler blocks and times-out
	// single thread to preserve sequence of events
	private final ExecutorService threadPool = Executors.newSingleThreadExecutor();
	
	private final String host;
	private final int port;
	private final int clientId;
	private final int noMktDataLinesKey;
	private final String accountId;
	
	private long minElapsedBetweenHistDataReqBatches = DEFAULT_MIN_ELAPSED_BETWEEN_HIST_DATA_REQ_BATCHES_MS;
	private int numOfReqsInHistDataReqBatches = DEFAULT_NUM_OF_REQS_IN_HIST_DATA_REQ_BATCHES;
	private int minHistDataPeriodMs = DEFAULT_MIN_HIST_DATA_PERIOD_MS;
	private int maxBarPerRequest = DEFAULT_MAX_BARS_PER_REQUEST;
	private int maxWaitToConnectMs = DEFAULT_MAX_WAIT_TO_CONNECT_MS;
	private int maxWaitResponseMs = DEFAULT_MAX_WAIT_RESPONSE_MS;
	
	private volatile IBConnection connectionInstance;
	private final AtomicInteger requestSequence;
	private final IBConstantTranslator constantTranslator;
	
	private Map<String, Set<IRealTimeDataListener>> realTimeDataListeners = new ConcurrentHashMap<String, Set<IRealTimeDataListener>>();
	private Map<String, Integer> realTimeDataRequestIds = new ConcurrentHashMap<String, Integer>();
	
	private Map<String, Set<ITickListener>> tickListeners = new ConcurrentHashMap<String, Set<ITickListener>>();
	private Map<String, Integer> tickRequestIds = new ConcurrentHashMap<String, Integer>();
	
	private Set<IOrderStatusListener> orderStatusListeners = new CopyOnWriteArraySet<IOrderStatusListener>();
	private Set<IPositionListener> positionListeners = new CopyOnWriteArraySet<IPositionListener>();
	
	private Map<Integer, IOrder> sentOrders = new ConcurrentHashMap<Integer, IOrder>();
	
	private volatile int nextOrderId = -1;
	
	private OrderStatusClient orderStatusClient;
	private PositionClient positionClient;
	
	private class OrderStatusClient extends IBClient {
		@Override
		public void orderStatus(int orderId, final String status, final int filled, final int remaining, final double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
			final String orderIdStr = Integer.toString(orderId);
			if ("Submitted".equals(status) || "PreSubmitted".equals(status)) {
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						for (IOrderStatusListener listener : orderStatusListeners) {
							try {
								listener.onOrderSubmitted(orderIdStr, true);
							} catch (Throwable t) {
								logger.log(Level.SEVERE, "Exception during order status dispatch", t);
							}
						}
					}});
			} else	if ("Filled".equals(status)) {
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						for (IOrderStatusListener listener : orderStatusListeners) {
							try {
								listener.onOrderFilled(orderIdStr, filled, remaining == 0 ? true : false , avgFillPrice);
							} catch (Throwable t) {
								logger.log(Level.SEVERE, "Exception during order status dispatch", t);
							}
						}
					}});
			} else if ("Cancelled".equals(status)) {
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						for (IOrderStatusListener listener : orderStatusListeners) {
							try {
								listener.onOrderCancelled(orderIdStr);
							} catch (Throwable t) {
								logger.log(Level.SEVERE, "Exception during order status dispatch", t);
							}
						}
					}});
			} else {
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						for (IOrderStatusListener listener : orderStatusListeners) {
							try {
								listener.onOrderStatus(orderIdStr, status);
							} catch (Throwable t) {
								logger.log(Level.SEVERE, "Exception during order status dispatch", t);
							}
						}
					}});
			}
		}
		
		@Override
		protected void doSendRequest() {
			throw new UnsupportedOperationException();
		}
	}

	private class PositionClient extends IBClient {
		
		private class PositionPriceUpdater {
			PositionBean position;
			ITickListener tickListener;
		}
		final Map<IContract, PositionPriceUpdater> positionUpdaters = new ConcurrentHashMap<IContract, PositionPriceUpdater>();
		
		@Override
		public void updatePortfolio(Contract iBContract, int positionAmt, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
			if (accountName.equals(accountId)) {
				final IContract contract = ContractBean.copyOf(new IBContract(iBContract, constantTranslator));
				final PositionBean position = new PositionBean(new Date(), positionAmt, marketPrice, marketValue, averageCost, unrealizedPNL, realizedPNL);
				
				if (!positionUpdaters.containsKey(contract)) { // setup price listener for that contract: since IB sends too few updates, we update automatically based on price changes
					PositionPriceUpdater positionPriceUpdater = new PositionPriceUpdater();
					positionPriceUpdater.position = position;
					final ITickListener tickListener = new ITickListener() {
						@Override
						public void onTick(ITickPoint tick) {
							if (tick.getDataType() == DataType.TRADES) {
								PositionPriceUpdater pu = positionUpdaters.get(contract);
								if (pu != null) {
									synchronized (pu) { // update position with current price
										pu.position.setMarketPrice(tick.getValue());
										pu.position.setMarketValue(tick.getValue() * pu.position.getSignedAmount());
										pu.position.setUnrealizedPnl((tick.getValue() - pu.position.getAveragePrice()) * pu.position.getSignedAmount());
									}
									deliverPosition(contract, pu.position);
								}
							}
						}};
					positionPriceUpdater.tickListener = tickListener;
					positionUpdaters.put(contract, positionPriceUpdater);
					threadPool.execute(new Runnable() {
						@Override
						public void run() {
							try {
								startTicks(contract, tickListener);
							} catch (Throwable e) {
								logger.log(Level.SEVERE, "Exception while setting up price listener for position update", e);
							}
						}
					});
				} else {
					PositionPriceUpdater pu = positionUpdaters.get(contract);
					if (pu != null) {
						synchronized(pu) { // renew position with original values from IB
							pu.position = position;
						}
					}
				}
				
				deliverPosition(contract, position);
			}
		}
		
		public void stopPositionPriceUpdates() throws ConnectException {
			for (Map.Entry<IContract, PositionPriceUpdater> entry : positionUpdaters.entrySet()) {
				stopTicks(entry.getKey(), entry.getValue().tickListener);
			}
			positionUpdaters.clear();
		}
		
		private void deliverPosition(final IContract contract, final IPosition position) {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					for (IPositionListener listener : positionListeners) {
						try {
							listener.onPositionUpdate(contract, PositionBean.copyOf(position));
						} catch (Throwable t) {
							logger.log(Level.SEVERE, "Exception during position update dispatch", t);
						}
					}
				}});
		}
		
		@Override
		protected void doSendRequest() {
			throw new UnsupportedOperationException();
		}
	};

	public IBAdapter(String host, int port, int clientId, int firstRequestNo, int noMktDataLinesKey, String accountId) {
		this.host = host;
		this.port = port;
		this.clientId = clientId;
		this.noMktDataLinesKey = noMktDataLinesKey;
		this.accountId = accountId;
		requestSequence = new AtomicInteger(firstRequestNo);
		constantTranslator = new IBConstantTranslator();
	}

	public IBConstantTranslator getConstantTranslator() {
		return constantTranslator;
	}

	private void connect(ITaskMonitor taskMonitor) throws ConnectException {
		if (connectionInstance == null || !connectionInstance.isConnected()) {
			Thread connectionThread = new Thread(new Runnable() {
				@Override
				public void run() {
					if (connectionInstance == null) {
						connectionInstance = new IBConnection(host, port, clientId);
					}
				}});
			connectionThread.start();
			long waited = 0;
			while ((taskMonitor == null || !taskMonitor.isCancelled()) && (connectionInstance == null || !connectionInstance.isConnected()) && waited < maxWaitToConnectMs) {
				try {
					Thread.sleep(CONNECTION_SLEEP_QUANTUM);
				} catch (InterruptedException e) {
					logger.log(Level.WARNING, "Exception while waiting for connection to IB", e);
				}
				waited += CONNECTION_SLEEP_QUANTUM;
			}
			if (taskMonitor != null && taskMonitor.isCancelled()) {
				throw new ConnectException("Connection cancelled by user after " + waited + "ms");
			}
			if (connectionInstance == null || !connectionInstance.isConnected()) {
				throw new ConnectException("Connection failed after " + waited + "ms");
			}
		}
		if (taskMonitor != null) {
			taskMonitor.done();
		}
	}

	private synchronized IBConnection getConnection() throws ConnectException {
		connect(null);
		return connectionInstance;
	}

	public synchronized boolean isConnected() {
		if (connectionInstance != null && connectionInstance.isConnected())
			return true;
		else
			return false;
	}

	public synchronized void disconnect() {
		if (connectionInstance != null) 
			connectionInstance.close();
	}

	@Override
	public Deque<ITrade> getTrades() throws ConnectException, RequestFailedException {
		SimpleDateFormat executionFilterDateFormat = new SimpleDateFormat("yyyyMMdd-00:00:00");
		final ExecutionFilter filter = new ExecutionFilter();
		filter.m_clientId = clientId;
		filter.m_time = executionFilterDateFormat.format(new Date());
		final Deque<ITrade> trades = new LinkedList<ITrade>();
		
		IBClient tradesClient = new IBClient() {
			@Override
			public void doSendRequest() {
				getSender().reqExecutions(getPendingReqId(), filter);
			}
			
			@Override
			public void execDetails(int reqId, Contract iBContract, Execution execution) {
				if (reqId == getPendingReqId()) {
					IOrder order = sentOrders.get(execution.m_orderId);
					trades.add(TradeBean.copyOf(new IBTradeInfo(execution, order)));
				}
			}
			
			@Override
			public void execDetailsEnd(int reqId) {
				if (reqId == getPendingReqId()) {
					setRequestComplete();
				}
			}
		};
		
		IBConnection connection = getConnection();
		
		connection.addClient(tradesClient);

		tradesClient.sendRequest(requestSequence.getAndIncrement());

		try {
			handleRequest(tradesClient);
		} finally {
			connection.removeClient(tradesClient);
		}	

		return trades;
	}

	@Override
	public List<IContract> searchContracts(IContract criteria, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		final Contract iBCriteria = IBContract.toIBContract(criteria, constantTranslator);
		final List<IContract> result = new ArrayList<IContract>();
		
		if (taskMonitor != null) {
			taskMonitor.beginTask(1);
		}

		IBClient contractDetailsClient = new IBClient() {
			@Override
			public void doSendRequest() {
				getSender().reqContractDetails(getPendingReqId(), iBCriteria);
			}

			@Override
			public void contractDetails(int reqId, ContractDetails details) {
				if (reqId == getPendingReqId()) {
					IBContract iBContract = new IBContract(details.m_summary, constantTranslator);
					iBContract.setDetails(details);
					ImmutableContractBean bean = new ImmutableContractBean(iBContract);
					result.add(bean);
				}
			}

			@Override
			public void contractDetailsEnd(int reqId) {
				if (reqId == getPendingReqId())
					setRequestComplete();
			}
		};

		IBConnection connection = getConnection();
		
		connection.addClient(contractDetailsClient);

		contractDetailsClient.sendRequest(requestSequence.getAndIncrement());

		try {
			handleRequest(contractDetailsClient);
		} finally {
			connection.removeClient(contractDetailsClient);
		}

		if (taskMonitor != null) {
			taskMonitor.done();
		}

		return result;
	}
	
	private int maxYearsOfHistoricalData() throws RequestFailedException {
		switch (noMktDataLinesKey) {
		case 0:
			return 1; // Less than 499
		case 1:
			return 2; // 500 - 749
		case 2:
			return 3; // 750 - 999
		case 3:
			return 4; // more than 999
		default:
			throw new RequestFailedException("Illegal number of market data lines");
		}
	}
	
	private long maxRequestPeriodPerBarSize(BarSize barSize) {
		if (barSize.compareTo(BarSize.ONE_SEC) <= 0) {
			return 1000L * 60L * 30L; // 30 min
		} else if (barSize.compareTo(BarSize.FIVE_SECS) < 0) {
			return 1000L * 60L * 60L * 2L; // 2 hours
		} else if (barSize.compareTo(BarSize.FIFTEEN_SECS) < 0) {
			return 1000L * 60L * 60L * 4L; // 4 hours
		} else if (barSize.compareTo(BarSize.THIRTY_SECS) <= 0) {
			return 1000L * 60L * 60L * 24L; // 1 day
		} else if (barSize.compareTo(BarSize.ONE_MIN) <= 0) {
			return 1000L * 60L * 60L * 24L * 2L; // 2 days
		} else if (barSize.compareTo(BarSize.FIFTEEN_MINS) <= 0) {
			return 1000L * 60L * 60L * 24L * 7L; // 1 week
		} else if (barSize.compareTo(BarSize.ONE_HOUR) <= 0) {
			return 1000L * 60L * 60L * 24L * 30L; // 1 month
		} else
			return 1000L * 60L * 60L * 24L * 30L * 12L * 4L; // default: 4 years
	}

	public List<IOHLCPoint> historicalBars(IContract contract, Date startDateTime, Date endDateTime,
			BarSize barSize, DataType dataType, boolean includeAfterHours, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {

		SimpleDateFormat timestampDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

		LinkedList<List<IOHLCPoint>> tmpResults = new LinkedList<List<IOHLCPoint>>(); 
		
		// find max request period based on IB limitations
		
		long remainingPeriod = endDateTime.getTime() - startDateTime.getTime();
		
		// check number of years
		long numberOfYears = remainingPeriod / 360L / 24L / 60L / 60L / 1000L;
		int maxYears = maxYearsOfHistoricalData();
		if (numberOfYears > maxYears) {
			throw new RequestFailedException("Current number of market data lines only allows for " + maxYears + " of historical data");
		}
		// break request in smaller batches to comply with IB limitations
		long maxPeriodForOneRequest = remainingPeriod;
		long barsPerInterval = maxPeriodForOneRequest / barSize.getDurationInMs();
		if (barsPerInterval > maxBarPerRequest) {
			maxPeriodForOneRequest = barSize.getDurationInMs() * maxBarPerRequest;
		}
		maxPeriodForOneRequest = Math.min(maxPeriodForOneRequest, maxRequestPeriodPerBarSize(barSize));
		
		Date nextEndDateTime = endDateTime;
		
		// comply with IB timing restrictions
		long lastBatchTime = 0L;
		int requestNo = 0;
		
		int approxCallNo = (int) (remainingPeriod / maxPeriodForOneRequest);
		if (taskMonitor != null) {
			taskMonitor.beginTask(approxCallNo * 10); // retrieving then adding partial results: IB calls count 7 - saving results count 3
		}

		logger.log(Level.INFO, "Start downloading: " + new Date()); // TODO: remove
		boolean forceSleep = false;
		while (remainingPeriod > 0) {
			
			if (taskMonitor != null && taskMonitor.isCancelled()) {
				throw new RequestFailedException("Request cancelled by user");
			}
			
			if (requestNo % numOfReqsInHistDataReqBatches == 0 || forceSleep) {
				long elapsedSinceLastBatch = System.currentTimeMillis() - lastBatchTime;
				logger.log(Level.INFO, "Now: " + timestampDateFormat.format(new Date()) + "; Elapsed: " + elapsedSinceLastBatch); // TODO: remove
				if (elapsedSinceLastBatch < minElapsedBetweenHistDataReqBatches) {
					try {
						logger.log(Level.INFO, "Sleeping: " + (minElapsedBetweenHistDataReqBatches - elapsedSinceLastBatch) + "ms"); // TODO: remove
						for (long partElapsed = 0L; partElapsed < minElapsedBetweenHistDataReqBatches - elapsedSinceLastBatch; partElapsed += HIST_DATA_REQUEST_SLEEP_QUANTUM) {
							Thread.sleep(HIST_DATA_REQUEST_SLEEP_QUANTUM);
							if (taskMonitor != null && taskMonitor.isCancelled()) {
								throw new RequestFailedException("Request cancelled by user");
							}
						}
					} catch (InterruptedException e) {
						throw new RequestFailedException("Exception while sleeping through request batches", e);
					}
				}
				lastBatchTime = System.currentTimeMillis();
				forceSleep = false;
			}
			long currentRequestPeriod;
			if (remainingPeriod > maxPeriodForOneRequest) {
				currentRequestPeriod = maxPeriodForOneRequest;
			} else if (remainingPeriod < minHistDataPeriodMs) {
				currentRequestPeriod = minHistDataPeriodMs;
			} else {
				currentRequestPeriod = remainingPeriod;
			}
			TimePeriod requestPeriod = TimePeriod.findApproxPeriod(currentRequestPeriod);
			List<IOHLCPoint> partialResult = null;

			logger.log(Level.INFO, "Request #" + requestNo + ". Data until: " + nextEndDateTime); // TODO: remove
			try {
				
				partialResult = doGetHistoricalData(contract, nextEndDateTime, requestPeriod, barSize, dataType, includeAfterHours);
				
			} catch (PacingViolationException e) {
				logger.log(Level.WARNING, "Pacing violation exception while retrieving historical data from IB. Force sleep", e);
				forceSleep = true;
				continue;
			} catch (NoDataReturnedException e) {
				logger.log(Level.INFO, "Historical data request from IB returned no data. Continue with next request", e);
				partialResult = null;
			}
			if (partialResult == null || nextEndDateTime.equals(partialResult.get(0).getIndex())) { // empty or duplicate result
				logger.log(Level.INFO, "Empty or duplicate result: " + nextEndDateTime); // TODO: remove
				nextEndDateTime = TimePeriod.subtractPeriodFromDate(nextEndDateTime, requestPeriod);
			} else {
				tmpResults.addFirst(partialResult);
				nextEndDateTime = partialResult.get(0).getIndex();
			}
			remainingPeriod = nextEndDateTime.getTime() - startDateTime.getTime();
			requestNo++;
			
			if (taskMonitor != null) {
				taskMonitor.worked(7);
			}
		}

		logger.log(Level.INFO, "All data downloaded. Create time series: " + new Date()); // TODO: remove
		List<IOHLCPoint> result = new ArrayList<IOHLCPoint>();

		IOHLCPoint lastBarAdded = null;
		Date dateOfLastBarAdded = new Date(0L);
		for (List<IOHLCPoint> partialResult : tmpResults) {

			if (taskMonitor != null && taskMonitor.isCancelled()) {
				throw new RequestFailedException("Request cancelled by user");
			}
			
			for (IOHLCPoint bar : partialResult) {
				Date endOfLastBarAdded = new Date(dateOfLastBarAdded.getTime() + barSize.getDurationInMs());
				if (!bar.getIndex().before(startDateTime)) {
					if (!bar.getIndex().before(endOfLastBarAdded)) {
						result.add(bar);
						lastBarAdded = bar;
						dateOfLastBarAdded = bar.getIndex();						
					} else if (bar.getIndex().after(dateOfLastBarAdded) && bar.getIndex().before(endOfLastBarAdded)) { // consolidate partial bars (e.g. ES 15.00-15.15 + 15.30-14.00)
						result.set(result.size() - 1, OHLCPoint.merge(lastBarAdded, bar));
					}
				}
				if (!bar.getIndex().before(startDateTime) && bar.getIndex().after(dateOfLastBarAdded)) {
					result.add(bar);
					dateOfLastBarAdded = bar.getIndex();
				}
			}
			
			if (taskMonitor != null) {
				taskMonitor.worked(3);
			}
		}
		
		if (taskMonitor != null) {
			taskMonitor.done();
		}
		
		// TODO remove
		logger.log(Level.INFO, "Now the time is: " + new Date());
		if (!result.isEmpty()) {
			logger.log(Level.INFO, "Beginning of historical data: " + result.get(0).getIndex());
			logger.log(Level.INFO, "End of historical data: " + result.get(result.size() - 1).getIndex());
		}

		return result;
	}

	private List<IOHLCPoint> doGetHistoricalData(IContract contract, final Date endDateTime, final TimePeriod period,
			final BarSize barSize, DataType dataType, final boolean includeAfterHours) throws ConnectException, RequestFailedException {
		final Contract iBContract = IBContract.toIBContract(contract, constantTranslator);
		final String iBDurationUnit = constantTranslator.getCode(period.getUnitOfTime());
		final String iBBarSize = constantTranslator.getCode(barSize);
		final String iBDataType = dataType == DataType.BID_ASK ? "MIDPOINT" : constantTranslator.getCode(dataType); // BID_ASK does not return OHLC bars: open = WAVG(BID), close = WAVG(ASK)
		final List<IOHLCPoint> result = new ArrayList<IOHLCPoint>();
		final SimpleDateFormat timestampDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

		IBClient historicalDataClient = new IBClient() {
			private DateFormat sendDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss z");
			private DateFormat receiveDateFormat = new SimpleDateFormat("yyyyMMdd");

			@Override
			public void doSendRequest() {
				sendDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
				String endTimeRepr = sendDateFormat.format(endDateTime);
				logger.log(Level.INFO, "Historical Data Request: " + endTimeRepr + "; " + period.getAmount() + " " + iBDurationUnit + "; " + iBBarSize + "; " + iBDataType);
				getSender().reqHistoricalData(getPendingReqId(), iBContract, endTimeRepr, period.getAmount() + " " + iBDurationUnit, iBBarSize, iBDataType,
						includeAfterHours ? 0 : 1, 2);
			}

			@Override
			public void historicalData(int reqId, String dateRepr, double open, double high, double low, double close, int volume, int count, double WAP,
					boolean hasGaps) {
				if (reqId == getPendingReqId()) {
					if (dateRepr.startsWith("finished-")) {
						Matcher matcher = HIST_DATA_LAST_UPDATE_MESSAGE.matcher(dateRepr);
						if (matcher.matches()) {
							String timestampRepr = matcher.group(1);
							try {
								Date timestamp = timestampDateFormat.parse(timestampRepr);
								((OHLCPoint)result.get(result.size() - 1)).setLastUpdate(timestamp);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						setRequestComplete();
					} else {
						Date date;
						if (dateRepr.length() == 8) { // alternate format sent by IB when barSize = 1 DAY: yyyyMMdd
							try {
								date = receiveDateFormat.parse(dateRepr);
							} catch (ParseException e) {
								logger.log(Level.SEVERE, "Unparseable date in historical data: " + dateRepr, e);
								e.printStackTrace();
								return;
							}
						} else {
							Long dateInSecs = Long.valueOf(dateRepr);
							date = new Date(dateInSecs * 1000);
						}
						OHLCPoint data = new OHLCPoint(barSize, date, open, high, low, close, (long)volume, WAP, count);
						result.add(data);
					}
				}
			}
		};

		IBConnection connection = getConnection();

		connection.addClient(historicalDataClient);

		historicalDataClient.sendRequest(requestSequence.getAndIncrement());

		try {
			handleRequest(historicalDataClient);
		} finally {
			connection.removeClient(historicalDataClient);
		}

		return result;
	}
	
	private String buildRealTimeSeriesRequestCode(IContract contract, BarSize barSize, DataType dataType, boolean includeAfterHours) {
		SimpleDateFormat eightDigitDateFormat = new SimpleDateFormat("yyyyMMdd");
		StringBuilder buffer = new StringBuilder();
		buffer.append(contract.getSymbol()).append("|");
		buffer.append(contract.getCurrency().getCurrencyCode()).append("|");
		buffer.append(contract.getSecurityType().name()).append("|");
		if (contract.getExpiryDate() != null) {
			buffer.append(eightDigitDateFormat.format(contract.getExpiryDate())).append("|");
		}
		buffer.append(barSize.name()).append("|");
		buffer.append(dataType.name()).append("|");
		buffer.append(Boolean.toString(includeAfterHours));
		return buffer.toString();
	}

	@Override
	public void startRealTimeBars(IContract contract, final BarSize barSize, DataType dataType, final boolean includeAfterHours, IRealTimeDataListener listener)
			throws ConnectException, RequestFailedException {
		
		final int barSizeConst = 5;
		final Contract iBContract = IBContract.toIBContract(contract, constantTranslator);
		final String iBDataType = dataType == DataType.BID_ASK ? "MIDPOINT" : constantTranslator.getCode(dataType); // BID_ASK does not return OHLC bars: open = WAVG(BID), close = WAVG(ASK)
		
		final String realTimeSeriesRequestCode = buildRealTimeSeriesRequestCode(contract, barSize, dataType, includeAfterHours);
		
		Set<IRealTimeDataListener> setOfListeners = realTimeDataListeners.get(realTimeSeriesRequestCode);
		
		boolean newRequest = false;
		IBConnection connection = null;
		IBClient realTimeDataClient = null;
		
		if (setOfListeners == null || setOfListeners.isEmpty()) {
			newRequest = true;
			setOfListeners = new CopyOnWriteArraySet<IRealTimeDataListener>();
			realTimeDataListeners.put(realTimeSeriesRequestCode, setOfListeners);
			
			final Set<IRealTimeDataListener> currentListeners = setOfListeners; // duplicate as final variable to make it valid for use by the inner class
			realTimeDataClient = new IBClient() {
				@Override
				public void doSendRequest() {
					int requestId = getPendingReqId();
					realTimeDataRequestIds.put(realTimeSeriesRequestCode, requestId);
					getSender().reqRealTimeBars(getPendingReqId(), iBContract, barSizeConst, iBDataType, !includeAfterHours);
				}

				@Override
				public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {
					if (reqId == getPendingReqId()) {
						final OHLCPoint ohlc = new OHLCPoint(BarSize.FIVE_SECS, new Date((long)time * 1000L), open, high, low, close, volume, wap, count);
						threadPool.execute(new Runnable() {
							@Override
							public void run() {
								for (IRealTimeDataListener listener : currentListeners) {
									try {
										listener.onRealTimeBar(ohlc);
									} catch (Throwable t) {
										logger.log(Level.SEVERE, "Excetion while dispatching realtime bar", t);
									}
								}
							}});
					}
				}
			};

			connection = getConnection();
			connection.addClient(realTimeDataClient);
		}
		
		setOfListeners.add(listener);
		
		if (newRequest) {
			realTimeDataClient.sendRequest(requestSequence.getAndIncrement());
		}
	}

	@Override
	public void stopRealTimeBars(IContract contract, BarSize barSize, DataType dataType, boolean includeAfterHours, IRealTimeDataListener listener) throws ConnectException {
		String realTimeSeriesRequestCode = buildRealTimeSeriesRequestCode(contract, barSize, dataType, includeAfterHours);
		
		Set<IRealTimeDataListener> setOfListeners = realTimeDataListeners.get(realTimeSeriesRequestCode);
		
		if (setOfListeners != null) {
			setOfListeners.remove(listener);
			
			if (setOfListeners.isEmpty()) {
				realTimeDataListeners.remove(setOfListeners);
				Integer requestId = realTimeDataRequestIds.remove(realTimeSeriesRequestCode);
				getConnection().getSender().cancelRealTimeBars(requestId);
			}
		}
	}
	
	private String buildTickRequestCode(IContract contract) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(contract.getSymbol()).append("|");
		buffer.append(contract.getCurrency().getCurrencyCode()).append("|");
		buffer.append(contract.getSecurityType().name());
		return buffer.toString();
	}

	@Override
	public void startTicks(IContract contract, ITickListener listener) throws ConnectException, RequestFailedException {
		final Contract iBContract = IBContract.toIBContract(contract, constantTranslator);
		
		final String tickRequestCode = buildTickRequestCode(contract);
		
		Set<ITickListener> setOfListeners = tickListeners.get(tickRequestCode);
		
		boolean newRequest = false;
		IBConnection connection = null;
		IBClient tickClient = null;
		
		if (setOfListeners == null || setOfListeners.isEmpty()) {
			newRequest = true;
			setOfListeners = new CopyOnWriteArraySet<ITickListener>();
			tickListeners.put(tickRequestCode, setOfListeners);
			
			final Set<ITickListener> currentListeners = setOfListeners; // duplicate as final variable to make it valid for use by the inner class
			tickClient = new IBClient() {
				private Double lastTradePrice;
				private Double lastAskPrice;
				private Double lastBidPrice;
				
				@Override
				public void doSendRequest() {
					int requestId = getPendingReqId();
					tickRequestIds.put(tickRequestCode, requestId);
					getSender().reqMktData(getPendingReqId(), iBContract, "233", false);
				}

				@Override
				public void tickSize(int reqId, int field, int size) {
					if (reqId == getPendingReqId()) {
						Date timestamp = new Date();
						ITickPoint tick = null;
						switch (field) {
						case TickType.ASK_SIZE:
							if (lastAskPrice != null) {
								tick = new TickPoint(timestamp, DataType.ASK, lastAskPrice, size);
							}
							break;
						case TickType.BID_SIZE:
							if (lastBidPrice != null) {
								tick = new TickPoint(timestamp, DataType.BID, lastBidPrice, size);
							}
							break;
						case TickType.LAST_SIZE:
							if (lastTradePrice != null) {
								tick = new TickPoint(timestamp, DataType.TRADES, lastTradePrice, size);
							}
							break;
						case TickType.VOLUME:
							// not enough information to be useful
						}
						if (tick != null) {
							final ITickPoint constantTick = tick;
							threadPool.execute(new Runnable() {
								@Override
								public void run() {
									for (ITickListener listener : currentListeners) {
										try {
											listener.onTick(constantTick);
										} catch (Throwable t) {
											logger.log(Level.SEVERE, "Excetion while dispatching size tick", t);
										}
									}
								}});
						}
					}
				}

				@Override
				public void tickPrice(int reqId, int field, double price, int canAutoExecute) {
					if (reqId == getPendingReqId()) {
						Date timestamp = new Date();
						ITickPoint tick = null;
						ITickPoint midTick = null;
						switch (field) {
						case TickType.ASK:
							lastAskPrice = price;
							tick = new TickPoint(timestamp, DataType.ASK, lastAskPrice, 0);
							midTick = new TickPoint(timestamp, DataType.MIDPOINT, lastBidPrice != null ? (lastBidPrice + lastAskPrice) / 2 : lastAskPrice, 0);
							break;
						case TickType.BID:
							lastBidPrice = price;
							tick = new TickPoint(timestamp, DataType.BID, lastBidPrice, 0);
							midTick = new TickPoint(timestamp, DataType.MIDPOINT, lastAskPrice != null ? (lastBidPrice + lastAskPrice) / 2 : lastBidPrice, 0);
							break;
						case TickType.LAST:
						case TickType.OPEN:
						case TickType.CLOSE:
							tick = new TickPoint(timestamp, DataType.TRADES, price, 0);
							lastTradePrice = price;
							break;
						}
						if (tick != null) {
							final ITickPoint constantTick = tick;
							final ITickPoint constantMidTick = midTick;
							threadPool.execute(new Runnable() {
								@Override
								public void run() {
									for (ITickListener listener : currentListeners) {
										listener.onTick(constantTick);
										if (constantMidTick != null) {
											try {
												listener.onTick(constantMidTick);
											} catch (Throwable t) {
												logger.log(Level.SEVERE, "Excetion while dispatching size tick", t);
											}
										}
									}
								}});
						}
					}
				}
				
				@Override
				public void tickString(int reqId, int tickType, String value) {
//					if (reqId == getPendingReqId()) {
//						switch (tickType) {
//						case TickType.RT_VOLUME: //value example: "1307.50;17;1337617022706;1271944;1300.6509583;false"
//							double price;
//							int size;
//							Date timestamp;
//							String[] tokens = value.split(";");
//							if (tokens.length >= 6) {
//								price = Double.valueOf(tokens[0]);
//								size = Integer.valueOf(tokens[1]);
//								timestamp = new Date(Long.valueOf(tokens[2]));
//							}
//						}
//					}
				}
				
				@Override
				public void tickGeneric(int tickerId, int tickType, double value) {
					logger.log(Level.WARNING, "Generic tick received from IB: " + EWrapperMsgGenerator.tickGeneric(tickerId, tickType, value).toString());
				}
			};

			connection = getConnection();
			connection.addClient(tickClient);
		}
		
		setOfListeners.add(listener);
		
		if (newRequest) {
			tickClient.sendRequest(requestSequence.getAndIncrement());
		}
	}

	@Override
	public void stopTicks(IContract contract, ITickListener listener) throws ConnectException {
		String tickRequestCode = buildTickRequestCode(contract);
		
		Set<ITickListener> setOfListeners = tickListeners.get(tickRequestCode);
		
		if (setOfListeners != null) {
			setOfListeners.remove(listener);
			
			if (setOfListeners.isEmpty()) {
				tickListeners.remove(setOfListeners);
				Integer requestId = tickRequestIds.remove(tickRequestCode);
				getConnection().getSender().cancelMktData(requestId);
			}
		}
	}

	public int nextOrderId() throws ConnectException, RequestFailedException {
		if (nextOrderId == -1) {
			IBClient orderIdClient = new IBClient() {
				@Override
				public void nextValidId(int orderId) {
					logger.log(Level.INFO, "Received order id: " + orderId);
					nextOrderId = orderId;
					setRequestComplete();
				}

				@Override
				protected void doSendRequest() {
					logger.log(Level.INFO, "Request order id");
					getSender().reqIds(1);
				}
			};
			
			IBConnection connection = getConnection();
			connection.addClient(orderIdClient);
			orderIdClient.sendRequest(requestSequence.getAndIncrement());
			try {
				handleRequest(orderIdClient);
			} finally {
				connection.removeClient(orderIdClient);
			}
		}
		return nextOrderId++;
	}

	private int sendOrder(final Order iBOrder, final Contract iBContract) throws ConnectException, RequestFailedException {
		final int id = nextOrderId();
		getConnection().getSender().placeOrder(id, iBContract, iBOrder);
		return id;
	}

	@Override
	public String sendOrder(IOrder order) throws ConnectException, RequestFailedException {
		Order iBOrder = IBOrder.toIBOrder(order, constantTranslator);
		Contract iBContract = IBContract.toIBContract(order.getContract(), constantTranslator);
		int id = sendOrder(iBOrder, iBContract);
		iBOrder.m_orderId = id;
		sentOrders.put(id, new IBOrder(order.getContract(), iBOrder, constantTranslator));
		logger.log(Level.INFO, "Sent order: " + id);
		return Integer.toString(id);
	}
	
	@Override
	public String[] sendBracketOrders(IOrder parent, IOrder[] children) throws ConnectException, RequestFailedException {
		Order iBParentOrder = IBOrder.toIBOrder(parent, constantTranslator);
		Contract iBParentContract = IBContract.toIBContract(parent.getContract(), constantTranslator);
		
		int parentId = sendOrder(iBParentOrder, iBParentContract);
		
		String[] ids = new String[1 + children.length];
		ids[0] = Integer.toString(parentId);
		
		for (int i = 0; i < children.length; i++) {
			Order iBChildOrder = IBOrder.toIBOrder(children[i], constantTranslator);
			iBChildOrder.m_parentId = parentId;
			Contract iBChildContract = IBContract.toIBContract(children[i].getContract(), constantTranslator);
			int childId = sendOrder(iBChildOrder, iBChildContract);
			ids[i + 1] = Integer.toString(childId);
		}
		return ids;
	}

	@Override
	public void addOrderStatusListener(IOrderStatusListener listener) throws ConnectException {
		orderStatusListeners.add(listener);
		if (orderStatusClient == null) {
			orderStatusClient = new OrderStatusClient();
			getConnection().addClient(orderStatusClient);
		}
	}

	@Override
	public void removeOrderStatusListener(IOrderStatusListener listener) throws ConnectException {
		orderStatusListeners.remove(listener);
		if (orderStatusListeners.isEmpty() && orderStatusClient != null) {
			getConnection().removeClient(orderStatusClient);
			orderStatusClient = null;
		}
	}

	@Override
	public void addPositionListener(IPositionListener listener) throws ConnectException {
		positionListeners.add(listener);
		if (positionClient == null) {
			positionClient = new PositionClient();
			IBConnection connection = getConnection();
			connection.addClient(positionClient);
			connection.getSender().reqAccountUpdates(true, accountId);
		}
	}

	@Override
	public void removePositionListener(IPositionListener listener) throws ConnectException {
		positionListeners.remove(listener);
		if (positionClient != null) {
			IBConnection connection = getConnection();
			connection.getSender().reqAccountUpdates(false, accountId);
			connection.removeClient(positionClient);
			positionClient.stopPositionPriceUpdates();
			positionClient = null;
		}
	}
	
	private boolean waitResponse(IBClient requestClient) {
		for (int wait = 0; wait < maxWaitResponseMs && requestClient.isRequestInProgress(); wait += HANDLE_REQUEST_SLEEP_QUANTUM) {
			try {
				Thread.sleep(HANDLE_REQUEST_SLEEP_QUANTUM);
			} catch (InterruptedException e) {
			}
		}
		return requestClient.isRequestInProgress() == false && requestClient.hasErrors() == false;
	}

	private void handleRequest(IBClient requestClient) throws RequestFailedException, PacingViolationException {
		if (!waitResponse(requestClient)) {
			if (requestClient.hasErrors()) {
				if (requestClient.getErrorMessage().contains("pacing violation")) {
					throw new PacingViolationException(requestClient.getErrorMessage(), requestClient.getException());
				} else if (requestClient.getErrorMessage().contains("query returned no data")) {
					throw new NoDataReturnedException(requestClient.getErrorMessage(), requestClient.getException());
				}
				throw new RequestFailedException(requestClient.getErrorMessage(), requestClient.getException());
			}
			throw new RequestFailedException();
		}
	}

	public long getMinElapsedBetweenHistDataReqBatches() {
		return minElapsedBetweenHistDataReqBatches;
	}

	public void setMinElapsedBetweenHistDataReqBatches(long minElapsedBetweenHistDataReqBatches) {
		this.minElapsedBetweenHistDataReqBatches = minElapsedBetweenHistDataReqBatches;
	}

	public int getNumOfReqsInHistDataReqBatches() {
		return numOfReqsInHistDataReqBatches;
	}

	public void setNumOfReqsInHistDataReqBatches(int numOfReqsInHistDataReqBatches) {
		this.numOfReqsInHistDataReqBatches = numOfReqsInHistDataReqBatches;
	}

	public int getMaxHistDataPeriodMs() {
		return minHistDataPeriodMs;
	}

	public void setMinHistDataPeriodMs(int minHistDataPeriodMs) {
		this.minHistDataPeriodMs = minHistDataPeriodMs;
	}

	public int getMaxBarPerRequest() {
		return maxBarPerRequest;
	}

	public void setMaxBarPerRequest(int maxBarPerRequest) {
		this.maxBarPerRequest = maxBarPerRequest;
	}

	public int getMaxWaitToConnectMs() {
		return maxWaitToConnectMs;
	}

	public void setMaxWaitToConnectMs(int maxWaitToConnectMs) {
		this.maxWaitToConnectMs = maxWaitToConnectMs;
	}

	public int getMaxWaitResponseMs() {
		return maxWaitResponseMs;
	}

	public void setMaxWaitResponseMs(int maxWaitResponseMs) {
		this.maxWaitResponseMs = maxWaitResponseMs;
	}
}
