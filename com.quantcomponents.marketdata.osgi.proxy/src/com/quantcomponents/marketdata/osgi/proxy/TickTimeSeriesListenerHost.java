/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata.osgi.proxy;

import java.util.Date;

import com.quantcomponents.core.osgi.SeriesListenerHost;
import com.quantcomponents.marketdata.ITickPoint;
import com.quantcomponents.marketdata.osgi.ITickTimeSeriesListenerHost;
import com.quantcomponents.marketdata.osgi.ITickTimeSeriesListenerHostLocal;

public class TickTimeSeriesListenerHost extends SeriesListenerHost<Date, Double, ITickPoint> implements ITickTimeSeriesListenerHost, ITickTimeSeriesListenerHostLocal {}
