package com.quantcomponents.yahoo;

import java.net.ConnectException;
import java.util.Date;
import java.util.List;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.marketdata.IMarketDataProvider;
import com.quantcomponents.marketdata.IOHLCPoint;

public class YahooFinanceAdapterComponent implements IMarketDataProvider {

	@Override
	public List<IContract> searchContracts(IContract criteria,
			ITaskMonitor taskMonitor) throws ConnectException,
			RequestFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IOHLCPoint> historicalBars(IContract contract,
			Date startDateTime, Date endDateTime, BarSize barSize,
			DataType dataType, boolean includeAfterHours,
			ITaskMonitor taskMonitor) throws ConnectException,
			RequestFailedException {
		// TODO Auto-generated method stub
		return null;
	}

}
