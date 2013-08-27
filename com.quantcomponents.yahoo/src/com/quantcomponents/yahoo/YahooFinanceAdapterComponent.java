package com.quantcomponents.yahoo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.core.model.SecurityType;
import com.quantcomponents.core.model.beans.ContractBean;
import com.quantcomponents.core.model.beans.ContractDescBean;
import com.quantcomponents.marketdata.IMarketDataProvider;
import com.quantcomponents.marketdata.IOHLCPoint;

public class YahooFinanceAdapterComponent implements IMarketDataProvider {
	private static final String YAHOO_TICKER_QUERY_URL = "http://d.yimg.com/autoc.finance.yahoo.com/autoc";
	private static final String YAHOO_TICKER_QUERY_ARGS = "?query=%s&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
	private static final int YAHOO_PORT = 80;
	private static final String YAHOO_SYMBOL_KEY = "symbol";
	private static final String YAHOO_EXCHANGE_KEY = "exchDisp";
	private static final String YAHOO_TYPE_KEY = "typeDisp";
	private static final String YAHOO_DESCRIPTION_KEY = "name";

	@Override
	public List<IContract> searchContracts(IContract criteria, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		String symbol = criteria.getSymbol();
		if (symbol == null || symbol.trim().length() == 0) {
			throw new RequestFailedException("Symbol must be speficied for Yahoo! Finance ticker query");
		}
		String tickerQueryArgs = String.format(YAHOO_TICKER_QUERY_ARGS, symbol);
		String tickerQueryURLWithArgs = YAHOO_TICKER_QUERY_URL + tickerQueryArgs;
		JSON response = null;
		try {
			Socket yahooConnection = new Socket(tickerQueryURLWithArgs, YAHOO_PORT);
			Reader responseReader = new InputStreamReader(yahooConnection.getInputStream());
			response = JSON.parse(responseReader); 
		} catch (IOException e) {
			throw new ConnectException("Exception while connecting to: " + tickerQueryURLWithArgs + "[" + e.getMessage() + "]");
		} catch (JSONException e) {
			throw new RequestFailedException("Exception querying to: " + tickerQueryURLWithArgs, e);
		}
		@SuppressWarnings("unchecked")
		List<JSON> securityList = (List<JSON>) response.get("ResultSet").get("Result");
		List<IContract> contractList = new LinkedList<IContract>();
		for (JSON security : securityList) {
			ContractBean contract = new ContractBean();
			contract.setSymbol((String) security.get(YAHOO_SYMBOL_KEY).getValue());
			contract.setExchange((String) security.get(YAHOO_EXCHANGE_KEY).getValue());
			contract.setSecurityType(convertSecurityType((String) security.get(YAHOO_TYPE_KEY).getValue()));
			ContractDescBean description = new ContractDescBean();
			description.setLongName((String) security.get(YAHOO_DESCRIPTION_KEY).getValue());
			contract.setContractDescription(description);
			contractList.add(contract);
		}
		return contractList;
	}

	@Override
	public List<IOHLCPoint> historicalBars(IContract contract, Date startDateTime, Date endDateTime, BarSize barSize, DataType dataType,
			boolean includeAfterHours, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private SecurityType convertSecurityType(String code) {
		if ("Equity".equals(code) || "Fund".equals(code) || "ETF".equals(code)) {
			return SecurityType.STK;
		} else if ("Future".equals(code)) {
			return SecurityType.FUT;
		} else if ("Index".equals(code)){
			return SecurityType.IND;
		} else {
			return null;
		}
	}
}
