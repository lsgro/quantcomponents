/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.chart;

/**
 * 
 * Container for the X and Y ranges of a data-series
 * @param <A> type of the abscissa
 * @param <O> type of the ordinate
 */
public class DataRange<A, O> implements IDataRange<A, O> {
	private final A bottomX;
	private final A topX;
	private final O bottomY;
	private final O topY;
	
	public DataRange(A bottomX, A topX, O bottomY, O topY) {
		this.bottomX = bottomX;
		this.topX = topX;
		this.bottomY = bottomY;
		this.topY = topY;
	}

	@Override
	public A getLowX() {
		return bottomX;
	}

	@Override
	public A getHighX() {
		return topX;
	}

	@Override
	public O getLowY() {
		return bottomY;
	}

	@Override
	public O getHighY() {
		return topY;
	}

}
