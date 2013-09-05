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
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.quantcomponents.core.exceptions.RequestFailedException;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.ITaskMonitor;
import com.quantcomponents.core.model.TimePeriod;
import com.quantcomponents.core.model.beans.ContractBean;
import com.quantcomponents.core.utils.LangUtils;
import com.quantcomponents.marketdata.IRealTimeMarketDataManager;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.ui.core.TaskMonitorAdapter;

public class NewHistoricalData extends Wizard implements IPageChangingListener {
	private final MarketDataManagerPresentationWrapper marketDataManager;
	
	private ContractBean contractCriteria;
	private NewHistoricalDataPage1 page1;
	private NewHistoricalDataPage2 page2;
	private NewHistoricalDataPage3 page3;
	
	private Date startDateTime;
	private Date endDateTime;
	
	public NewHistoricalData(MarketDataManagerPresentationWrapper marketDataManager) {
		this.marketDataManager = marketDataManager;
		this.contractCriteria = new ContractBean();
	}
	
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		IWizardContainer container = getContainer();
		if (container instanceof WizardDialog) {
			WizardDialog dialog = (WizardDialog) container;
			dialog.addPageChangingListener(this);
		}
	}
	
	@Override
	public void addPages() {
		page1 = new NewHistoricalDataPage1(contractCriteria);
		addPage(page1);
		page2 = new NewHistoricalDataPage2();
		addPage(page2);
		page3 = new NewHistoricalDataPage3(marketDataManager);
		addPage(page3);
	}
	
	private List<IContract> searchContracts() {
		final List<IContract> contractList = new ArrayList<IContract>();
		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
			dialog.setCancelable(true);
			dialog.run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						contractList.addAll(marketDataManager.searchContracts(contractCriteria, new TaskMonitorAdapter(monitor, "Searching contracts...")));
					} catch (ConnectException e) {
						throw new InvocationTargetException(e);
					} catch (RequestFailedException e) {
						throw new InvocationTargetException(e);
					}
				}});
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "Error", "IB contract search error: " + LangUtils.exceptionMessage(e));
			return Collections.emptyList();
		}
		if (contractList == null || contractList.isEmpty()) {
			MessageDialog.openError(this.getShell(), "No results", "Search for contracts returned empty set. Please revise your criteria");
			return Collections.emptyList();
		}
		return contractList;
	}
		
	@Override
	public void handlePageChanging(PageChangingEvent event) {
		if (event.getCurrentPage() == page1 && event.getTargetPage() == page2) {
			List<IContract> contracts = searchContracts();
			if (contracts.isEmpty()) {
				event.doit = false;
			} else {
				page2.getContractListViewer().setInput(contracts);
			}
		} else if (event.getTargetPage() == page3) {
			page3.setSelectedContract(page2.getSelectedContract());
		}
	}
	
	private void displayErrorMessage(String title, String message, String exceptionMessage) {
		if (exceptionMessage.length() > 0) {
			message += " [" + exceptionMessage + "]";
		}
		MessageDialog.openError(this.getShell(), title, message);
	}
	
	@Override
	public boolean performFinish() {
		boolean realtimeUpdate = page3.isRealtimeUpdate();
		final StockDatabasePresentationWrapper stockDatabase = marketDataManager.createStockDatabase(page2.getSelectedContract(), page3.getDataType(), page3.getBarSize(), page3.isAfterHoursIncluded(), page3.getTimeZone());
		try {
			TimePeriod period = new TimePeriod(page3.getPeriodUnit(), page3.getPeriodAmount());
			endDateTime = null;
			if (realtimeUpdate) {
				endDateTime = new Date();
			} else {
				endDateTime = page3.getEndDate();
			}
			startDateTime = TimePeriod.subtractPeriodFromDate(endDateTime, period);
			retrieveHistoricalData(stockDatabase, startDateTime, endDateTime);
		} catch (InvocationTargetException e) {
			if (!MessageDialog.openConfirm(this.getShell(), "Historical data error", "IB server error while retrieving historical data [" + e.getCause().getMessage() + "]\nContinue?")) {
				return false;
			}
		} catch (InterruptedException e) {
			displayErrorMessage("Historical data error", "Task interrupted while retrieving historical data", e.getMessage());
			return false;
		} 
		try {
			if (realtimeUpdate) {
				startAutoUpdate(stockDatabase);
			}
			IBaseLabelProvider labelDecorator = PlatformUI.getWorkbench().getDecoratorManager().getBaseLabelProvider(AutoUpdateIconDecorator.DECORATOR_ID);
			if (labelDecorator != null) { // it is enabled
				AutoUpdateIconDecorator autoUpdateIconDecorator = (AutoUpdateIconDecorator) labelDecorator;
				autoUpdateIconDecorator.fireLabelProviderChanged(stockDatabase);
			}
		} catch (InvocationTargetException e) {
			displayErrorMessage("Auto-update error", "IB server error while setting auto-update", e.getCause().getMessage());
		} catch (InterruptedException e) {
			displayErrorMessage("Auto-update error", "Task interrupted while setting auto-update", e.getMessage());
		} catch (Exception e) {
			displayErrorMessage("Auto-update error", "Error while setting auto-update", e.getMessage());
		}
		return true;
	}
	
	private void retrieveHistoricalData(final IStockDatabase stockDatabase, final Date startDateTime, final Date endDateTime) throws InvocationTargetException, InterruptedException {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		dialog.setCancelable(true);
		dialog.run(true, true, new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					ITaskMonitor taskMonitor = new TaskMonitorAdapter(monitor, "Retrieving historical data...");
					marketDataManager.fillHistoricalData(stockDatabase, startDateTime, endDateTime, taskMonitor);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				} 
			}});
	}
	
	private void startAutoUpdate(final IStockDatabase stockDatabase) throws InvocationTargetException, InterruptedException {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		dialog.setCancelable(true);
		final IRealTimeMarketDataManager realTimeMarketDataManager = (IRealTimeMarketDataManager) marketDataManager;
		dialog.run(true, true, new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					ITaskMonitor taskMonitor = new TaskMonitorAdapter(monitor, "Starting auto-update...");
					realTimeMarketDataManager.startRealtimeUpdate(stockDatabase, true, taskMonitor);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				} 
			}});
	}
}
