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

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.quantcomponents.algo.ITradeStatsPoint;
import com.quantcomponents.algo.TradeStatsProcessor;
import com.quantcomponents.core.model.IMutableSeries;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.core.model.ISeriesListener;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.series.LinkedListSeries;
import com.quantcomponents.core.series.SimplePoint;
import com.quantcomponents.core.utils.LangUtils;

public class TradingStatsView extends ViewPart implements ISelectionListener, ISeriesListener<Date, Double> {
	
	private static class ExecutionInfo {
		public ExecutionInfo(TradeStatsProcessor processor, IMutableSeries<Date, Double, ISeriesPoint<Date, Double>> outputSeries) {
			this.processor = processor;
			this.outputSeries = outputSeries;
		}
		TradeStatsProcessor processor;
		IMutableSeries<Date, Double, ISeriesPoint<Date, Double>> outputSeries;
	}
	
	private class TradeSeriesContentProvider implements IStructuredContentProvider, ISeriesListener<Date, Double> {
		ISeries<Date, Double, ISeriesPoint<Date, Double>> data;
		final Deque<ITradeStatsPoint> trades = new LinkedList<ITradeStatsPoint>(); 

		@Override
		public void dispose() {
			if (data != null) {
				data.removeSeriesListener(this);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (data != null) {
				data.removeSeriesListener(this);
			}
			if (newInput != null) {
				data = (ISeries<Date, Double, ISeriesPoint<Date,Double>>) newInput;
				fillTradeSeries();
				data.addSeriesListener(this);
			}
		}

		@Override
		public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) {
			if (updatedItem instanceof ITradeStatsPoint) {
				synchronized (this) {
					trades.removeLast();
					trades.addLast((ITradeStatsPoint) updatedItem);
				}
			}
		}

		@Override
		public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
			if (newItem instanceof ITradeStatsPoint) {
				synchronized (this) {
					trades.addLast((ITradeStatsPoint) newItem);
				}
			}
		}

		@Override
		public synchronized Object[] getElements(Object inputElement) {
			return trades.toArray(new Object[trades.size()]);
		}
		
