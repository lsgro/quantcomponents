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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

import com.quantcomponents.algo.ExecutionCreationException;
import com.quantcomponents.algo.ExecutionType;
import com.quantcomponents.algo.TradingAgentBindingHandle;
import com.quantcomponents.algo.TradingAgentConfigurationHandle;
import com.quantcomponents.algo.TradingAgentExecutionHandle;
import com.quantcomponents.algo.TradingAgentFactoryHandle;
import com.quantcomponents.algo.IManagedRunnable.RunningStatus;
import com.quantcomponents.algo.IStockDatabaseTradingManager;
import com.quantcomponents.core.utils.LangUtils;
import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.ui.core.IMonitorableContainer;
import com.quantcomponents.ui.core.IMonitorableContainerListener;

public class TradingManagersView extends ViewPart {
	public static final String VIEW_ID ="com.quantcomponents.ui.algo.tradingManagers";
	private static final long EXECUTION_DECORATOR_CHECK_INTERVAL = 500L;
	private IMonitorableContainer<IStockDatabaseTradingManager> tradingAgentManagerContainer;
	private IStockDatabaseTradingManager selectedTradingAgentManager;
	private TradingAgentFactoryHandle selectedTradingAgentFactory;
	private TradingAgentConfigurationHandle selectedTradingAgentConfiguration;
	private TradingAgentBindingHandle selectedTradingAgentBinding;
	private TradingAgentExecutionHandle selectedTradingAgentExecution;
	private Object currentlySelectedObject;
	private Composite parent;
	private TreeViewer tradingAgentTree;
	private TradingManagersViewAdapterFactory adapterFactory;
	
	private final Action refreshTree = new Action("Refresh") {
		@Override
		public void run() {
			refreshTree();
		}
	};

	private final Action createTradingAgentConfiguration = new Action("Create agent configuration...") {
		@Override
		public void run() {
			if (selectedTradingAgentFactory != null) {
				Properties configProperties = new Properties();
				String[] configurationKeys = selectedTradingAgentFactory.getConfigurationKeys();
				if (configurationKeys != null && configurationKeys.length > 0) {
					for (String key : configurationKeys) {
						configProperties.put(key, "");
					}
					TradingAgentConfigurationDialog dialog = new TradingAgentConfigurationDialog(configurationKeys, configProperties, true, null, parent.getShell());
					if (dialog.open() == Dialog.OK) {
						Map<String, String> errorMsgs = new HashMap<String, String>();
						if (selectedTradingAgentManager.isConfigurationValid(selectedTradingAgentFactory, configProperties, errorMsgs)) {
							selectedTradingAgentManager.createConfiguration(selectedTradingAgentFactory, configProperties, dialog.getName());
							refreshTree();
						} else {
							StringBuilder buffer = new StringBuilder();
							for (Map.Entry<String, String> entry : errorMsgs.entrySet()) {
								buffer.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
							}
							MessageDialog.openError(parent.getShell(), "Configuration not valid", buffer.toString());
						}
					}
				}
			}
		}
	};

	private final Action viewTradingAgentConfiguration = new Action("View") {
		@Override
		public void run() {
			if (selectedTradingAgentConfiguration != null) {
				String[] configurationKeys = selectedTradingAgentFactory.getConfigurationKeys();
				Properties configProperties = selectedTradingAgentManager.getConfigurationProperties(selectedTradingAgentConfiguration);
				new TradingAgentConfigurationDialog(configurationKeys, configProperties, false, selectedTradingAgentConfiguration.getPrettyName(), parent.getShell()).open();
			}
		}
	};

	private final Action createTradingAgentBinding = new Action("Bind inputs...") {
		@Override
		public void run() {
			if (selectedTradingAgentConfiguration != null) {
				Map<String, IStockDatabase> inputStockDatabases = new HashMap<String, IStockDatabase>();
				for (String inputName : selectedTradingAgentConfiguration.getInputSeriesNames()) {
					inputStockDatabases.put(inputName, null);
				}
				TradingAgentBindingDialog dialog = new TradingAgentBindingDialog(inputStockDatabases, true, null, parent.getShell());
				if (dialog.open() != Dialog.OK) {
					return;
				}
				selectedTradingAgentManager.createBinding(selectedTradingAgentConfiguration, inputStockDatabases, dialog.getName());
				refreshTree();
			}
		}
	};
	
	private final Action viewTradingAgentBinding = new Action("View") {
		@Override
		public void run() {
			if (selectedTradingAgentBinding != null) {
				Map<String, IStockDatabase> inputStockDatabases = selectedTradingAgentManager.getBindingInputStockDatabases(selectedTradingAgentBinding);
				new TradingAgentBindingDialog(inputStockDatabases, false, selectedTradingAgentBinding.getPrettyName(), parent.getShell()).open();
			}
		}
	};
	
