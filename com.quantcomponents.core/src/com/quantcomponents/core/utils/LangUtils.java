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

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class LangUtils {
	public static boolean safeNotEqualsObject(Object o1, Object o2) {
		return o1 != null && !o1.equals(o2) || o2 != null && !o2.equals(o1);
	}

	public static boolean safeNotEqualsDouble(Double o1, Double o2, double threshold) {
		return o1 == null && o2 != null || o1 != null && o2 == null || o1 != null && o2 != null && Math.abs(o1 - o2) > threshold;
	}

	public static <T> Iterator<T> unmodifiableIterator(final Iterator<T> source) {
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return source.hasNext();
			}

			@Override
			public T next() {
				return source.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}};
	}
	
	public static int indexInArray(Object[] array, Object o) {
		for (int i = 0; i < array.length; i++) {
			if (o.equals(array[i])) {
				return i;
			}
		}
		return -1;
	}
	
	public static 	Dictionary<String, Object> cloneDictionary(Dictionary<String, ?> source) {
		Dictionary<String, Object> target = new Hashtable<String, Object>();
		Enumeration<String> keys = source.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			target.put(key, source.get(key));
		}
		return target;
	}

	private static String extractMessage(Throwable e) {
		if (e.getMessage() != null) {
			return e.getMessage();
		} else {
			return e.toString();
		}
	}
	
	public static String exceptionMessage(Throwable e) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(extractMessage(e));
		if (e.getCause() != null) {
			buffer.append(" [cause: ").append(extractMessage(e.getCause())).append("]");
		}
		return buffer.toString();
	}
}