		private synchronized void fillTradeSeries() {
			trades.clear();
			for (ISeriesPoint<Date, Double> point : data) {
				if (point instanceof ITradeStatsPoint) {
					trades.add((ITradeStatsPoint) point);
				}
			}
		}
	}
	
	public static final String VIEW_ID = "com.quantcomponents.ui.algo.tradingStats";
	private static final int SORT_ASCENDING = 1;
	private final Map<TradingAgentExecutionWrapper, ExecutionInfo> executionMap = new HashMap<TradingAgentExecutionWrapper, ExecutionInfo>();
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private final DecimalFormat df = new DecimalFormat("0.00");
	
	private volatile Composite parent;
	private volatile TabFolder rootTabs;
	private volatile TabItem tabList;
	private volatile TabItem tabStats;
	
	private volatile TableViewer tradesTableViewer;
	
	private volatile Text bestTradeTimeDisplay;
	private volatile Text bestTradePnlDisplay;
	private volatile Text worstTradeTimeDisplay;
	private volatile Text worstTradePnlDisplay;
	private volatile Text maxDrawdownEndTimeDisplay;
	private volatile Text maxDrawdownValueDisplay;
	private volatile Text maxRunupEndTimeDisplay;
	private volatile Text maxRunupValueDisplay;
	private volatile Text lowestEquityTimeDisplay;
	private volatile Text lowestEquityValueDisplay;
	private volatile Text highestEquityTimeDisplay;
	private volatile Text highestEquityValueDisplay;
	private volatile ExecutionInfo currentExecutionInfo;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		
		rootTabs = new TabFolder(parent, SWT.NULL);
		
		tabList = new TabItem(rootTabs, SWT.NULL);
		tabList.setText("List");
		
		tabStats = new TabItem(rootTabs, SWT.NULL);
		tabStats.setText("Statistics");
		
		Composite tradesContainer = new Composite(rootTabs, SWT.NULL);
		tabList.setControl(tradesContainer);
		
		// ----------------------- TRADE TABLE ----------------------- //
		Composite listContainer = new Composite(rootTabs, SWT.NULL);
		tabList.setControl(listContainer);
		listContainer.setLayout(new FillLayout());
		
		tradesTableViewer = new TableViewer(listContainer, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		
		TableViewerColumn viewColDateTime = new TableViewerColumn(tradesTableViewer, SWT.NONE);
		viewColDateTime.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ITradeStatsPoint t = (ITradeStatsPoint) element;
				return sdf.format(t.getIndex());
			}
		});
		TableColumn columnDateTime = viewColDateTime.getColumn();
		columnDateTime.setText("Time");
		columnDateTime.setWidth(140);
		columnDateTime.setResizable(true);
		
		TableViewerColumn viewColSide = new TableViewerColumn(tradesTableViewer, SWT.NONE);
		viewColSide.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ITradeStatsPoint t = (ITradeStatsPoint) element;
				return t.getTrade().getOrder() != null ? t.getTrade().getOrder().getSide().name() : "";
			}
		});
		TableColumn columnSide = viewColSide.getColumn();
		columnSide.setText("Side");
		columnSide.setWidth(40);
		columnSide.setResizable(true);
		
		TableViewerColumn viewColSize = new TableViewerColumn(tradesTableViewer, SWT.NONE);
		viewColSize.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ITradeStatsPoint t = (ITradeStatsPoint) element;
				return t.getTrade().getOrder() != null ? Integer.toString(t.getTrade().getOrder().getAmount()) : "";
			}
		});
		TableColumn columnSize = viewColSize.getColumn();
		columnSize.setText("Size");
		columnSize.setWidth(40);
		columnSize.setResizable(true);
		
		TableViewerColumn viewColExPrice = new TableViewerColumn(tradesTableViewer, SWT.RIGHT);
		viewColExPrice.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ITradeStatsPoint t = (ITradeStatsPoint) element;
				return df.format(t.getTrade().getExecutionPrice());
			}
		});
		TableColumn columnExPrice = viewColExPrice.getColumn();
		columnExPrice.setText("Ex.Price");
		columnExPrice.setWidth(80);
		columnExPrice.setResizable(true);
		
		TableViewerColumn viewColAvgPrice = new TableViewerColumn(tradesTableViewer, SWT.RIGHT);
		viewColAvgPrice.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ITradeStatsPoint t = (ITradeStatsPoint) element;
				return df.format(t.getTrade().getAveragePrice());
			}
		});
		TableColumn columnAvgPrice = viewColAvgPrice.getColumn();
		columnAvgPrice.setText("Avg.Price");
		columnAvgPrice.setWidth(80);
		columnAvgPrice.setResizable(true);
		
		TableViewerColumn viewColTradePnl = new TableViewerColumn(tradesTableViewer, SWT.RIGHT);
		viewColTradePnl.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ITradeStatsPoint t = (ITradeStatsPoint) element;
				return df.format(t.getTradePnl());
			}
		});
		TableColumn columnTradePnl = viewColTradePnl.getColumn();
		columnTradePnl.setText("Trade P&&L");
		columnTradePnl.setWidth(80);
		columnTradePnl.setResizable(true);
		
		TableViewerColumn viewColMaxFavExc = new TableViewerColumn(tradesTableViewer, SWT.RIGHT);
		viewColMaxFavExc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ITradeStatsPoint t = (ITradeStatsPoint) element;
				return df.format(t.getMaxFavorableExcursion());
			}
		});
		TableColumn columnMaxFavExc = viewColMaxFavExc.getColumn();
		columnMaxFavExc.setText("Fav.Chg.");
		columnMaxFavExc.setWidth(80);
		columnMaxFavExc.setResizable(true);
		
		TableViewerColumn viewColMaxAdvExc = new TableViewerColumn(tradesTableViewer, SWT.RIGHT);
		viewColMaxAdvExc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ITradeStatsPoint t = (ITradeStatsPoint) element;
				return df.format(t.getMaxAdverseExcursion());
			}
		});
		TableColumn columnMaxAdvExc = viewColMaxAdvExc.getColumn();
		columnMaxAdvExc.setText("Adv.Chg.");
		columnMaxAdvExc.setWidth(80);
		columnMaxAdvExc.setResizable(true);
		
		Table table = tradesTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setSortColumn(columnDateTime);
		table.setSortDirection(SORT_ASCENDING);
		
		tradesTableViewer.setContentProvider(new TradeSeriesContentProvider());

		// ----------------------- TRADE STATS ----------------------- //
		Composite statsContainer = new Composite(rootTabs, SWT.NULL);
		tabStats.setControl(statsContainer);
		
		statsContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout rootLayout = new GridLayout();
		rootLayout.verticalSpacing = 10;
		rootLayout.numColumns = 3;
		statsContainer.setLayout(rootLayout);
		
		Label bestTradeLabel = new Label(statsContainer, SWT.NULL);
		bestTradeLabel.setText("Best trade");
		bestTradeTimeDisplay = new Text(statsContainer, SWT.READ_ONLY | SWT.BORDER);
		GridData bestTradeTimeDisplayGridData = new GridData();
		bestTradeTimeDisplayGridData.widthHint = 250;
		bestTradeTimeDisplay.setLayoutData(bestTradeTimeDisplayGridData);
		bestTradePnlDisplay = new Text(statsContainer, SWT.READ_ONLY | SWT.BORDER | SWT.RIGHT);
		GridData bestTradePnlDisplayGridData = new GridData();
		bestTradePnlDisplayGridData.widthHint = 100;
		bestTradePnlDisplay.setLayoutData(bestTradePnlDisplayGridData);
		
		Label worstTradeLabel = new Label(statsContainer, SWT.NULL);
		worstTradeLabel.setText("Worst trade");
		worstTradeTimeDisplay = new Text(statsContainer, SWT.READ_ONLY | SWT.BORDER);
		GridData worstTradeTimeDisplayGridData = new GridData();
		worstTradeTimeDisplayGridData.widthHint = 250;
		worstTradeTimeDisplay.setLayoutData(worstTradeTimeDisplayGridData);
		worstTradeTimeDisplay.setSize(400, 10);
		worstTradePnlDisplay = new Text(statsContainer, SWT.READ_ONLY | SWT.BORDER | SWT.RIGHT);
		GridData worstTradePnlDisplayGridData = new GridData();
		worstTradePnlDisplayGridData.widthHint = 100;
		worstTradePnlDisplay.setLayoutData(worstTradePnlDisplayGridData);

		Label maxDrawdownLabel = new Label(statsContainer, SWT.NULL);
		maxDrawdownLabel.setText("Max drawdown");
		maxDrawdownEndTimeDisplay = new Text(statsContainer, SWT.READ_ONLY | SWT.BORDER);
		GridData maxDrawdownEndTimeDisplayGridData = new GridData();
		maxDrawdownEndTimeDisplayGridData.widthHint = 250;
		maxDrawdownEndTimeDisplay.setLayoutData(maxDrawdownEndTimeDisplayGridData);
		maxDrawdownEndTimeDisplay.setSize(400, 10);
		maxDrawdownValueDisplay = new Text(statsContainer, SWT.READ_ONLY | SWT.BORDER | SWT.RIGHT);
		GridData maxDrawdownValueDisplayGridData = new GridData();
		maxDrawdownValueDisplayGridData.widthHint = 100;
		maxDrawdownValueDisplay.setLayoutData(maxDrawdownValueDisplayGridData);
		
		Label maxRunupLabel = new Label(statsContainer, SWT.NULL);
		maxRunupLabel.setText("Max runup");
		maxRunupEndTimeDisplay = new Text(statsContainer, SWT.READ_ONLY | SWT.BORDER);
		GridData maxRunupEndTimeDisplayGridData = new GridData();
		maxRunupEndTimeDisplayGridData.widthHint = 250;
		maxRunupEndTimeDisplay.setLayoutData(maxRunupEndTimeDisplayGridData);
		maxRunupEndTimeDisplay.setSize(400, 10);
		maxRunupValueDisplay = new Text(statsContainer, SWT.READ_ONLY | SWT.BORDER | SWT.RIGHT);
		GridData maxRunupValueDisplayGridData = new GridData();
		maxRunupValueDisplayGridData.widthHint = 100;
		maxRunupValueDisplay.setLayoutData(maxRunupValueDisplayGridData);
	
		Label lowestEquityPointLabel = new Label(statsContainer, SWT.NULL);
		lowestEquityPointLabel.setText("Lowest equity");
		lowestEquityTimeDisplay = new Text(statsContainer, SWT.READ_ONLY | SWT.BORDER);
		GridData lowestEquityTimeDisplayGridData = new GridData();
		lowestEquityTimeDisplayGridData.widthHint = 250;
		lowestEquityTimeDisplay.setLayoutData(lowestEquityTimeDisplayGridData);
		lowestEquityValueDisplay = new Text(statsContainer, SWT.READ_ONLY | SWT.BORDER | SWT.RIGHT);
		GridData lowestEquityValueDisplayGridData = new GridData();
		lowestEquityValueDisplayGridData.widthHint = 100;
		lowestEquityValueDisplay.setLayoutData(lowestEquityValueDisplayGridData);
		
		Label highestEquityPointLabel = new Label(statsContainer, SWT.NULL);
		highestEquityPointLabel.setText("Highest equity");
		highestEquityTimeDisplay = new Text(statsContainer, SWT.READ_ONLY | SWT.BORDER);
		GridData highestEquityTimeDisplayGridData = new GridData();
		highestEquityTimeDisplayGridData.widthHint = 250;
		highestEquityTimeDisplay.setLayoutData(highestEquityTimeDisplayGridData);
		highestEquityValueDisplay = new Text(statsContainer, SWT.READ_ONLY | SWT.BORDER | SWT.RIGHT);
		GridData highestEquityValueDisplayGridData = new GridData();
		highestEquityValueDisplayGridData.widthHint = 100;
		highestEquityValueDisplay.setLayoutData(highestEquityValueDisplayGridData);

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
				final TradingAgentExecutionWrapper executionHandle = (TradingAgentExecutionWrapper) object; 
				try {
					getViewSite().getWorkbenchWindow().run(true, false, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							try {
							ExecutionInfo executionInfo = executionMap.get(executionHandle);
							if (executionInfo == null) {
								TradeStatsProcessor processor = new TradeStatsProcessor();
								String processorOutputSeriesID = "output-" + processor.toString();
								ISeries<Date, Double, ISeriesPoint<Date, Double>> executionOutputSeries = executionHandle.getManager().getExecutionOutput(executionHandle.getHandle());
								executionInfo = new ExecutionInfo(processor, new LinkedListSeries<Date, Double, ISeriesPoint<Date, Double>>(processorOutputSeriesID, false));
								executionInfo.processor.wire(Collections.singletonMap(TradeStatsProcessor.INPUT_SERIES_NAME, executionOutputSeries) ,executionInfo.outputSeries);
								executionMap.put(executionHandle, executionInfo);
							}
							if (currentExecutionInfo != null) {
								currentExecutionInfo.outputSeries.removeSeriesListener(TradingStatsView.this);
							}
							currentExecutionInfo = executionInfo;
							currentExecutionInfo.outputSeries.addSeriesListener(TradingStatsView.this);
							TradingStatsView.this.parent.getDisplay().asyncExec(new Runnable() {
								@Override
								public void run() {
									tradesTableViewer.setInput(currentExecutionInfo.outputSeries);
								}});
							updateStats();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}});
				} catch (Exception e) {
					MessageDialog.openError(parent.getShell(), "Trading stats update failed", "Error while changing selection: " + LangUtils.exceptionMessage(e));
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
	
	private void updateStats() {
		final TradeStatsProcessor tradeStatsProcessor = currentExecutionInfo == null ? null : currentExecutionInfo.processor;
		if (tradeStatsProcessor != null) {
			parent.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					ITradeStatsPoint bestTrade = tradeStatsProcessor.getBestTrade();
					if (bestTrade != null) {
						bestTradeTimeDisplay.setText(sdf.format(bestTrade.getIndex()));
						bestTradePnlDisplay.setText(df.format(bestTrade.getTradePnl()));
					} else {
						bestTradeTimeDisplay.setText("");
						bestTradePnlDisplay.setText("");
					}
					ITradeStatsPoint worstTrade = tradeStatsProcessor.getWorstTrade();
					if (worstTrade != null) {
						worstTradeTimeDisplay.setText(sdf.format(worstTrade.getIndex()));
						worstTradePnlDisplay.setText(df.format(worstTrade.getTradePnl()));
					} else {
						worstTradeTimeDisplay.setText("");
						worstTradePnlDisplay.setText("");
					}
					SimplePoint maxDDEnd = tradeStatsProcessor.getEndOfMaxDrawdown();
					if (maxDDEnd != null) {
						maxDrawdownEndTimeDisplay.setText(sdf.format(maxDDEnd.getIndex()));
						maxDrawdownValueDisplay.setText(df.format(maxDDEnd.getValue() - tradeStatsProcessor.getStartOfMaxDrawdown().getValue()));
					} else {
						maxDrawdownEndTimeDisplay.setText("");
						maxDrawdownValueDisplay.setText("");
					}
					SimplePoint maxRUEnd = tradeStatsProcessor.getEndOfMaxRunup();
					if (maxRUEnd != null) {
						maxRunupEndTimeDisplay.setText(sdf.format(maxRUEnd.getIndex()));
						maxRunupValueDisplay.setText(df.format(maxRUEnd.getValue() - tradeStatsProcessor.getStartOfMaxRunup().getValue()));
					} else {
						maxRunupEndTimeDisplay.setText("");
						maxRunupValueDisplay.setText("");
					}
					SimplePoint lowestEP = tradeStatsProcessor.getLowestEquityPoint();
					if (lowestEP != null) {
						lowestEquityTimeDisplay.setText(sdf.format(lowestEP.getIndex()));
						lowestEquityValueDisplay.setText(df.format(lowestEP.getValue()));
					} else {
						lowestEquityTimeDisplay.setText("");
						lowestEquityValueDisplay.setText("");
					}
					SimplePoint highestEP = tradeStatsProcessor.getHighestEquityPoint();
					if (highestEP != null) {
						highestEquityTimeDisplay.setText(sdf.format(highestEP.getIndex()));
						highestEquityValueDisplay.setText(df.format(highestEP.getValue()));
					} else {
						highestEquityTimeDisplay.setText("");
						highestEquityValueDisplay.setText("");
					}
					tradesTableViewer.refresh();
				}});
		}
	}
	
	@Override
	public void onItemUpdated(ISeriesPoint<Date, Double> existingItem, ISeriesPoint<Date, Double> updatedItem) {
		updateStats();
	}

	@Override
	public void onItemAdded(ISeriesPoint<Date, Double> newItem) {
		updateStats();
	}
}
