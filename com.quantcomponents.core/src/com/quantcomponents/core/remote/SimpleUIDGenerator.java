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

import java.util.logging.Logger;

public class SimpleUIDGenerator implements IUIDGenerator {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SimpleUIDGenerator.class.getName());
	private Long nextUid = new Long(0);

	public void deactivate() {
	}
	
	@Override
	public synchronized Long nextUID() {
		return nextUid++;
	}
}
