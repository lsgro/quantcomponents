/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.marketdata;

import java.io.Serializable;
import java.util.Date;

import com.quantcomponents.core.model.BarSize;

/**
 * OHLC point implementation
 * @see IOHLCPoint
 */
public class OHLCPoint implements IOHLCPoint, Serializable {
	private static final long serialVersionUID = 1815636615364084312L;
	private final BarSize barSize;
	private final Date date;
	private final Double open;
	private final Double high;
	private final Double low;
	private final Double close;
	private final Long volume;
	private final Double WAP;
	private final Integer count;
	private Date lastUpdate;
	
	public static OHLCPoint copy(IOHLCPoint source) {
		return new OHLCPoint(source.getBarSize(), source.getIndex(), source.getOpen(), source.getHigh(), source.getLow(), source.getClose(), source.getVolume(), source.getWAP(), source.getCount());
	}

	public static OHLCPoint merge(IOHLCPoint first, IOHLCPoint second) {
		long volume = first.getVolume() + second.getVolume();
		int count = first.getCount() + second.getCount();
		double low = Math.min(first.getLow(), second.getLow());
		double high = Math.max(first.getHigh(), second.getHigh());
		double wap = (first.getWAP() * first.getVolume() + second.getWAP() * second.getVolume()) / volume;
		return new OHLCPoint(first.getBarSize(), first.getIndex(), first.getOpen(), high, low, second.getClose(), volume, wap, count);
	}

	public OHLCPoint(BarSize barSize, Date date, Double open, Double high, Double low,
			Double close, Long volume, Double WAP, Integer count) {
		this.barSize = barSize;
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.WAP = WAP;
		this.count = count;
	}
	@Override
	public BarSize getBarSize() {
		return barSize;
	}
	@Override
	public Date getIndex() {
		return date;
	}
	@Override
	public Double getOpen() {
		return open;
	}
	@Override
	public Double getHigh() {
		return high;
	}
	@Override
	public Double getLow() {
		return low;
	}
	@Override
	public Double getClose() {
		return close;
	}
	@Override
	public Long getVolume() {
		return volume;
	}
	@Override
	public Double getBottomValue() {
		return getLow();
	}
	@Override
	public Double getTopValue() {
		return getHigh();
	}
	@Override
	public Double getValue() {
		return getClose();
	}
	@Override
	public Double getWAP() {
		return WAP;
	}
	@Override
	public Integer getCount() {
		return count;
	}
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		buffer.append(getIndex());
		buffer.append("; O: ");
		buffer.append(getOpen());
		buffer.append("; H: ");
		buffer.append(getHigh());
		buffer.append("; L: ");
		buffer.append(getLow());
		buffer.append("; C: ");
		buffer.append(getClose());
		buffer.append("; V: ");
		buffer.append(getVolume());
		buffer.append("; #: ");
		buffer.append(getCount());
		buffer.append("]");
		return buffer.toString();
	}
	public Date getLastUpdate() {
		if (lastUpdate == null) {
			return new Date(getIndex().getTime() + getBarSize().getDurationInMs());
		} else {
			return lastUpdate;
		}
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public Date getStartIndex() {
		return getIndex();
	}

	@Override
	public Date getEndIndex() {
		return getLastUpdate();
	}
}
