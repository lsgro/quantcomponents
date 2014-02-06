package com.quantcomponents.demo.marketdata;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.core.model.SecurityType;
import com.quantcomponents.core.model.beans.ContractBean;
import com.quantcomponents.core.model.beans.ContractDescBean;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.IRealTimeMarketDataProvider;
import com.quantcomponents.marketdata.ITickPoint;
import com.quantcomponents.marketdata.OHLCPoint;
import com.quantcomponents.marketdata.TickPoint;

public class SimulatedRealTimeMarketDataProvider implements
		IRealTimeMarketDataProvider {

	private static final DataType[] DATA_TYPES = new DataType[] { DataType.MIDPOINT };
	private static final BarSize[] BAR_SIZES = new BarSize[] { BarSize.FIVE_SECS };
	private static final ContractBean SAMPLE_CONTRACT = new ContractBean();
	private static final List<IContract> CONTRACT_LIST = Collections.singletonList((IContract)SAMPLE_CONTRACT);
	private static final double AVERAGE_PRICE = 500.0;
	private static final double VOL_PRICE = 0.01;
	private static final double AVERAGE_VOLUME = 100;
	private static final double VOL_VOLUME = 0.5;
	private static final long SLEEP_PERIOD = 5 * 1000L;
	
	static {
		SAMPLE_CONTRACT.setSymbol("AAPL");
		SAMPLE_CONTRACT.setExchange("Nasdaq");
		SAMPLE_CONTRACT.setCurrency(Currency.getInstance("USD"));
		SAMPLE_CONTRACT.setMultiplier(1);
		SAMPLE_CONTRACT.setSecurityType(SecurityType.STK);
		ContractDescBean desc = new ContractDescBean();
		desc.setLongName("Apple Inc.");
		desc.setTimeZone(TimeZone.getDefault());
		SAMPLE_CONTRACT.setContractDescription(desc);
	}
	
	private volatile IRealTimeDataListener barListener;
	private volatile ITickListener tickListener;	
	private volatile boolean notifyBarListener;
	private volatile boolean notifyTickListener;
	private volatile boolean activated;
	
	public void activate() {
		activated = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Random random = new Random();
				double lastClose = AVERAGE_PRICE;
				while (activated) {
					IOHLCPoint point = generateRandomPoint(random, lastClose, VOL_PRICE, AVERAGE_VOLUME, VOL_VOLUME, new Date()); 
					lastClose = point.getClose();
					if (notifyBarListener) {
						try {
							barListener.onRealTimeBar(point);
						} catch (Throwable t) {
							// burp!
						}
					}
					if (notifyTickListener) {
						ITickPoint tick = new TickPoint(point.getIndex(), DATA_TYPES[0], point.getClose(), point.getVolume().intValue());
						try {
							tickListener.onTick(tick);;
						} catch (Throwable t) {
							// burp!
						}
					}
					try {
						Thread.sleep(SLEEP_PERIOD);
					} catch (Throwable t) {
						// burp!
					}
				}
			}}).start();
		
	}
	
	public void deactivate() {
		activated = false;
	}
	
	@Override
	public DataType[] availableDataTypes() {
		return DATA_TYPES;
	}

	@Override
	public BarSize[] availableBarSizes() {
		return BAR_SIZES;
	}

	@Override
	public List<IContract> searchContracts(IContract criteria,
			ITaskMonitor taskMonitor) throws ConnectException,
			RequestFailedException {
		return CONTRACT_LIST;
	}

	@Override
	public List<IOHLCPoint> historicalBars(IContract contract,
			Date startDateTime, Date endDateTime, BarSize barSize,
			DataType dataType, boolean includeAfterHours,
			ITaskMonitor taskMonitor) throws ConnectException,
			RequestFailedException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDateTime);
		Random random = new Random();
		List<IOHLCPoint> points = new LinkedList<IOHLCPoint>();
		double lastClose = AVERAGE_PRICE;
		for (Date t = cal.getTime(); t.before(endDateTime); t = new Date(t.getTime() + barSize.getDurationInMs())) {
			IOHLCPoint point = generateRandomPoint(random, lastClose, VOL_PRICE, AVERAGE_VOLUME, VOL_VOLUME, t); 
			points.add(point);
			lastClose = point.getClose();
		}
		return points;
	}

	@Override
	public void startRealTimeBars(IContract contract, BarSize barSize,
			DataType dataType, boolean includeAfterHours,
			IRealTimeDataListener listener) throws ConnectException,
			RequestFailedException {
		barListener = listener;
		notifyBarListener = true;
	}

	@Override
	public void stopRealTimeBars(IContract contract, BarSize barSize,
			DataType dataType, boolean includeAfterHours,
			IRealTimeDataListener listener) throws ConnectException {
		notifyBarListener = false;
	}

	@Override
	public void startTicks(IContract contract, ITickListener listener)
			throws ConnectException, RequestFailedException {
		tickListener = listener;
		notifyTickListener = true;
	}

	@Override
	public void stopTicks(IContract contract, ITickListener listener)
			throws ConnectException {
		notifyTickListener = false;
	}
	
	private OHLCPoint generateRandomPoint(Random random, double lastValue, double volPrice, double avgVolume, double volVolume, Date date) {
		double[] deltas = new double[]{
				nextGaussian(random) * volPrice,
				nextGaussian(random) * volPrice,
				nextGaussian(random) * volPrice
		};
		Arrays.sort(deltas);
		double open = lastValue;
		double high = lastValue + deltas[2];
		double low = lastValue + deltas[0];
		double close = lastValue + deltas[1];
		long volume = (long)(avgVolume + nextGaussian(random) * volVolume);
		return new OHLCPoint(BAR_SIZES[0], date, open, high, low, close, volume, 0.0, 0);
	}
	
	private double nextGaussian(Random random) { // emergency approx of N ^ -1
		return Math.tan((random.nextDouble()-.5)*3.14)/24;
	}
}
