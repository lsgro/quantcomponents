package com.quantcomponents.yahoo;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.ConnectException;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

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
	private static final Logger logger = Logger.getLogger("YahooFinanceAdapterComponent");
	private static final String YAHOO_TICKER_QUERY_URL = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=%s&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
	private static final String YAHOO_STOCK_QUERY_URL = "http://finance.yahoo.com/q?s=%s";
	private static final String YAHOO_SYMBOL_KEY = "symbol";
	private static final String YAHOO_EXCHANGE_KEY = "exchDisp";
	private static final String YAHOO_TYPE_KEY = "typeDisp";
	private static final String YAHOO_DESCRIPTION_KEY = "name";
	private static final String YAHOO_BROKER_ID = "Yahoo!";
	
	private final Pattern STOCK_CURRENCY_PATTERN = Pattern.compile(".*Currency in (...)\\..*", Pattern.DOTALL);

	@Override
	public DataType[] availableDataTypes() {
		return new DataType[] {
			DataType.MIDPOINT};
	}

	@Override
	public BarSize[] availableBarSizes() {
		return new BarSize[] {
			BarSize.ONE_DAY,
			BarSize.ONE_WEEK,
			BarSize.ONE_MONTH};
	}

	@Override
	public List<IContract> searchContracts(IContract criteria, ITaskMonitor taskMonitor) throws ConnectException, RequestFailedException {
		String symbol = criteria.getSymbol();
		if (symbol == null || symbol.trim().length() == 0) {
			throw new RequestFailedException("Symbol must be speficied for Yahoo! Finance ticker query");
		}
		String queryUrl = String.format(YAHOO_TICKER_QUERY_URL, symbol);
		JSON response = null;
		try {
			String responseString = httpQuery(queryUrl);
			String jsonResponse = responseString.replace("YAHOO.Finance.SymbolSuggest.ssCallback(", "").replace(")","");
			Reader responseReader = new StringReader(jsonResponse);
			response = JSON.parse(responseReader); 
		} catch (IOException e) {
			throw new ConnectException("Exception while connecting to: " + queryUrl + " [" + e.getMessage() + "]");
		} catch (JSONException e) {
			throw new RequestFailedException("Exception parsing response data from: " + queryUrl, e);
		}
		@SuppressWarnings("unchecked")
		List<JSON> securityList = (List<JSON>) response.get("ResultSet").get("Result").getValue();
		List<IContract> contractList = new LinkedList<IContract>();
		for (JSON security : securityList) {
			Currency stockCurrency = null;
			try {
				StockInfo stockInfo = getStockInfo(symbol);
				stockCurrency = stockInfo.currency;
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Exception while querying currency for: " + symbol, e);
				continue;
			}
			if (criteria.getCurrency() != null && !criteria.getCurrency().equals(stockCurrency)) {
				continue;
			}
			ContractBean contract = new ContractBean();
			contract.setSymbol((String) security.get(YAHOO_SYMBOL_KEY).getValue());
			contract.setExchange((String) security.get(YAHOO_EXCHANGE_KEY).getValue());
			contract.setSecurityType(convertSecurityType((String) security.get(YAHOO_TYPE_KEY).getValue()));
			contract.setCurrency(stockCurrency);
			ContractDescBean description = new ContractDescBean();
			description.setLongName((String) security.get(YAHOO_DESCRIPTION_KEY).getValue());
			description.setTimeZone(TimeZone.getDefault()); // I can't find a reliable source on Yahoo!Finance. A table indexed by exchange?.. Yeah! Why don't you do it? This is a list: http://finance.yahoo.com/exchanges
			contract.setContractDescription(description);
			contract.setBrokerID(YAHOO_BROKER_ID);
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
	
	private static class StockInfo {
		StockInfo(Currency currency) {
			this.currency = currency;
		}
		Currency currency;
	}

	private StockInfo getStockInfo(String symbol) throws HttpException, IOException {
		String queryUrl = String.format(YAHOO_STOCK_QUERY_URL, symbol);
		String responseString = httpQuery(queryUrl);
		Matcher m = STOCK_CURRENCY_PATTERN.matcher(responseString);
		if (!m.matches()) {
			throw new IOException("HTTP response doesn't match currency pattern: " + responseString);
		}
		String currencyCode = m.group(1);
		return new StockInfo(Currency.getInstance(currencyCode));
	}
	
	private String httpQuery(String url) throws HttpException, IOException {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(method);
        if (statusCode != HttpStatus.SC_OK) {
          throw new ConnectException("Query to " + url + " failed [" + method.getStatusLine() + "]");
        }
		byte[] responseBody = method.getResponseBody();
		return new String(responseBody);
	}
}
