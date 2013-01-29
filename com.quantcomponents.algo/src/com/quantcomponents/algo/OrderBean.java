/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo;

import java.io.Serializable;

import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.OrderSide;
import com.quantcomponents.core.model.OrderType;
import com.quantcomponents.core.model.beans.ContractBean;

/**
 * Order bean
 */
public class OrderBean implements IOrder, Serializable {
	private static final long serialVersionUID = 7398136690551441727L;
	private String id;
	private IContract contract;
	private OrderSide side;
	private OrderType type;
	private int amount;
	private Double limitPrice;
	private Double auxPrice;
	
	public static OrderBean copyOf(IOrder source) {
		OrderBean copy = new OrderBean(source.getContract() == null ? null : ContractBean.copyOf(source.getContract()), source.getSide(), source.getType(), source.getAmount(), source.getLimitPrice(), source.getAuxPrice());
		copy.setId(source.getId());
		return copy;
	}

	public OrderBean(IContract contract, OrderSide side, OrderType type, int amount, Double limitPrice, Double auxPrice) {
		this.contract = contract;
		this.side = side;
		this.type = type;
		this.amount = amount;
		this.limitPrice = limitPrice;
		this.auxPrice = auxPrice;
	}
	@Override
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public IContract getContract() {
		return contract;
	}
	public void setContract(IContract contract) {
		this.contract = contract;
	}
	@Override
	public OrderSide getSide() {
		return side;
	}
	public void setSide(OrderSide side) {
		this.side = side;
	}
	@Override
	public OrderType getType() {
		return type;
	}
	public void setType(OrderType type) {
		this.type = type;
	}
	@Override
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	@Override
	public Double getLimitPrice() {
		return limitPrice;
	}
	public void setLimitPrice(Double limitPrice) {
		this.limitPrice = limitPrice;
	}
	@Override
	public Double getAuxPrice() {
		return auxPrice;
	}
	public void setAuxPrice(Double auxPrice) {
		this.auxPrice = auxPrice;
	}
	@Override
	public String toString() {
		return stringRepr(this);
	}
	
	public static String stringRepr(IOrder order) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("order [");
		buffer.append(order.getContract().toString());
		buffer.append("]; side: ");
		buffer.append(order.getSide().name());
		buffer.append("; type: ");
		buffer.append(order.getType().name());
		buffer.append("; amnt: ");
		buffer.append(order.getAmount());
		if (order.getLimitPrice() != null) {
			buffer.append("; lmt: ");
			buffer.append(order.getLimitPrice());
		}
		if (order.getAuxPrice() != null) {
			buffer.append("; aux: ");
			buffer.append(order.getAuxPrice());
		}
		return buffer.toString();
	}
	
}
