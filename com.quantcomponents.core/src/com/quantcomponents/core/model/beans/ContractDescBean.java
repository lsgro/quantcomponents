/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.model.beans;

import java.io.Serializable;
import java.util.TimeZone;

import com.quantcomponents.core.model.IContractDesc;

/**
 * 
 * Bean for {@link com.quantcomponents.core.model.IContractDesc}
 */
public class ContractDescBean implements IContractDesc, Serializable {
	private static final long serialVersionUID = -7641452437441281967L;
	private String longName;
	private TimeZone timeZone;
	
	public static ContractDescBean copyOf(IContractDesc desc) {
		ContractDescBean bean = new ContractDescBean();
		bean.setLongName(desc.getLongName());
		bean.setTimeZone(desc.getTimeZone());
		return bean;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
}
