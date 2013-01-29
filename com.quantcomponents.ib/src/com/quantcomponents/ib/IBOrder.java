/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ib;

import com.ib.client.Order;
import com.quantcomponents.algo.IOrder;
import com.quantcomponents.algo.OrderBean;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.OrderSide;
import com.quantcomponents.core.model.OrderType;

public class IBOrder implements IOrder {
	private final Order iBOrder;
	private final IBConstantTranslator constantTranslator;
	private IContract contract;
	
	public IBOrder(IContract contract, Order iBOrder, IBConstantTranslator constantTranslator) {
		this.contract = contract;
		this.iBOrder = iBOrder;
		this.constantTranslator = constantTranslator;
	}

	public IBOrder(IContract contract, IBConstantTranslator constantTranslator) {
		this(contract, new Order(), constantTranslator);
	}

	public static Order toIBOrder(IOrder order, IBConstantTranslator constantTranslator) {
		if (order instanceof IBOrder) {
			return ((IBOrder) order).iBOrder;
		} else {
			IBOrder iBOrder = new IBOrder(null, new Order(), constantTranslator);
			iBOrder.setAmount(order.getAmount());
			iBOrder.setSide(order.getSide());
			iBOrder.setType(order.getType());
			iBOrder.setAuxPrice(iBOrder.getAuxPrice());
			if (OrderType.LIMIT.equals(iBOrder.getType())) {
				iBOrder.setLimitPrice(order.getLimitPrice());
			}
		return iBOrder.iBOrder;
		}
	}

	@Override
	public String getId() {
		return Integer.toString(iBOrder.m_orderId);
	}
	
	protected void setId(String id) {
		iBOrder.m_orderId = Integer.valueOf(id);
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
		return constantTranslator.getOrderSide(iBOrder.m_action);
	}
	
	public void setSide(OrderSide side) {
		if (side == null) {
			iBOrder.m_action = null;
		} else {
			iBOrder.m_action = constantTranslator.getCode(side);
		}
	}

	public void setOCAGroup(String ocaGroup) {
		iBOrder.m_ocaGroup = ocaGroup;
	}
	
	@Override
	public OrderType getType() {
		return constantTranslator.getOrderType(iBOrder.m_orderType);
	}
	
	public void setType(OrderType type) {
		if (type == null) {
			iBOrder.m_orderType = null;
		} else {
			iBOrder.m_orderType = constantTranslator.getCode(type);
		}
	}

	@Override
	public int getAmount() {
		return iBOrder.m_totalQuantity;
	}

	public void setAmount(int amount) {
		iBOrder.m_totalQuantity = amount;
	}
	
	@Override
	public Double getLimitPrice() {
		if (OrderType.LIMIT.equals(getType())) {
			return iBOrder.m_lmtPrice;
		} else {
			return null;
		}
	}

	public void setLimitPrice(double limitPrice) {
		if (OrderType.LIMIT.equals(getType())) {
			iBOrder.m_lmtPrice = limitPrice;
		} else {
			throw new IllegalArgumentException("Limit price can only be set on a LIMIT order");
		}
	}

	@Override
	public Double getAuxPrice() {
		return iBOrder.m_auxPrice;
	}
	
	public void setAuxPrice(double auxPrice) {
		iBOrder.m_auxPrice = auxPrice;
	}
	
	@Override
	public String toString() {
		return OrderBean.stringRepr(this);
	}

}
