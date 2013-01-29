/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.model;

/**
 * Implementors of this interface have a unique ID that can be used to persist
 * objects between runtimes
 *
 */
public interface IPersistentIdentifiable {
	/**
	 * A unique ID to persist objects between several runs or different runtimes via serialization
	 * The ID should be unique among values representing the same object, e.g. a time series
	 * @return A unique ID
	 */
	String getPersistentID();
}
