/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.ui.marketdata;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.quantcomponents.chart.series.ITimeSeriesChartModel;
import com.quantcomponents.core.calendar.ITradingCalendar;
import com.quantcomponents.core.model.ISeriesPoint;
import com.quantcomponents.core.utils.LangUtils;
import com.quantcomponents.ui.core.TaskMonitorAdapter;

public class TimeChartDialog<P extends ISeriesPoint<Date, Double>> extends Dialog {
	private final ITimeSeriesChartModel<P> chartModel;
	private final StockDatabasePresentationWrapper stockDatabase;
	private final List<ITradingCalendar> tradingCalendars;
	private ITradingCalendar tradingCalendar;
	private TabFolder tabFolder;
	private TabItem dateTab;
	private TabItem calendarTab;
	private Date startDate;
	private Date endDate;
	private boolean movingWindow;
	private StartEndDateEditor editor;
	private TableViewer calendarViewer;

	public TimeChartDialog(ITimeSeriesChartModel<P> chartModel, StockDatabasePresentationWrapper stockDatabase, List<ITradingCalendar> tradingCalendars, Shell parentShell) {
		super(parentShell);
		this.tradingCalendar = chartModel.getTradingCalendar();
		this.chartModel = chartModel;
		this.stockDatabase = stockDatabase;
		this.tradingCalendars = tradingCalendars;
		this.startDate = chartModel.getStartDate();
		this.endDate = chartModel.getEndDate();
		this.movingWindow = chartModel.isMovingWindow();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.NULL);
		tabFolder.addSelectionListener(new SelectionListener() { // automatically highlight the selected calendar when tab shows
			public void widgetSelected(SelectionEvent e) {
				if (e.item.equals(calendarTab)) {
					calendarViewer.getTable().setFocus();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		dateTab = new TabItem(tabFolder, SWT.NULL);
		dateTab.setText("Dates");
		calendarTab = new TabItem(tabFolder, SWT.NULL);
		calendarTab.setText("Trading Calendar");
		editor = new StartEndDateEditor(tabFolder, startDate, endDate, movingWindow);
		dateTab.setControl(editor);
		
		Composite calendarViewerContainer = new Composite(tabFolder, SWT.NULL);
		calendarViewerContainer.setLayout(new FillLayout());
		calendarTab.setControl(calendarViewerContainer);
		
		calendarViewer = new TableViewer(calendarViewerContainer, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		
		TableViewerColumn viewColName = new TableViewerColumn(calendarViewer, SWT.NONE);
		viewColName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ITradingCalendar calendar = (ITradingCalendar) element;
				return calendar.getName();
			}	
		});
		TableColumn columnName = viewColName.getColumn();
		columnName.setText("Name");
		columnName.setWidth(150);
		columnName.setResizable(true);
		
		TableViewerColumn viewColDesc = new TableViewerColumn(calendarViewer, SWT.NONE);
		viewColDesc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ITradingCalendar calendar = (ITradingCalendar) element;
				return calendar.getDescription();
			}	
		});
		TableColumn columnDesc = viewColDesc.getColumn();
		columnDesc.setText("Description");
		columnDesc.setWidth(200);
		columnDesc.setResizable(true);
		
		Table table = calendarViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		calendarViewer.setContentProvider(new ArrayContentProvider());
		calendarViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection != null) {
					Object firstElement = selection.getFirstElement();
					if (firstElement != null && firstElement instanceof ITradingCalendar) {
						tradingCalendar = (ITradingCalendar) firstElement;
					}
				}
			}});
		calendarViewer.setInput(tradingCalendars);
		String currentCaledarName = chartModel.getTradingCalendar().getName();
		for (int i = 0; i < tradingCalendars.size(); i++) {
			ITradingCalendar calendar = tradingCalendars.get(i);
			if (calendar != null && calendar.getName().equals(currentCaledarName)) {
				table.setSelection(i);
				break;
			}
		}
		return tabFolder;
	}
	
	public Date getStartDate() {
		return editor.getStartDate();
	}
	
	public Date getEndDate() {
		return editor.getEndDate();
	}
	
	public boolean isMovingWindow() {
		return editor.isMovingWindow();
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.PROCEED_ID, "Apply", false);
		super.createButtonsForButtonBar(parent);
	}
	
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.PROCEED_ID) {
			executeChange();
		} else {
			super.buttonPressed(buttonId);
		}
	}
	
	@Override
	protected void okPressed() {
		if (!getStartDate().before(getEndDate())) {
			MessageDialog.openError(getParentShell(), "Error", "Start date must precede end date");
			return;
		}
		if (executeChange()) {
			setReturnCode(OK);
			close();
		}
	}
	
	private boolean executeChange() {
		final Date newStartDate = getStartDate();
		final Date newEndDate = getEndDate();
		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
			dialog.setCancelable(true);
			dialog.run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						chartModel.setSuspendUpdates(true);
						stockDatabase.getParent().fillHistoricalData(stockDatabase, newStartDate, newEndDate, new TaskMonitorAdapter(monitor, "Retrieving historical data..."));
						chartModel.setSuspendUpdates(false);
					} catch (Exception e) {
						MessageDialog.openError(getShell(), "Error", "Error while retrieving historical data: " + LangUtils.exceptionMessage(e));
					}
				}});
		} catch (InvocationTargetException e) {
			MessageDialog.openError(getShell(), "Error", "A problem occurred while retrieving historical data");
			return false;
		} catch (InterruptedException e) {
			MessageDialog.openError(getShell(), "Error", "Task interrupted while retrieving historical data: " + LangUtils.exceptionMessage(e));
			return false;
		} 
		if (isMovingWindow()) {
			chartModel.setFixedDurationWindow(newStartDate, tradingCalendar);
		} else {
			chartModel.setFixedWindow(newStartDate, newEndDate, tradingCalendar);
		}
		return true;
	}
}
