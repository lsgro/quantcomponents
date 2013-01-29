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

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.quantcomponents.core.model.BareDate;

public class XmlBareDateAdapter extends XmlAdapter<String, BareDate> {

	@Override
	public BareDate unmarshal(String v) throws Exception {
		return new BareDate(v);
	}

	@Override
	public String marshal(BareDate v) throws Exception {
		return v.toString();
	}

}
