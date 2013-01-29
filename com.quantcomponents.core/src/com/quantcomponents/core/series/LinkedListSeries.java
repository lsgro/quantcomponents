/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.core.series;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.quantcomponents.core.model.IMutableSeries;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;

/**
 * Thread-safe implementation of {@link com.quantcomponents.core.model.IMutableSeries} based on a {@link java.util.LinkedList}
 *
 * @param <A> type of the abscissa
 * @param <O> type of the ordinates
 * @param <P> type of the data point
 */
public class LinkedListSeries<A extends Comparable<A>, O extends Comparable<O>, P extends ISeriesPoint<A, O>> implements IMutableSeries<A, O, P>, Serializable {
	private static final long serialVersionUID = 2891785718239781721L;
	private LinkedList<P> items = new LinkedList<P>();
	private transient List<ISeriesListener<A, O>> listeners = new CopyOnWriteArrayList<ISeriesListener<A, O>>();
	private final String ID;
	private final boolean enforceStrictSequence;
	private volatile long timestamp;

	public LinkedListSeries(String ID, boolean enforceStrictSequence) {
		this.ID = ID;
		this.enforceStrictSequence = enforceStrictSequence;
		updateTimestamp();
	}

	@Override
	public String getPersistentID() {
		return ID;
	}
	
	@Override
	public void addLast(P item) {
		synchronized (items) {
			if (!items.isEmpty()) {
				P last = items.getLast();
				if (isEnforceStrictSequence()) {
					if (item.getIndex().compareTo(last.getIndex()) <= 0) {
						throw new IllegalArgumentException("Item with index: " + item.getIndex() + " must follow last item index: " + last.getIndex());
					}
				} else {
					checkDuplicate(item, last);
					if (item.getIndex().compareTo(last.getIndex()) < 0) {
						throw new IllegalArgumentException("Item with index: " + item.getIndex() + " must not precede last item index: " + last.getIndex());
					}
				}
			}
			items.add(item);
			updateTimestamp();
		} // release lock before running arbitrary code
		if (listeners != null) {
			for (ISeriesListener<A, O> listener : listeners) {
				listener.onItemAdded(item);
			}
		}
	}
	
	@Override
	public void addFirst(P item) {
		synchronized (items) {
			if (!items.isEmpty()) {
				P first = items.getFirst();
				if (isEnforceStrictSequence()) {
					if (item.getIndex().compareTo(first.getIndex()) >= 0) {
						throw new IllegalArgumentException("Item with index: " + item.getIndex() + " must precede last item index: " + first.getIndex());
					}
				} else {
					checkDuplicate(item, first);
					if (item.getIndex().compareTo(first.getIndex()) > 0) {
						throw new IllegalArgumentException("Item with index: " + item.getIndex() + " must not follow last item index: " + first.getIndex());
					}
				}
			}
			items.addFirst(item);
			updateTimestamp();
		} // release lock before running arbitrary code
		if (listeners != null) {
			for (ISeriesListener<A, O> listener : listeners) {
				listener.onItemAdded(item);
			}
		}
	}

	public void addLastIfNotExists(P item) {
		synchronized (items) {
			if (!items.isEmpty()) {
				A lastIndex = items.getLast().getIndex();
				if (item.getIndex().compareTo(lastIndex) <= 0) {
					return;
				}
			}
			items.add(item);
			updateTimestamp();
		} // release lock before running arbitrary code
		if (listeners != null) {
			for (ISeriesListener<A, O> listener : listeners) {
				listener.onItemAdded(item);
			}
		}
	}
	
	public void addFirstIfNotExists(P item) {
		synchronized (items) {
			if (!items.isEmpty()) {
				A firstIndex = items.getFirst().getIndex();
				if (item.getIndex().compareTo(firstIndex) >= 0) {
					return;
				}
			}
			items.addFirst(item);
			updateTimestamp();
		} // release lock before running arbitrary code
		if (listeners != null) {
			for (ISeriesListener<A, O> listener : listeners) {
				listener.onItemAdded(item);
			}
		}
	}

