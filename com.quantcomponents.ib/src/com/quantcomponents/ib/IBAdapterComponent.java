package com.quantcomponents.ib;

import java.net.ConnectException;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.ConfigurationException;

import com.quantcomponents.algo.IExecutionService;
import com.quantcomponents.algo.IOrder;
import com.quantcomponents.algo.IOrderStatusListener;
import com.quantcomponents.algo.IPositionListener;
import com.quantcomponents.algo.ITrade;
import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.core.utils.HostUtils;
import com.quantcomponents.marketdata.IRealTimeMarketDataManager;
import com.quantcomponents.marketdata.IStockDatabaseContainerFactory;
import com.quantcomponents.marketdata.RealTimeMarketDataManager;

public class IBAdapterComponent extends RealTimeMarketDataManager implements IRealTimeMarketDataManager, IExecutionService {
	private static final Logger logger = Logger.getLogger(IBAdapterComponent.class.getName());
	private static final String PRETTY_NAME = "InteractiveBrokers@" + HostUtils.hostname();
	private static final String IB_DB_ID = "ib";
	public static final String HOST_KEY = "host";
	public static final String PORT_KEY = "port";
	public static final String CLIENT_ID_KEY = "clientId";
	public static final String ACCOUNT_ID_KEY = "accountId";
	public static final String FIRST_REQUEST_NO_KEY = "firstRequestNo";
	public static final String NO_MKT_DATA_LINES_KEY = "noMktDataLinesKey";
	
	private volatile IStockDatabaseContainerFactory stockDatabaseContainerFactory;	
	private volatile IBAdapter ibAdapter;

	public void setStockDatabaseContainerFactory(IStockDatabaseContainerFactory stockDatabaseContainerFactory) {
		this.stockDatabaseContainerFactory = stockDatabaseContainerFactory;
	}
	
	public void activate(Map<?,?> properties) throws Exception {
		logger.log(Level.INFO, "Received new configuration: ");
		for (Object key : properties.keySet()) {
			logger.log(Level.INFO, key + " -> " + properties.get(key));
		}

		String host = (String) properties.get(HOST_KEY);
		if (host == null) {
			throw new ConfigurationException("Property not set: " + HOST_KEY);
		}

		Object portValue = properties.get(PORT_KEY);
		if (portValue == null) {
			throw new ConfigurationException("Property not set: " + PORT_KEY);
		}
		Integer port = parsePropertyValue(portValue);

		Object clientIdValue = properties.get(CLIENT_ID_KEY);
		if (clientIdValue == null) {
			throw new ConfigurationException("Property not set: " + CLIENT_ID_KEY);
		}
		Integer clientId = parsePropertyValue(clientIdValue);

		String accountId = (String) properties.get(ACCOUNT_ID_KEY);
		if (accountId == null) {
			throw new ConfigurationException("Property not set: " + ACCOUNT_ID_KEY);
		}

		Object firstRequestNoValue = properties.get(FIRST_REQUEST_NO_KEY);
		if (firstRequestNoValue == null) {
			firstRequestNoValue = 0;
		}
		Integer firstRequestNo = parsePropertyValue(firstRequestNoValue);

		Object noMktDataLinesKeyValue = properties.get(NO_MKT_DATA_LINES_KEY);
		if (noMktDataLinesKeyValue == null) {
			noMktDataLinesKeyValue = 0;
		}
		Integer noMktDataLinesKey = parsePropertyValue(noMktDataLinesKeyValue);
	
		ibAdapter = new IBAdapter(host, port, clientId, firstRequestNo, noMktDataLinesKey, accountId);
		setMarketDataProvider(ibAdapter);
		setStockDatabaseContainer(stockDatabaseContainerFactory.getInstance(IB_DB_ID));
	}
	
	public void deactivate() {
		ibAdapter.disconnect();
	}
		
	private Integer parsePropertyValue(Object propertyValue) {
		if (propertyValue instanceof Integer) {
			return (Integer) propertyValue;
		} else if (propertyValue instanceof String) {
			String str = (String) propertyValue;
			return Integer.parseInt(str);
		}
		return null;
	}
	
	@Override
	public DataType[] availableDataTypes() {
		return ibAdapter.availableDataTypes();
	}

	@Override
	public BarSize[] availableBarSizes() {
		return ibAdapter.availableBarSizes();
	}

	@Override
	public Deque<ITrade> getTrades() throws ConnectException, RequestFailedException {
		return ibAdapter.getTrades();
	}

	@Override
	public List<IContract> searchContracts(IContract criteria, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		return ibAdapter.searchContracts(criteria, taskMonitor);
	}

	@Override
	public String sendOrder(IOrder order) throws ConnectException, RequestFailedException {
		return ibAdapter.sendOrder(order);
	}

	@Override
	public String[] sendBracketOrders(IOrder parent, IOrder[] children) throws ConnectException, RequestFailedException {
		return ibAdapter.sendBracketOrders(parent, children);
	}

	@Override
	public void addOrderStatusListener(IOrderStatusListener listener) throws ConnectException {
		ibAdapter.addOrderStatusListener(listener);
	}

	@Override
	public void removeOrderStatusListener(IOrderStatusListener listener) throws ConnectException {
		ibAdapter.removeOrderStatusListener(listener);
	}

	@Override
	public void addPositionListener(IPositionListener listener) throws ConnectException {
		ibAdapter.addPositionListener(listener);
	}

	@Override
	public void removePositionListener(IPositionListener listener) throws ConnectException {
		ibAdapter.removePositionListener(listener);
	}

	@Override
	public String getPrettyName() {
		return PRETTY_NAME;
	}
}
