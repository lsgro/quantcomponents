package com.quantcomponents.algo;

/**
 * Listener to order status.
 * Used to monitor the state changes of an order. All the  methods except
 * {@link IOrderStatusListener#onOrderFilled(String, int, boolean, double)} are optional,
 * depending on which functionality is implementsd by the broker/interface to the market.
 */
public interface IOrderStatusListener {
	/**
	 * Optionally called by the execution service when an order has been submitted
	 * @param orderId ID of the submitted order
	 * @param active true if the order is active on the broker platform, false otherwise
	 */
	void onOrderSubmitted(String orderId, boolean active);
	/**
	 * Called by the execution service when an order has been filled
	 * @param orderId ID of the order filled
	 * @param filled the amount filled
	 * @param full true if the order has been filled completely
	 * @param averagePrice the average price of execution of the amount filled
	 */
	void onOrderFilled(String orderId, int filled, boolean full, double averagePrice);
	/**
	 * Optionally called by the execution service when an order has been cancelled
	 * @param orderId ID of the order cancelled
	 */
	void onOrderCancelled(String orderId);
	/**
	 * Optionally called by the execution service for status changes other than the
	 * previous ones
	 * @param orderId ID of the order that has changed status
	 * @param status value of the new status
	 */
	void onOrderStatus(String orderId, String status);
}