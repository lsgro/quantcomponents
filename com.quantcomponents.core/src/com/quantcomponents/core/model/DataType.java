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
 * Type of time-series data
 */
public enum DataType {
    BID_ASK,
    MIDPOINT,
    TRADES,
    BID,
    ASK,
    VOLUME;
    /**
     * Some type includes others, i.e. when this broader type is specified, all
     * its constituents can be returned
     * @param other The more specific type
     * @return true if 'other' is included in this type
     */
    public boolean includes(DataType other) {
    	if (this == other) {
    		return true;
    	}
    	if (this == BID_ASK && (other == TRADES || other == BID || other == ASK)) {
    		return true;
    	}
    	return false;
    }
}
