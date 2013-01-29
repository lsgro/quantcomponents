/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata.osgi.host;

import com.quantcomponents.core.remote.ServiceHandle;
import com.quantcomponents.marketdata.IMutableOHLCTimeSeries;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.osgi.IMutableOHLCTimeSeriesHost;
import com.quantcomponents.marketdata.osgi.IMutableOHLCTimeSeriesHostLocal;

public class MutableOHLCTimeSeriesHost extends OHLCTimeSeriesHost implements IMutableOHLCTimeSeriesHost, IMutableOHLCTimeSeriesHostLocal {
	
	public MutableOHLCTimeSeriesHost() {}
	
	@Override
	public void addLastItem(ServiceHandle<IMutableOHLCTimeSeriesHost> timeSeriesHostHandle, IOHLCPoint item) {
		((IMutableOHLCTimeSeries) retrieveSeriesInfo(timeSeriesHostHandle).series).addLast(item);
	}

	@Override
	public void addFirstItem(ServiceHandle<IMutableOHLCTimeSeriesHost> timeSeriesHostHandle, IOHLCPoint item) {
		((IMutableOHLCTimeSeries) retrieveSeriesInfo(timeSeriesHostHandle).series).addFirst(item);
	}

	@Override
	public void addLastItemIfNotExists(ServiceHandle<IMutableOHLCTimeSeriesHost> timeSeriesHostHandle, IOHLCPoint item) {
		((IMutableOHLCTimeSeries) retrieveSeriesInfo(timeSeriesHostHandle).series).addLastIfNotExists(item);
	}

	@Override
	public void addFirstItemIfNotExists(ServiceHandle<IMutableOHLCTimeSeriesHost> timeSeriesHostHandle, IOHLCPoint item) {
		((IMutableOHLCTimeSeries) retrieveSeriesInfo(timeSeriesHostHandle).series).addFirstIfNotExists(item);
	}

	@Override
	public void insertFromTail(ServiceHandle<IMutableOHLCTimeSeriesHost> timeSeriesHostHandle, IOHLCPoint item) {
		((IMutableOHLCTimeSeries) retrieveSeriesInfo(timeSeriesHostHandle).series).insertFromTail(item);
	}

	@Override
	public void updateItem(ServiceHandle<IMutableOHLCTimeSeriesHost> timeSeriesHostHandle, IOHLCPoint item) {
		((IMutableOHLCTimeSeries) retrieveSeriesInfo(timeSeriesHostHandle).series).updateTail(item);
	}
}
