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

import java.util.TimeZone;

/**
 * Longer description for financial contracts
 *
 */
public interface IContractDesc {
	/**
	 * Long name of the contract
	 */
	String getLongName();
	/**
	 * Timezone of the primary trading venue for this contract
	 */
	TimeZone getTimeZone();
}
