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
import java.util.UUID;

/**
 * Implementation of {@link IHierarchyItemHandle}
 */
public class HierarchyItemHandle implements IHierarchyItemHandle, Serializable {
	private static final long serialVersionUID = -4629605540067412726L;
	private final String name;
	private final String ID;
	
	public HierarchyItemHandle(String name, String ID) {
		this.name = name;
		this.ID = ID;
	}
	
	public HierarchyItemHandle(String name) {
		this(name, name + "-" + UUID.randomUUID().toString());
	}

	@Override
	public String getPrettyName() {
		return name;
	}

	@Override
	public String getPersistentID() {
		return ID;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof IHierarchyItemHandle) {
			return ((IHierarchyItemHandle) o).getPersistentID().equals(getPersistentID());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return getPersistentID().hashCode();
	}
}
