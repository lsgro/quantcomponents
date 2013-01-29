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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.quantcomponents.algo.IPositionPoint;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.model.beans.ContractBase;

public class PositionView extends ViewPart implements ISelectionListener {
	
	private class ExecutionInfo implements ISeriesListener<Date, Double> {
		TradingAgentExecutionWrapper executionWrapper;
		ISeries<Date, Double, ISeriesPoint<Date, Double>> executionOutput;
		Map<IContract, IPositionPoint> positionMap = new ConcurrentHashMap<IContract, IPositionPoint>();
		
		public ExecutionInfo(TradingAgentExecutionWrapper executionWrapper) {
			this.executionWrapper = executionWrapper;
			this.executionOutput = executionWrapper.getManager().getExecutionOutput(executionWrapper.getHandle());
			for (ISeriesPoint<Date, Double> point : executionOutput) {
				if (point instanceof IPositionPoint) {
					onItemAdded((IPositionPoint) point);
				}
			}
			this.executionOutput.addSeriesListener(this);
		}

		public void dispose() {
			executionOutput.removeSeriesListener(this);
		}
		
		@Override
		public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) {
			if (updatedItem instanceof IPositionPoint) {
				IPositionPoint positionPoint = (IPositionPoint) updatedItem;
				positionMap.put(positionPoint.getContract(), positionPoint);
				executionUpdated(this);
			}
		}

		@Override
		public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
			onItemUpdated(null, newItem);
		}
	}
	
	public static final String VIEW_ID = "com.quantcomponents.ui.algo.positions";
	private static final int SORT_ASCENDING = 1;
	private final Map<TradingAgentExecutionWrapper, ExecutionInfo> executionMap = new ConcurrentHashMap<TradingAgentExecutionWrapper, ExecutionInfo>();
	private final NumberFormat priceFormat = new DecimalFormat("0.000");
	private final NumberFormat positionFormat = new DecimalFormat("0.##");
	private volatile Composite parent;
	private volatile ExecutionInfo currentExecutionInfo;
	private volatile TableViewer positionListViewer;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		
		positionListViewer = new TableViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		TableViewerColumn viewColContract = new TableViewerColumn(positionListViewer, SWT.NONE);
		viewColContract.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IPositionPoint p = (IPositionPoint)element;
				return ContractBase.shortStringRepr(p.getContract());
			}});
		TableColumn columnContract = viewColContract.getColumn();
		columnContract.setText("Contract");
		columnContract.setWidth(120);
		columnContract.setResizable(true);
		
		TableViewerColumn viewColPosition = new TableViewerColumn(positionListViewer, SWT.RIGHT);
		viewColPosition.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IPositionPoint p = (IPositionPoint)element;
				return positionFormat.format(p.getPosition().getSignedAmount());
			}});
		TableColumn columnPosition = viewColPosition.getColumn();
		columnPosition.setText("Position");
		columnPosition.setWidth(80);
		columnPosition.setResizable(true);
		
		TableViewerColumn viewColMktPrice = new TableViewerColumn(positionListViewer, SWT.RIGHT);
		viewColMktPrice.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IPositionPoint p = (IPositionPoint)element;
				return priceFormat.format(p.getPosition().getMarketPrice());
			}});
		TableColumn columnMktPrice = viewColMktPrice.getColumn();
		columnMktPrice.setText("Mkt Price");
		columnMktPrice.setWidth(100);
		columnMktPrice.setResizable(true);
		
		TableViewerColumn viewColMktValue = new TableViewerColumn(positionListViewer, SWT.RIGHT);
		viewColMktValue.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IPositionPoint p = (IPositionPoint)element;
				return priceFormat.format(p.getPosition().getMarketValue());
			}});
		TableColumn columnMktValue = viewColMktValue.getColumn();
		columnMktValue.setText("Mkt Value");
		columnMktValue.setWidth(100);
		columnMktValue.setResizable(true);
		
		TableViewerColumn viewColAvgPrice = new TableViewerColumn(positionListViewer, SWT.RIGHT);
		viewColAvgPrice.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IPositionPoint p = (IPositionPoint)element;
				return priceFormat.format(p.getPosition().getAveragePrice());
			}});
		TableColumn columnAvgPrice = viewColAvgPrice.getColumn();
		columnAvgPrice.setText("Avg Price");
		columnAvgPrice.setWidth(100);
		columnAvgPrice.setResizable(true);
		
		TableViewerColumn viewColUnrPnl = new TableViewerColumn(positionListViewer, SWT.RIGHT);
		viewColUnrPnl.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IPositionPoint p = (IPositionPoint)element;
				return priceFormat.format(p.getPosition().getUnrealizedPnl());
			}});
		TableColumn columnUnrPnl = viewColUnrPnl.getColumn();
		columnUnrPnl.setText("Unr P&&L");
		columnUnrPnl.setWidth(100);
		columnUnrPnl.setResizable(true);
		
		TableViewerColumn viewColRealPnl = new TableViewerColumn(positionListViewer, SWT.RIGHT);
		viewColRealPnl.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IPositionPoint p = (IPositionPoint)element;
				return priceFormat.format(p.getPosition().getRealizedPnl());
			}});
		TableColumn columnRealPnl = viewColRealPnl.getColumn();
		columnRealPnl.setText("Real P&&L");
		columnRealPnl.setWidth(100);
		columnRealPnl.setResizable(true);
		
		Table table = positionListViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setSortColumn(columnContract);
		table.setSortDirection(SORT_ASCENDING);
		
		positionListViewer.setContentProvider(new ArrayContentProvider());
		
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
				TradingAgentExecutionWrapper executionWrapper = (TradingAgentExecutionWrapper) object; 
				if (currentExecutionInfo == null || currentExecutionInfo.executionWrapper != executionWrapper) {
					ExecutionInfo executionInfo = executionMap.get(executionWrapper);
					if (executionInfo == null) {
						executionInfo = new ExecutionInfo(executionWrapper);
						executionMap.put(executionWrapper, executionInfo);
					}
					currentExecutionInfo = executionInfo;
					executionUpdated(currentExecutionInfo);
				}
			}
		}
	}
	
	@Override
	public void dispose() {
		for (ExecutionInfo executionInfo : executionMap.values()) {
			executionInfo.dispose();
		}
	}

	private void executionUpdated(final ExecutionInfo executionInfo) {
		if (executionInfo == currentExecutionInfo) {
			PositionView.this.parent.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					positionListViewer.setInput(executionInfo.positionMap.values());
				}});
		}
	}
}
