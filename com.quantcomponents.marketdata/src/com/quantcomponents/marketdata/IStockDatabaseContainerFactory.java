package com.quantcomponents.marketdata;

public interface IStockDatabaseContainerFactory {
	IStockDatabaseContainer getInstance(String id) throws Exception;
}
