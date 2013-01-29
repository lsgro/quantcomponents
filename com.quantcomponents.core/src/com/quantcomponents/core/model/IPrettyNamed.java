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
 * Implementors of this interface, tipically service providers,
 * can be displayed to users with a compact
 * name that tells the type and location of the object.
 *
 */
public interface IPrettyNamed {
	/**
	 * A compact name of the object, including type and location, to be shown to users
	 * It should strive to be unique in a distributed implementation
	 * @return A compact name of the implementor instance
	 */
	String getPrettyName();
}
