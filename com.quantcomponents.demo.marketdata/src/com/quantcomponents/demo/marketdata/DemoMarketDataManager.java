package com.quantcomponents.demo.marketdata;

import java.util.Map;

import com.quantcomponents.core.utils.HostUtils;
import com.quantcomponents.marketdata.IRealTimeMarketDataManager;
import com.quantcomponents.marketdata.IStockDatabaseContainerFactory;
import com.quantcomponents.marketdata.RealTimeMarketDataManager;

public class DemoMarketDataManager extends RealTimeMarketDataManager implements IRealTimeMarketDataManager {
		private static final String PRETTY_NAME = "Demo@" + HostUtils.hostname();
		private static final String DEMO_DB_ID = "demo";
		private volatile IStockDatabaseContainerFactory stockDatabaseContainerFactory;
		
		public void setStockDatabaseContainerFactory(IStockDatabaseContainerFactory stockDatabaseContainerFactory) {
			this.stockDatabaseContainerFactory = stockDatabaseContainerFactory;
		}
			
		public void activate(Map<?,?> properties) throws Exception {
			setMarketDataProvider(new SimulatedRealTimeMarketDataProvider());
			setStockDatabaseContainer(stockDatabaseContainerFactory.getInstance(DEMO_DB_ID));
		}

		@Override
		public String getPrettyName() {
			return PRETTY_NAME;
		}
	}