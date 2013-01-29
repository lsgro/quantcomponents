/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.remote;

import java.io.Serializable;


/**
 * 
 * Handle to a remote resource
 * @param <T> Type of the remote resource host
 */
public class ServiceHandle<T> implements Serializable {
	private static final long serialVersionUID = -206906935376718005L;
	private final Long uid;

	public ServiceHandle(Long uid) {
		this.uid = uid;
	}

	public String getUID() {
		return uid.toString();
	}
	
	@Override
	public String toString() {
		return "Handle [UID: " + getUID() + "]";
	}
	
	@Override
	public int hashCode() {
		return uid.hashCode();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o) {
		if (o instanceof ServiceHandle) {
			return getUID().equals(((ServiceHandle) o).getUID());
		}
		return false;
	}
}