	private final Action executeSimulatedTradingAgentBinding = new Action("Start backtest") {
		@Override
		public void run() {
			if (selectedTradingAgentBinding != null) {
				TradingAgentExecutionHandle execution;
				try {
					execution = selectedTradingAgentManager.createExecution(selectedTradingAgentBinding, ExecutionType.BACKTEST);
				} catch (ExecutionCreationException e) {
					MessageDialog.openError(parent.getShell(), "Execution creation error", LangUtils.exceptionMessage(e));
					return;
				}
				refreshTree();
				selectedTradingAgentManager.startExecution(execution);
				TradingAgentExecutionWrapper wrapper = adapterFactory.getOrCreateAgentExecutionWrapper(execution, selectedTradingAgentManager);
				new Thread(new ExecutionLabelDecoratorMonitor(selectedTradingAgentManager, execution, wrapper, EXECUTION_DECORATOR_CHECK_INTERVAL)).start();
			}
		}
	};
	
	private final Action executeLiveTradingAgentBinding = new Action("Start live execution") {
		@Override
		public void run() {
			if (selectedTradingAgentBinding != null) {
				TradingAgentExecutionHandle execution;
				try {
					execution = selectedTradingAgentManager.createExecution(selectedTradingAgentBinding, ExecutionType.LIVE);
				} catch (ExecutionCreationException e) {
					MessageDialog.openError(parent.getShell(), "Execution creation error", LangUtils.exceptionMessage(e));
					return;
				}
				refreshTree();
				selectedTradingAgentManager.startExecution(execution);
				TradingAgentExecutionWrapper wrapper = adapterFactory.getOrCreateAgentExecutionWrapper(execution, selectedTradingAgentManager);
				new Thread(new ExecutionLabelDecoratorMonitor(selectedTradingAgentManager, execution, wrapper, EXECUTION_DECORATOR_CHECK_INTERVAL)).start();
			}
		}
	};
	
	private final Action pauseExecution = new Action("Pause execution") {
		@Override
		public void run() {
			if (selectedTradingAgentExecution != null) {
				if (selectedTradingAgentManager.getRunningStatus(selectedTradingAgentExecution) == RunningStatus.RUNNING) {
					MessageDialog.openError(parent.getShell(), "Wrong status", "Only running execution can be paused");
					return;
				}
				selectedTradingAgentManager.pauseExecution(selectedTradingAgentExecution);
				TradingAgentExecutionWrapper wrapper = adapterFactory.getOrCreateAgentExecutionWrapper(selectedTradingAgentExecution, selectedTradingAgentManager);
				IBaseLabelProvider labelDecorator = PlatformUI.getWorkbench().getDecoratorManager().getBaseLabelProvider(ExecutionRunningDecorator.DECORATOR_ID);
				if (labelDecorator != null) { // it is enabled
					ExecutionRunningDecorator executionRunningIconDecorator = (ExecutionRunningDecorator) labelDecorator;
					executionRunningIconDecorator.fireLabelProviderChanged(wrapper);
				}
			}
		}
	};
	
	private final Action resumeExecution = new Action("Resume execution") {
		@Override
		public void run() {
			if (selectedTradingAgentExecution != null) {
				if (selectedTradingAgentManager.getRunningStatus(selectedTradingAgentExecution) != RunningStatus.PAUSED) {
					MessageDialog.openError(parent.getShell(), "Wrong status", "Only paused execution can be resumed");
					return;
				}
				selectedTradingAgentManager.resumeExecution(selectedTradingAgentExecution);
				TradingAgentExecutionWrapper wrapper = adapterFactory.getOrCreateAgentExecutionWrapper(selectedTradingAgentExecution, selectedTradingAgentManager);
				IBaseLabelProvider labelDecorator = PlatformUI.getWorkbench().getDecoratorManager().getBaseLabelProvider(ExecutionRunningDecorator.DECORATOR_ID);
				if (labelDecorator != null) { // it is enabled
					ExecutionRunningDecorator executionRunningIconDecorator = (ExecutionRunningDecorator) labelDecorator;
					executionRunningIconDecorator.fireLabelProviderChanged(wrapper);
				}
			}
		}
	};
	
