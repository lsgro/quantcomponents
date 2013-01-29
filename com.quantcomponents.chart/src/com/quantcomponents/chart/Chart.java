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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Base class for charts.
 * Implementors must:
 * <ul
 * <li>write the method {@link Chart#updateMetrics(IChartMetrics)}, which
 * is called at each paint iteration to update the metrics</li>
 * <li>add their {@link IDrawable} objects to the list returned by 
 * {@link Chart#getDrawables()}</li>
 * </ul>
 * The chart will in turn call {@link IDrawable#draw(IChartMetrics, org.eclipse.swt.graphics.GC)}
 * on each of them according to their position in the list.
 *
 * @param <A> type of the chart abscissa
 * @param <O> type of the chart ordinate
 */
public abstract class Chart<A, O> extends Composite {
	private static final long REFRESH_THREAD_WAIT_QUANTUM = 500;
	private static final long MIN_TIME_BETWEEN_REFRESH = 20;
	private final Canvas canvas;
	private final List<IDrawable<A, O>> listOfDrawables = new CopyOnWriteArrayList<IDrawable<A, O>>();
	private volatile IChartMetrics<A, O> metrics;
	private final Thread refreshThread;
	private volatile boolean refreshScheduled;
	
	private final class ChartRefreshLoop implements Runnable {
		@Override
		public void run() {
			while (!isDisposed()) {
				try {
					synchronized (refreshThread) {
						while (!refreshScheduled) {
							refreshThread.wait(REFRESH_THREAD_WAIT_QUANTUM);
							if (isDisposed()) {
								return;
							}
						}
						refreshScheduled = false;
						getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								updateView();
							}});
						Thread.sleep(MIN_TIME_BETWEEN_REFRESH);
					}
				} catch (InterruptedException e) { /* who cares? */ }
			}
		}
	}
	
	public Chart(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		canvas = new Canvas(this, SWT.DOUBLE_BUFFERED);
		canvas.addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) { /* do nothing */ }

			@Override
			public void controlResized(ControlEvent e) {
				updateView();
			}
		});
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				Rectangle canvasBounds = canvas.getBounds();
				canvas.setBackground(Chart.this.getBackground());
				canvas.drawBackground(e.gc, 0, 0, canvasBounds.width, canvasBounds.height);
				IChartMetrics<A, O> currentChartMetrics = metrics;
				if (currentChartMetrics != null) {
					updateMetrics(currentChartMetrics);
					currentChartMetrics.setDrawingArea(canvasBounds); 
					for (IDrawable<A, O> drawable : listOfDrawables) {
						drawable.draw(currentChartMetrics, e.gc);
					}
				}
			}});
		refreshThread = new Thread(new ChartRefreshLoop());
		refreshThread.start();
	}
	
	public abstract void updateMetrics(IChartMetrics<A, O> metrics); 
	
	public void setMetrics(IChartMetrics<A, O> metrics) {
		this.metrics = metrics;
	}

	public List<IDrawable<A, O>> getDrawables() {
		return listOfDrawables;
	}
	
	public Control getControl() {
		return canvas;
	}
	
	public void refresh() {
		synchronized(refreshThread) {
			refreshScheduled = true;
			refreshThread.notify();
		}
	}

	private void updateView() {
		if (canvas != null && !canvas.isDisposed()) {
			canvas.redraw();
			canvas.update();
		}
	}
}
