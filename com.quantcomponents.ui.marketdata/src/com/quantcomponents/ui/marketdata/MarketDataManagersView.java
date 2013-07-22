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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

import com.quantcomponents.core.utils.LangUtils;
import com.quantcomponents.marketdata.IRealTimeMarketDataManager;
import com.quantcomponents.ui.core.IMonitorableContainer;
import com.quantcomponents.ui.core.IMonitorableContainerListener;
import com.quantcomponents.ui.core.TaskMonitorAdapter;

public class MarketDataManagersView extends ViewPart {
	public static final String VIEW_ID = "com.quantcomponent.ui.marketdata.marketDataManagers";
	private IMonitorableContainer<MarketDataManagerPresentationWrapper> marketDataManagerContainer;
	private MarketDataManagerPresentationWrapper selectedMarketDataManager;
	private final List<StockDatabasePresentationWrapper> multipleStockDatabaseSelection = new ArrayList<StockDatabasePresentationWrapper>();
	private Composite parent;
	private TreeViewer dataManagerTree;
	private final MarketDataViewAdapterFactory adapterFactory = new MarketDataViewAdapterFactory();
	private final Action addStockDatabaseAction = new Action("Add Stock Database...") {
		@Override
		public void run() {
			if (selectedMarketDataManager != null) {
				WizardDialog dialog = new WizardDialog(parent.getShell(), new NewHistoricalData(selectedMarketDataManager));
				dialog.open();
			}
		}
	};
	private final Action removeStockDatabaseAction = new Action("Remove") {
		@Override
		public void run() {
			if (selectedMarketDataManager != null && multipleStockDatabaseSelection.size() > 0) {
				if (MessageDialog.openConfirm(parent.getShell(), "Confirmation", "Delete " + multipleStockDatabaseSelection.size() + " stock database(s)?")) {
					for (StockDatabasePresentationWrapper selectedStockDatabase : multipleStockDatabaseSelection) {
						try {
							selectedMarketDataManager.removeStockDatabase(selectedStockDatabase);
						} catch (Exception e) {
							MessageDialog.openError(parent.getShell(), "Error", "Stock database deletion failed: " + LangUtils.exceptionMessage(e));
							return;
						}
					}
					refreshTree();
					multipleStockDatabaseSelection.clear();
				}
			}
		}
	};
	private final Action openViewAction = new Action("Open view") {
		@Override
		public void run() {
			if (selectedMarketDataManager != null && multipleStockDatabaseSelection.size() > 0) {
				for (StockDatabasePresentationWrapper selectedStockDatabase : multipleStockDatabaseSelection) {
					try {
						getSite().getPage().showView(StockDatabaseChartView.MULTI_STOCK_DB_VIEW_ID, selectedStockDatabase.getPrettyName(), IWorkbenchPage.VIEW_VISIBLE);
					} catch (Exception e) {
						MessageDialog.openError(parent.getShell(), "Error", "A problem occurred while opening view for: " + selectedStockDatabase.getPrettyName() + "[" + LangUtils.exceptionMessage(e) + "]");
					}
				}
			}
		}
	};
	private final Action refreshAction = new Action("Refresh") {
		@Override
		public void run() {
			refreshTree();
		}
	};
	private final Action startAutoUpdateAction = new Action("Start auto-update") {
		@Override
		public void run() {
			if (selectedMarketDataManager != null && multipleStockDatabaseSelection.size() > 0) {
				try {
					for (final StockDatabasePresentationWrapper selectedStockDatabase : multipleStockDatabaseSelection) {
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(parent.getShell());
						dialog.setCancelable(true);
						dialog.run(true, true, new IRunnableWithProgress() {
							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
								try {
									IRealTimeMarketDataManager realTimeMarketDataManager = (IRealTimeMarketDataManager) selectedMarketDataManager;
									realTimeMarketDataManager.startRealtimeUpdate(selectedStockDatabase, true, new TaskMonitorAdapter(monitor, "Retrieving historical data"));
								} catch (Exception e) {
									MessageDialog.openError(parent.getShell(), "Error", "Error while retrieving historical data: " + LangUtils.exceptionMessage(e));
								}
							}});
						IBaseLabelProvider labelDecorator = PlatformUI.getWorkbench().getDecoratorManager().getBaseLabelProvider(AutoUpdateIconDecorator.DECORATOR_ID);
						if (labelDecorator != null) { // it is enabled
							AutoUpdateIconDecorator autoUpdateIconDecorator = (AutoUpdateIconDecorator) labelDecorator;
							autoUpdateIconDecorator.fireLabelProviderChanged(selectedStockDatabase);
						}
					}
				} catch (InvocationTargetException e) {
					MessageDialog.openError(parent.getShell(), "Error", "A problem occurred while starting auto-update: " + LangUtils.exceptionMessage(e));
				} catch (InterruptedException e) {
					MessageDialog.openError(parent.getShell(), "Error", "Task interrupted while starting auto-update: " + LangUtils.exceptionMessage(e));
				} catch (ClassCastException e) {
					MessageDialog.openError(parent.getShell(), "Error", "Market data source does not support real-time update");
				} 
			}
		}
	};
	private final Action stopAutoUpdateAction = new Action("Stop auto-update") {
		@Override
		public void run() {
			if (selectedMarketDataManager != null && multipleStockDatabaseSelection.size() > 0) {
				for (StockDatabasePresentationWrapper selectedStockDatabase : multipleStockDatabaseSelection) {
					try {
						IRealTimeMarketDataManager realTimeMarketDataManager = (IRealTimeMarketDataManager) selectedMarketDataManager;
						realTimeMarketDataManager.stopRealtimeUpdate(selectedStockDatabase);
					} catch (ClassCastException e) {
						MessageDialog.openError(parent.getShell(), "Error", "Market data source does not support real-time update");
					} catch (Exception e) {
						MessageDialog.openError(parent.getShell(), "Error", "A problem occurred while stopping auto-update: " + LangUtils.exceptionMessage(e));
					} 
					IBaseLabelProvider labelDecorator = PlatformUI.getWorkbench().getDecoratorManager().getBaseLabelProvider(AutoUpdateIconDecorator.DECORATOR_ID);
					if (labelDecorator != null) { // it is enabled
						AutoUpdateIconDecorator autoUpdateIconDecorator = (AutoUpdateIconDecorator) labelDecorator;
						autoUpdateIconDecorator.fireLabelProviderChanged(selectedStockDatabase);
					}
				}
			}
		}
	};

	private final IMonitorableContainerListener<MarketDataManagerPresentationWrapper> marketDataManagerContainerListener = new IMonitorableContainerListener<MarketDataManagerPresentationWrapper>() {
		@Override
		public void onElementAdded(MarketDataManagerPresentationWrapper manager) {
			manager.addListener(stockDatabaseContainerListener);
			refreshTree();
			for (StockDatabasePresentationWrapper stockDatabase : manager.getElements()) {
				showChart(stockDatabase);
			}
		}

		@Override
		public void onElementRemoved(MarketDataManagerPresentationWrapper manager) {
			manager.removeListener(stockDatabaseContainerListener);
			refreshTree();
			for (StockDatabasePresentationWrapper stockDatabase : manager.getElements()) {
				hideChart(stockDatabase);
			}
		}
		
		@Override
		public void onElementModified(MarketDataManagerPresentationWrapper element) {}
	};
	
	private final IMonitorableContainerListener<StockDatabasePresentationWrapper> stockDatabaseContainerListener = new IMonitorableContainerListener<StockDatabasePresentationWrapper>() {

		@Override
		public void onElementAdded(StockDatabasePresentationWrapper element) {
			refreshTree();
			showChart(element);
		}

		@Override
		public void onElementRemoved(StockDatabasePresentationWrapper element) {
			refreshTree();
			hideChart(element);
		}

		@Override
		public void onElementModified(StockDatabasePresentationWrapper element) {}
	};
	
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		marketDataManagerContainer = MarketDataPlugin.getDefault().getMarketDataManagerContainer();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		dataManagerTree = new TreeViewer(parent);
		
		dataManagerTree.setContentProvider(new BaseWorkbenchContentProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				super.inputChanged(viewer, oldInput, newInput);
				if (oldInput != null) {
					((IMonitorableContainer<MarketDataManagerPresentationWrapper>) oldInput).removeListener(marketDataManagerContainerListener);
				}
				if (newInput != null) {
					IMonitorableContainer<MarketDataManagerPresentationWrapper> container = (IMonitorableContainer<MarketDataManagerPresentationWrapper>) newInput;
					container.addListener(marketDataManagerContainerListener);
					for (MarketDataManagerPresentationWrapper manager : container.getElements()) {
						marketDataManagerContainerListener.onElementAdded(manager);
					}
				}
			}
		});
		
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		dataManagerTree.setLabelProvider(new DecoratingLabelProvider(new WorkbenchLabelProvider(), decorator));
		
		Platform.getAdapterManager().registerAdapters(adapterFactory, IMarketDataManagerContainer.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory, MarketDataManagerPresentationWrapper.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory, StockDatabasePresentationWrapper.class);
		
		dataManagerTree.setInput(marketDataManagerContainer);
		
		dataManagerTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				multipleStockDatabaseSelection.clear();
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					Object o = structuredSelection.getFirstElement();
					if (o instanceof MarketDataManagerPresentationWrapper) {
						selectedMarketDataManager = (MarketDataManagerPresentationWrapper) o;
						openViewAction.setEnabled(false);
						addStockDatabaseAction.setEnabled(true);
						removeStockDatabaseAction.setEnabled(false);
						if (selectedMarketDataManager instanceof RealTimeMarketDataManagerPresentationWrapper) {
							startAutoUpdateAction.setEnabled(false);
							stopAutoUpdateAction.setEnabled(false);
						}
					} else if (o instanceof StockDatabasePresentationWrapper) {
						openViewAction.setEnabled(true);
						addStockDatabaseAction.setEnabled(true);
						removeStockDatabaseAction.setEnabled(true);
						if (selectedMarketDataManager instanceof RealTimeMarketDataManagerPresentationWrapper) {
							startAutoUpdateAction.setEnabled(false);
							stopAutoUpdateAction.setEnabled(false);
						}
						if (structuredSelection.size() > 0) {
							Iterator<?> iterator = structuredSelection.iterator();
							while (iterator.hasNext()) {
								Object sel = iterator.next();
								if (sel instanceof StockDatabasePresentationWrapper) {
									StockDatabasePresentationWrapper stockDatabaseWrapper = (StockDatabasePresentationWrapper) sel;
									selectedMarketDataManager = stockDatabaseWrapper.getParent();
									multipleStockDatabaseSelection.add(stockDatabaseWrapper);
								}
							}
						} 
					} else {
						openViewAction.setEnabled(false);
						addStockDatabaseAction.setEnabled(false);
						removeStockDatabaseAction.setEnabled(false);
						startAutoUpdateAction.setEnabled(false);
						stopAutoUpdateAction.setEnabled(false);
					}
				}
			}
		});
		dataManagerTree.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					Object o = structuredSelection.getFirstElement();
					if (o instanceof StockDatabasePresentationWrapper) {
						StockDatabasePresentationWrapper selectedStockDatabase = (StockDatabasePresentationWrapper) o;
						try {
							getSite().getPage().showView(StockDatabaseChartView.MULTI_STOCK_DB_VIEW_ID, selectedStockDatabase.getPrettyName(), IWorkbenchPage.VIEW_VISIBLE);
						} catch (PartInitException e) {
							MessageDialog.openError(MarketDataManagersView.this.parent.getShell(), "Error", "A problem occurred while opening view for: " + selectedStockDatabase.getPrettyName() + "[" + LangUtils.exceptionMessage(e) + "]");
						}
					}
				}
			}});
		
		getSite().setSelectionProvider(dataManagerTree);
		hookGlobalActions(); 
		createContextMenu();
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
		Menu menu = menuMgr.createContextMenu(dataManagerTree.getControl());
		dataManagerTree.getControl().setMenu(menu);

		// Register menu for extension.
		getSite().registerContextMenu(menuMgr, dataManagerTree);
	}

	private void fillContextMenu(IMenuManager menuMgr) {
		if (multipleStockDatabaseSelection.size() > 0) {
			menuMgr.add(openViewAction);
			menuMgr.add(removeStockDatabaseAction);
			if (selectedMarketDataManager instanceof RealTimeMarketDataManagerPresentationWrapper) {
				startAutoUpdateAction.setEnabled(false);
				stopAutoUpdateAction.setEnabled(false);
			}
		}
		if (selectedMarketDataManager != null) {
			menuMgr.add(addStockDatabaseAction);
			menuMgr.add(refreshAction);
		}
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void hookGlobalActions() {
		IActionBars bars = getViewSite().getActionBars();
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), removeStockDatabaseAction);
	}

	@Override
	public void setFocus() {
	}
	
	@Override
	public void dispose() {
		Platform.getAdapterManager().unregisterAdapters(adapterFactory);
		super.dispose();
	}

	private void hideChart(StockDatabasePresentationWrapper stockDatabase) {
		IViewReference chartViewRef = getViewSite().getPage().findViewReference(StockDatabaseChartView.MULTI_STOCK_DB_VIEW_ID, stockDatabase.getPrettyName());
		if (chartViewRef != null) {
			getViewSite().getPage().hideView(chartViewRef);
		}
	}
	
	private void showChart(StockDatabasePresentationWrapper stockDatabase) {
		String secondaryId = stockDatabase.getPrettyName();
		IViewReference chartViewRef = getViewSite().getPage().findViewReference(StockDatabaseChartView.MULTI_STOCK_DB_VIEW_ID, secondaryId);
		if (chartViewRef != null) {
			StockDatabaseChartView view = (StockDatabaseChartView) chartViewRef.getView(false);
			if (view != null) {
				view.setupFromSecondaryId(secondaryId);
			}
		}
	}
	
	private void refreshTree() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				dataManagerTree.refresh();
			}
		});
	}
}