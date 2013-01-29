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
import com.quantcomponents.core.model.DataType;

/**
 * An implementation of {@link IOHLCPoint} that consolidates tick data
 * Each time the {@link OHLCPointAccumulator#addTick(ITickPoint)} method is called, the value of this data-point
 * is updated.
 * This class is useful when consolidating tick data into an OHLC series
 */
public class OHLCPointAccumulator implements IOHLCPoint, Serializable {
	private static final long serialVersionUID = 4470310868396495504L;
	private BarSize barSize;
	private DataType dataType;
	private Date date;
	private Date endDate;
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private Long volume;
	private double WAP;
	private int count;
	private Date lastUpdate;
	
	public OHLCPointAccumulator(){}

	public OHLCPointAccumulator(BarSize barSize, DataType dataType, Date date) {
		this.barSize = barSize;
		this.dataType = dataType;
		this.date = date;
		this.endDate = new Date(date.getTime() + barSize.getDurationInMs());
	}
	
	public static OHLCPointAccumulator fromIOHLCPoint(IOHLCPoint bar, DataType dataType) {
		OHLCPointAccumulator acc = new OHLCPointAccumulator(bar.getBarSize(), dataType, bar.getIndex());
		acc.open = bar.getOpen();
		acc.high = bar.getHigh();
		acc.low = bar.getLow();
		acc.close = bar.getClose();
		if (bar.getWAP() != null) {
			acc.WAP = bar.getWAP();
		}
		if (bar.getCount()!= null) {
			acc.count = bar.getCount();
		}
		if (bar.getVolume() != null) {
			acc.volume = bar.getVolume();
		}
		if (bar.getLastUpdate() != null) {
			acc.lastUpdate = bar.getLastUpdate();
		}
		return acc;
	}
	
	public boolean addTick(ITickPoint tick) {
		if (!dataType.includes(tick.getDataType())) {
			throw new IllegalArgumentException("Only " + dataType.name() + " assignable tick allowed");
		}
		if (tick.getIndex().before(date) || !tick.getIndex().before(endDate)) {
			return false;
		}
		if (lastUpdate != null && !tick.getIndex().after(lastUpdate)) {
			return false;
		}
		if (open == null) {
			open = tick.getValue();
		}
		if (close == null || lastUpdate != null && tick.getIndex().after(lastUpdate)) {
			close = tick.getValue();
		}
		if (low == null || tick.getValue().compareTo(low) < 0) {
			low = tick.getValue();
		}
		if (high == null || tick.getValue().compareTo(high) > 0) {
			high = tick.getValue();
		}
		if (tick.getSize() != null) {
			if (volume == null) {
				volume = (long) tick.getSize();
			} else {
				volume += (long) tick.getSize();
			}
		}
		WAP = (WAP * count + tick.getValue()) / ++count;
		lastUpdate = tick.getIndex();
		return true;
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
	public Date getStartIndex() {
		return getIndex();
	}

	@Override
	public Date getEndIndex() {
		return getLastUpdate();
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
	public Double getWAP() {
		return WAP;
	}

	@Override
	public Integer getCount() {
		return count;
	}

	@Override
	public Date getLastUpdate() {
		return lastUpdate;
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

}