	private final Action killExecution = new Action("Kill execution") {
		@Override
		public void run() {
			if (selectedTradingAgentExecution != null) {
				if (selectedTradingAgentManager.getRunningStatus(selectedTradingAgentExecution) != RunningStatus.RUNNING) {
					MessageDialog.openError(parent.getShell(), "Wrong status", "Only running execution can be killed");
					return;
				}
				selectedTradingAgentManager.killExecution(selectedTradingAgentExecution);
				TradingAgentExecutionWrapper wrapper = adapterFactory.getOrCreateAgentExecutionWrapper(selectedTradingAgentExecution, selectedTradingAgentManager);
				IBaseLabelProvider labelDecorator = PlatformUI.getWorkbench().getDecoratorManager().getBaseLabelProvider(ExecutionRunningDecorator.DECORATOR_ID);
				if (labelDecorator != null) { // it is enabled
					ExecutionRunningDecorator executionRunningIconDecorator = (ExecutionRunningDecorator) labelDecorator;
					executionRunningIconDecorator.fireLabelProviderChanged(wrapper);
				}
			}
		}
	};
	
	private final Action delete = new Action("Delete") {
		@Override
		public void run() {
			if (MessageDialog.openConfirm(parent.getShell(), "Confirmation", "Delete object?")) {
				if (currentlySelectedObject instanceof TradingAgentConfigurationWrapper) {
					TradingAgentConfigurationWrapper wrapper = (TradingAgentConfigurationWrapper) currentlySelectedObject;
					wrapper.getManager().removeConfiguration(wrapper.getHandle());
					tradingAgentTree.setSelection(new StructuredSelection(selectedTradingAgentFactory));
				} else if (currentlySelectedObject instanceof TradingAgentBindingWrapper) {
					TradingAgentBindingWrapper wrapper = (TradingAgentBindingWrapper) currentlySelectedObject;
					wrapper.getManager().removeBinding(wrapper.getHandle());
					tradingAgentTree.setSelection(new StructuredSelection(selectedTradingAgentConfiguration));
				} else if (currentlySelectedObject instanceof TradingAgentExecutionWrapper) {
					TradingAgentExecutionWrapper wrapper = (TradingAgentExecutionWrapper) currentlySelectedObject;
					wrapper.getManager().removeExecution(wrapper.getHandle());
					tradingAgentTree.setSelection(new StructuredSelection(selectedTradingAgentBinding));
				} else {
					return;
				}
				refreshTree();
			}
		}
	};

	private final IMonitorableContainerListener<IStockDatabaseTradingManager> tradingAgentManagerContainerListener = new IMonitorableContainerListener<IStockDatabaseTradingManager>() {
		@Override
		public void onElementAdded(IStockDatabaseTradingManager manager) {
			refreshTree();
			adapterFactory.resetCache();
		}

		@Override
		public void onElementRemoved(IStockDatabaseTradingManager manager) {
			refreshTree();
		}
		
		@Override
		public void onElementModified(IStockDatabaseTradingManager element) {}
	};

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		tradingAgentTree = new TreeViewer(parent);
		
