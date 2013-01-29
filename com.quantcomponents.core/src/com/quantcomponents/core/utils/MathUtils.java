/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.utils;

public class MathUtils {
	
	public static class LineParameters {
		double slope;
		double intercept;
	}

	public static LineParameters lineParametersFromSegment(double x_a, double y_a, double x_b, double y_b) {
		LineParameters params = new LineParameters();
		params.slope = (y_b - y_a) / (x_b - x_a);
		params.intercept = y_a - params.slope * x_a;
		return params;
	}

	public static double signedDistanceFromLine(LineParameters lineParameters, double x, double y) {
		return (y - lineParameters.slope * x - lineParameters.intercept) / Math.sqrt(lineParameters.slope * lineParameters.slope + 1);
	}

	public static double distanceFromLine(LineParameters lineParameters, double x, double y) {
		return Math.abs(signedDistanceFromLine(lineParameters, x, y));
	}

}
