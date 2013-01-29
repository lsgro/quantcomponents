/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.xml;

import java.util.Currency;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XmlCurrencyAdapter extends XmlAdapter<String, Currency> {

	@Override
	public Currency unmarshal(String v) throws Exception {
		return Currency.getInstance(v);
	}

	@Override
	public String marshal(Currency v) throws Exception {
		return v.getCurrencyCode();
	}

}