	@Override
	public void insertFromTail(P item) {
		synchronized (items) {
			if (items.isEmpty()) {
				items.addLast(item);
			} else {
				LinkedList<P> tail = new LinkedList<P>();
				while (!items.isEmpty() && items.getLast().getIndex().compareTo(item.getIndex()) >= 0) {
					tail.addFirst(items.removeLast());
				}
				try {
					if (!tail.isEmpty()) {
						P sameOrNextIndexPoint = tail.getFirst();
						checkDuplicate(item, sameOrNextIndexPoint);
						if (sameOrNextIndexPoint.getIndex().compareTo(item.getIndex()) == 0 && isEnforceStrictSequence()) {
							throw new IllegalArgumentException("Item index: " + item.getIndex() + " is not unique");
						}
					}
					items.addLast(item);
				} finally {
					items.addAll(tail);
				}
			}
		}
		if (listeners != null) {
			for (ISeriesListener<A, O> listener : listeners) {
				listener.onItemAdded(item);
			}
		}
	}

	public void updateTail(P item) {
		P previousItem = null;
		synchronized (items) {
			if (!items.isEmpty() && item.getIndex().equals(items.getLast().getIndex())) {
				previousItem = items.removeLast();
				items.addLast(item);
			} else {
				throw new IllegalArgumentException("Item with index: " + item.getIndex() + " no tail of series");
			}
			updateTimestamp();
		} // release lock before running arbitrary code
		if (listeners != null) {
			for (ISeriesListener<A, O> listener : listeners) {
				listener.onItemUpdated(previousItem, item);
			}
		}
	}

	@Override
	public P getFirst() {
		synchronized (items) {
			if (items.isEmpty())
				return null;
			else
				return items.getFirst();
		}
	}

	@Override
	public P getLast() {
		synchronized (items) {
			if (items.isEmpty())
				return null;
			else
				return items.getLast();
		}
	}

	@Override
	public P getMinimum() {
		O min = null;
		P minItem = null;
		synchronized (items) {
			for (P item : items) {
				if (minItem == null || min.compareTo(item.getBottomValue()) > 0) {
					min = item.getBottomValue();
					minItem = item;
				}
			}
		}
		return minItem;
	}

	@Override
	public P getMaximum() {
		O max = null;
		P maxItem = null;
		synchronized (items) {
			for (P item : items) {
				if (maxItem == null || max.compareTo(item.getTopValue()) < 0) {
					max = item.getTopValue();
					maxItem = item;
				}
			}
		}
		return maxItem;
	}

	@Override
	public synchronized void addSeriesListener(ISeriesListener<A, O> listener) {
		if (listeners == null) {
			listeners = new CopyOnWriteArrayList<ISeriesListener<A, O>>();
		} 
		listeners.add(listener);
	}

	@Override
	public synchronized void removeSeriesListener(ISeriesListener<A, O> listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	@Override
	public int size() {
		synchronized (items) {
			return items.size();
		}
	}

	@Override
	public boolean isEmpty() {
		synchronized (items) {
			return items.isEmpty();
		}	
	}

	@Override
	public Iterator<P> iterator() {
		synchronized (items) {
			return new LinkedList<P>(items).iterator();
		}
	}

	@Override
	public Iterator<P> descendingIterator() {
		synchronized (items) {
			return new LinkedList<P>(items).descendingIterator();
		}
	}

	@Override
	public boolean isEnforceStrictSequence() {
		return enforceStrictSequence;
	}
	
	@Override
	public long getTimestamp() {
		return timestamp;
	}
	
	public void clear() {
		synchronized (items) {
			items.clear();
		}
	}

	@Override
	public IMutableSeries<A, O, P> createEmptyMutableSeries(String ID) {
		return new LinkedListSeries<A, O, P>(ID, isEnforceStrictSequence());
	}

	private void updateTimestamp() {
		timestamp = System.currentTimeMillis();
	}

	private void checkDuplicate(P item1, P item2) {
		if (item1.equals(item2)) {
			throw new IllegalArgumentException("Duplicate item: " + item1);
		}
	}
}
