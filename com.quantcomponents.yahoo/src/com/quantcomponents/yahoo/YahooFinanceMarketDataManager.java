package com.quantcomponents.yahoo;

import java.util.Map;

import com.quantcomponents.core.utils.HostUtils;
import com.quantcomponents.marketdata.IMarketDataManager;
import com.quantcomponents.marketdata.IStockDatabaseContainerFactory;
import com.quantcomponents.marketdata.MarketDataManager;

public class YahooFinanceMarketDataManager extends MarketDataManager implements IMarketDataManager {
	private static final String PRETTY_NAME = "Yahoo!Finance@" + HostUtils.hostname();
	private static final String YAHOO_DB_ID = "yahoo";
	private volatile IStockDatabaseContainerFactory stockDatabaseContainerFactory;
	
	public void setStockDatabaseContainerFactory(IStockDatabaseContainerFactory stockDatabaseContainerFactory) {
		this.stockDatabaseContainerFactory = stockDatabaseContainerFactory;
	}
		
	public void activate(Map<?,?> properties) throws Exception {
		setMarketDataProvider(new YahooFinanceAdapterComponent());
		setStockDatabaseContainer(stockDatabaseContainerFactory.getInstance(YAHOO_DB_ID));
	}

	@Override
	public String getPrettyName() {
		return PRETTY_NAME;
	}
}