		tradingAgentTree.setContentProvider(new BaseWorkbenchContentProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				super.inputChanged(viewer, oldInput, newInput);
				if (oldInput != null) {
					((IMonitorableContainer<IStockDatabaseTradingManager>) oldInput).removeListener(tradingAgentManagerContainerListener);
				}
				if (newInput != null) {
					IMonitorableContainer<IStockDatabaseTradingManager> container = (IMonitorableContainer<IStockDatabaseTradingManager>) newInput;
					container.addListener(tradingAgentManagerContainerListener);
					for (IStockDatabaseTradingManager manager : container.getElements()) {
						tradingAgentManagerContainerListener.onElementAdded(manager);
					}
				}
			}
		});

		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		tradingAgentTree.setLabelProvider(new DecoratingLabelProvider(new WorkbenchLabelProvider(), decorator));
		
		tradingAgentManagerContainer = TradingAgentPlugin.getDefault().getTradingAgentManagerContainer();
		adapterFactory = new TradingManagersViewAdapterFactory(tradingAgentManagerContainer);
		Platform.getAdapterManager().registerAdapters(adapterFactory, IStockDatabaseTradingManagerContainer.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory, IStockDatabaseTradingManager.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory, TradingAgentFactoryWrapper.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory, TradingAgentConfigurationWrapper.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory, TradingAgentBindingWrapper.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory, TradingAgentExecutionWrapper.class);
		
		tradingAgentTree.setInput(tradingAgentManagerContainer);
		
		tradingAgentTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					currentlySelectedObject = structuredSelection.getFirstElement();
					if (currentlySelectedObject instanceof IStockDatabaseTradingManager) {
						selectedTradingAgentManager = (IStockDatabaseTradingManager) currentlySelectedObject;
						delete.setEnabled(false);
					} else if (currentlySelectedObject instanceof TradingAgentFactoryWrapper) {
						TradingAgentFactoryWrapper wrapper = (TradingAgentFactoryWrapper) currentlySelectedObject;
						selectedTradingAgentFactory = wrapper.getHandle();
						selectedTradingAgentManager = wrapper.getManager();
						delete.setEnabled(false);
					} else if (currentlySelectedObject instanceof TradingAgentConfigurationWrapper) {
						TradingAgentConfigurationWrapper wrapper = (TradingAgentConfigurationWrapper) currentlySelectedObject;
						selectedTradingAgentConfiguration = wrapper.getHandle();
						selectedTradingAgentFactory = wrapper.getManager().getParent(wrapper.getHandle());
						selectedTradingAgentManager = wrapper.getManager();
						delete.setEnabled(true);
					} else if (currentlySelectedObject instanceof TradingAgentBindingWrapper) {
						TradingAgentBindingWrapper wrapper = (TradingAgentBindingWrapper) currentlySelectedObject;
						selectedTradingAgentBinding = wrapper.getHandle();
						selectedTradingAgentConfiguration = wrapper.getManager().getParent(wrapper.getHandle());
						selectedTradingAgentFactory = wrapper.getManager().getParent(selectedTradingAgentConfiguration);
						selectedTradingAgentManager = wrapper.getManager();
						delete.setEnabled(true);
						executeLiveTradingAgentBinding.setEnabled(selectedTradingAgentManager.isExecutionTypeAvailable(ExecutionType.LIVE));
						executeSimulatedTradingAgentBinding.setEnabled(selectedTradingAgentManager.isExecutionTypeAvailable(ExecutionType.BACKTEST));
					} else if (currentlySelectedObject instanceof TradingAgentExecutionWrapper) {
						TradingAgentExecutionWrapper wrapper = (TradingAgentExecutionWrapper) currentlySelectedObject;
						selectedTradingAgentExecution = wrapper.getHandle();
						selectedTradingAgentBinding = wrapper.getManager().getParent(wrapper.getHandle());
						selectedTradingAgentConfiguration = wrapper.getManager().getParent(selectedTradingAgentBinding);
						selectedTradingAgentFactory = wrapper.getManager().getParent(selectedTradingAgentConfiguration);
						selectedTradingAgentManager = wrapper.getManager();
						if (selectedTradingAgentManager.getRunningStatus(selectedTradingAgentExecution) == RunningStatus.PAUSED) {
							pauseExecution.setEnabled(false);
							resumeExecution.setEnabled(true);
							killExecution.setEnabled(true);
						} else if (selectedTradingAgentManager.getRunningStatus(selectedTradingAgentExecution) == RunningStatus.RUNNING) {
							pauseExecution.setEnabled(true);
							resumeExecution.setEnabled(false);
							killExecution.setEnabled(true);
						} else {
							pauseExecution.setEnabled(false);
							resumeExecution.setEnabled(false);
							killExecution.setEnabled(false);
						}
						delete.setEnabled(true);
					}
				}
			}});
		
		getSite().setSelectionProvider(tradingAgentTree);
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
		Menu menu = menuMgr.createContextMenu(tradingAgentTree.getControl());
		tradingAgentTree.getControl().setMenu(menu);

		// Register menu for extension.
		getSite().registerContextMenu(menuMgr, tradingAgentTree);
	}

	private void fillContextMenu(IMenuManager menuMgr) {
		if (currentlySelectedObject instanceof TradingAgentFactoryWrapper) {
			menuMgr.add(createTradingAgentConfiguration);
		}
		if (currentlySelectedObject instanceof TradingAgentConfigurationWrapper) {
			menuMgr.add(createTradingAgentBinding);
			menuMgr.add(new Separator());
			menuMgr.add(viewTradingAgentConfiguration);
		}
		if (currentlySelectedObject instanceof TradingAgentBindingWrapper) {
			menuMgr.add(executeSimulatedTradingAgentBinding);
			menuMgr.add(executeLiveTradingAgentBinding);
			menuMgr.add(new Separator());
			menuMgr.add(viewTradingAgentBinding);
		}
		if (currentlySelectedObject instanceof TradingAgentExecutionWrapper) {
			menuMgr.add(pauseExecution);
			menuMgr.add(resumeExecution);
			menuMgr.add(killExecution);
		}
		menuMgr.add(new Separator());
		menuMgr.add(delete);
		menuMgr.add(new Separator());
		menuMgr.add(refreshTree);
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void hookGlobalActions() {
		IActionBars bars = getViewSite().getActionBars();
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), delete);
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private void refreshTree() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				tradingAgentTree.refresh();
			}
		});
	}

}
