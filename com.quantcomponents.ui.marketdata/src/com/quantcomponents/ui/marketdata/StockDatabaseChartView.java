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

import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.quantcomponents.chart.series.CandlestickRenderer;
import com.quantcomponents.chart.series.ITimeSeriesChartModel;
import com.quantcomponents.chart.series.ITimeSeriesChartModelListener;
import com.quantcomponents.chart.series.TimeSeriesChart;
import com.quantcomponents.chart.series.OHLCSeriesRenderer;
import com.quantcomponents.chart.series.TimeSeriesChartModel;
import com.quantcomponents.core.calendar.ITradingCalendar;
import com.quantcomponents.core.calendar.ITradingCalendarManager;
import com.quantcomponents.core.model.ISeries;
import com.quantcomponents.marketdata.IMarketDataManager;
import com.quantcomponents.marketdata.IOHLCPoint;
import com.quantcomponents.marketdata.IOHLCTimeSeries;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.ui.core.IMonitorableContainerListener;

public class StockDatabaseChartView extends ViewPart implements ISelectionListener, IMonitorableContainerListener<StockDatabasePresentationWrapper>, ITimeSeriesChartModelListener<IOHLCPoint> {
	public static final String SINGLETON_STOCK_DB_VIEW_ID = "com.quantcomponents.ui.marketdata.stockDatabaseChart";
	public static final String MULTI_STOCK_DB_VIEW_ID = "com.quantcomponents.ui.marketdata.stockDatabaseChartMulti";
	private static final int DEFAULT_INITIAL_NUMBER_OF_BARS = 200;
	private Composite parent;
	private ITradingCalendarManager tradingCalendarManager;
	private StockDatabasePresentationWrapper stockDatabase;
	private IOHLCTimeSeries timeSeries;
	private TimeSeriesChart chart;
	private ITimeSeriesChartModel<IOHLCPoint> chartModel;
	private Action showChartDialog;

	@Override
    public void init(IViewSite site) throws PartInitException {
		super.init(site);
		tradingCalendarManager = MarketDataPlugin.getDefault().getTradingCalendarManager();
    }
    
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		chart = new TimeSeriesChart(parent, SWT.NONE);
		chart.setSeriesRenderer(new OHLCSeriesRenderer(new CandlestickRenderer()));
		getSite().getPage().addSelectionListener(this);
		createActions();
		createContextMenu();
		if (getViewSite().getId().equals(MULTI_STOCK_DB_VIEW_ID)) {
			setupFromSecondaryId(getViewSite().getSecondaryId());
		}
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (getViewSite().getId().equals(SINGLETON_STOCK_DB_VIEW_ID)) {
			setupFromSelection(selection);
		}
	}

	@Override
	public void setFocus() {}

	private void createActions() {
		showChartDialog = new Action("Setup") {
			@Override
			public void run() {
				List<ITradingCalendar> tradingCalendars = tradingCalendarManager.findTradingCalendars(timeSeries.getContract(), timeSeries.isIncludeAfterHours());
				new TimeChartDialog<IOHLCPoint>(chartModel, stockDatabase, tradingCalendars, parent.getShell()).open();
				chart.setSeries(chartModel.data());
				chart.setTradingCalendar(chartModel.getTradingCalendar());
				chart.refresh();
			}
		};
	}
	
	private void createContextMenu() {
		// Create menu manager.
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(mgr);
			}
		});

		// Create menu.
		Menu menu = menuMgr.createContextMenu(chart.getControl());
		chart.getControl().setMenu(menu);

		// Register menu for extension.
		getSite().registerContextMenu(menuMgr, null);
	}
	
	private void fillContextMenu(IMenuManager menuMgr) {
		menuMgr.add(showChartDialog);
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	@Override
	public void onElementAdded(StockDatabasePresentationWrapper element) {}

	@Override
	public void onElementRemoved(StockDatabasePresentationWrapper element) {
		if (element.getPrettyName().equals(stockDatabase.getPrettyName())) {
			// stockDatabase = null; TODO check if it is needed
			chart.setSeries(null);
			chart.refresh();
		}
	}

	@Override
	public void onElementModified(StockDatabasePresentationWrapper element) {}

	@Override
	public void onModelUpdated(ISeries<Date, Double, IOHLCPoint> series) {
		chart.setSeries(series);
		chart.refresh();
	}
	
	public void setupFromSecondaryId(String secondaryId) {
		setPartName(secondaryId);
		for (IMarketDataManager marketDataManager : MarketDataPlugin.getDefault().getMarketDataManagerContainer().getElements()) {
			for (IStockDatabase stockDb : marketDataManager.allStockDatabases()) {
				if (stockDb instanceof StockDatabasePresentationWrapper) {
					StockDatabasePresentationWrapper stockDbWrapper = (StockDatabasePresentationWrapper) stockDb;
					if (stockDbWrapper.getPrettyName().equals(secondaryId)) {
						this.stockDatabase = stockDbWrapper;
						setupFromStockDatabase();
						return;
					}
				}
			}
		}
	}
	
	private void setupFromStockDatabase() {
		timeSeries = stockDatabase.getVirtualTimeSeries();
		List<ITradingCalendar> tradingCalendars = tradingCalendarManager.findTradingCalendars(timeSeries.getContract(), timeSeries.isIncludeAfterHours());
		ITradingCalendar tradingCalendar = tradingCalendars.get(0);
		chartModel = new TimeSeriesChartModel<IOHLCPoint>(timeSeries, tradingCalendar);
		chartModel.addListener(this);
		chartModel.setFixedDurationWindow(DEFAULT_INITIAL_NUMBER_OF_BARS, tradingCalendar);
		chart.setSeries(chartModel.data());
		chart.setTradingCalendar(tradingCalendar);
		chart.setPointInterval(timeSeries.getInterval());
		chart.refresh();
		stockDatabase.getParent().addListener(this);
	}
	
	private void setupFromSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object o = structuredSelection.getFirstElement();
			if (o instanceof StockDatabasePresentationWrapper) {
				if (stockDatabase != null) {
					if (((StockDatabasePresentationWrapper) o).getPersistentID().equals(stockDatabase.getPersistentID())) {
						return;
					}
					stockDatabase.getParent().removeListener(this);
				}
				if (chartModel != null) {
					chartModel.removeListener(this);
				}
				stockDatabase = (StockDatabasePresentationWrapper) o;
				setupFromStockDatabase();
				return;
			} 
		} 
	}
}
