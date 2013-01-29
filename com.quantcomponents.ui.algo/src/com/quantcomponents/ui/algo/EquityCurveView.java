/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ui.algo;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.quantcomponents.algo.EquityCurveProcessor;
import com.quantcomponents.chart.series.LevelSeriesRenderer;
import com.quantcomponents.chart.series.TimeSeriesChart;
import com.quantcomponents.core.model.IMutableSeries;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.series.LinkedListSeries;
import com.quantcomponents.core.utils.LangUtils;

public class EquityCurveView extends ViewPart implements ISelectionListener {
	public static final String VIEW_ID = "com.quantcomponents.ui.algo.equityCurve";
	private static final long DEFAULT_INTERVAL = 24L * 60L * 60L * 1000L;
	private static final int MARGIN_LEFT = 10;
	private static final int MARGIN_TOP = 10;
	private static final int MARGIN_RIGHT = 50;
	private static final int MARGIN_BOTTOM = 20;
	private static class ExecutionInfo {
		public ExecutionInfo(EquityCurveProcessor processor, IMutableSeries<Date, Double, ISeriesPoint<Date, Double>> outputSeries) {
			this.processor = processor;
			this.outputSeries = outputSeries;
		}
		EquityCurveProcessor processor;
		IMutableSeries<Date, Double, ISeriesPoint<Date, Double>> outputSeries;
	}
	private final Map<TradingAgentExecutionWrapper, ExecutionInfo> executionMap = new HashMap<TradingAgentExecutionWrapper, ExecutionInfo>();
	private volatile Composite parent;
	private volatile ExecutionInfo currentExecutionInfo;
	private volatile TimeSeriesChart chart;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		chart = new TimeSeriesChart(parent, SWT.NONE);
		chart.setSeriesRenderer(new LevelSeriesRenderer());
		chart.setMargins(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM);
		getSite().getPage().addSelectionListener(this);
		selectionChanged(null, getSite().getPage().getSelection());
	}
	
	@Override
	public void setFocus() {
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structured = (IStructuredSelection) selection;
			Object object = structured.getFirstElement();
			if (object instanceof TradingAgentExecutionWrapper) {
				final TradingAgentExecutionWrapper executionWrapper = (TradingAgentExecutionWrapper) object; 
				try {
					getViewSite().getWorkbenchWindow().run(true, false, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) {
							ExecutionInfo executionInfo = executionMap.get(executionWrapper);
							if (executionInfo == null) {
								EquityCurveProcessor processor = new EquityCurveProcessor();
								String processorOutputSeriesID = "output-" + processor.toString();
								ISeries<Date, Double, ISeriesPoint<Date, Double>> executionOutputSeries = executionWrapper.getManager().getExecutionOutput(executionWrapper.getHandle());
								executionInfo = new ExecutionInfo(processor, new LinkedListSeries<Date, Double, ISeriesPoint<Date, Double>>(processorOutputSeriesID, false));
								executionInfo.processor.wire(Collections.singletonMap(EquityCurveProcessor.INPUT_SERIES_NAME, executionOutputSeries), executionInfo.outputSeries);
								executionMap.put(executionWrapper, executionInfo);
							}
							currentExecutionInfo = executionInfo;
							chart.setSeries(currentExecutionInfo.outputSeries);
							chart.setPointInterval(calculateInitialPointInterval(currentExecutionInfo.outputSeries));
							chart.refresh();
						}});
				} catch (Exception e) {
					MessageDialog.openError(parent.getShell(), "Equity curve update failed", LangUtils.exceptionMessage(e));
				} 
			}
		}
	}
	
	@Override
	public void dispose() {
		for (ExecutionInfo executionInfo : executionMap.values()) {
			executionInfo.processor.unwire();
		}
	}
	
	private long calculateInitialPointInterval(ISeries<Date, Double, ISeriesPoint<Date, Double>> series) {
		if (series.isEmpty()) {
			return DEFAULT_INTERVAL;
		} else {
			return Math.max((series.getLast().getIndex().getTime() - series.getFirst().getIndex().getTime()) / series.size(), 1);
		}
	}
}
