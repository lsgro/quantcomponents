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

public class ExecutionCreationException extends Exception {
	private static final long serialVersionUID = -5214163927483236992L;
	public ExecutionCreationException() {
		super();
	}
	public ExecutionCreationException(String message) {
		super(message);
	}
	public ExecutionCreationException(String message, Throwable t) {
		super(message, t);
	}
}
