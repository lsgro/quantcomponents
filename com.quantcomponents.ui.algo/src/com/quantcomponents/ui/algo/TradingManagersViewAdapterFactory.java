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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.quantcomponents.algo.IStockDatabaseTradingManager;
import com.quantcomponents.algo.TradingAgentBindingHandle;
import com.quantcomponents.algo.TradingAgentConfigurationHandle;
import com.quantcomponents.algo.TradingAgentExecutionHandle;
import com.quantcomponents.algo.TradingAgentFactoryHandle;
import com.quantcomponents.ui.core.IMonitorableContainer;

public class TradingManagersViewAdapterFactory implements IAdapterFactory {
	private final IMonitorableContainer<IStockDatabaseTradingManager> rootContainer;
	private Map<TradingAgentFactoryHandle, TradingAgentFactoryWrapper> agentFactoryWrapperMap = new HashMap<TradingAgentFactoryHandle, TradingAgentFactoryWrapper>();
	private Map<TradingAgentConfigurationHandle, TradingAgentConfigurationWrapper> agentWrapperMap = new HashMap<TradingAgentConfigurationHandle, TradingAgentConfigurationWrapper>();
	private Map<TradingAgentBindingHandle, TradingAgentBindingWrapper> agentBindingWrapperMap = new HashMap<TradingAgentBindingHandle, TradingAgentBindingWrapper>();
	private Map<TradingAgentExecutionHandle, TradingAgentExecutionWrapper> agentExecutionWrapperMap = new HashMap<TradingAgentExecutionHandle, TradingAgentExecutionWrapper>();
	
	public TradingAgentFactoryWrapper getOrCreateFactoryWrapper(TradingAgentFactoryHandle factory, IStockDatabaseTradingManager manager) {
		TradingAgentFactoryWrapper wrapper = agentFactoryWrapperMap.get(factory);
		if (wrapper == null) {
			wrapper = new TradingAgentFactoryWrapper(factory, manager);
			agentFactoryWrapperMap.put(factory, wrapper);
		}
		return wrapper;
	}
	
	public TradingAgentConfigurationWrapper getOrCreateAgentWrapper(TradingAgentConfigurationHandle agent, IStockDatabaseTradingManager manager) {
		TradingAgentConfigurationWrapper wrapper = agentWrapperMap.get(agent);
		if (wrapper == null) {
			wrapper = new TradingAgentConfigurationWrapper(agent, manager);
			agentWrapperMap.put(agent, wrapper);
		}
		return wrapper;
	}
	
	public TradingAgentBindingWrapper getOrCreateAgentBindingWrapper(TradingAgentBindingHandle binding, IStockDatabaseTradingManager manager) {
		TradingAgentBindingWrapper wrapper = agentBindingWrapperMap.get(binding);
		if (wrapper == null) {
			wrapper = new TradingAgentBindingWrapper(binding, manager);
			agentBindingWrapperMap.put(binding, wrapper);
		}
		return wrapper;
	}
	
	public TradingAgentExecutionWrapper getOrCreateAgentExecutionWrapper(TradingAgentExecutionHandle execution, IStockDatabaseTradingManager manager) {
		TradingAgentExecutionWrapper wrapper = agentExecutionWrapperMap.get(execution);
		if (wrapper == null) {
			wrapper = new TradingAgentExecutionWrapper(execution, manager);
			agentExecutionWrapperMap.put(execution, wrapper);
		}
		return wrapper;
	}
	
	public void resetCache() {
		agentFactoryWrapperMap.clear(); 
		agentWrapperMap.clear(); 
		agentBindingWrapperMap.clear();
		agentExecutionWrapperMap.clear();
	}
	
	private IWorkbenchAdapter managerContainerAdapter = new IWorkbenchAdapter() {
		@Override
		public Object[] getChildren(Object o) {
			return ((IStockDatabaseTradingManagerContainer) o).getElements().toArray();
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		@Override
		public String getLabel(Object o) {
			return null;
		}

		@Override
		public Object getParent(Object o) {
			return null;
		}
	};
		
	private IWorkbenchAdapter managerAdapterAdapter = new IWorkbenchAdapter() {
		@Override
		public Object[] getChildren(Object o) {
			IStockDatabaseTradingManager manager = (IStockDatabaseTradingManager) o;
			Collection<TradingAgentFactoryHandle> factories = manager.getAllTradingAgentFactories();
			Object[] children = new Object[factories.size()];
			Map<TradingAgentFactoryHandle, TradingAgentFactoryWrapper> updatedWrapperMap = new HashMap<TradingAgentFactoryHandle, TradingAgentFactoryWrapper>();
			int i = 0;
			for (TradingAgentFactoryHandle factory : factories) {
				TradingAgentFactoryWrapper wrapper = getOrCreateFactoryWrapper(factory, manager);
				updatedWrapperMap.put(factory, wrapper);
				children[i++] = wrapper;
			}
			agentFactoryWrapperMap = updatedWrapperMap;
			return children;
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return TradingAgentPlugin.getDefault().getImageRegistry().getDescriptor(TradingAgentPlugin.TRADING_AGENT_MANAGER_IMAGE_KEY);
		}

		@Override
		public String getLabel(Object o) {
			return ((IStockDatabaseTradingManager) o).getPrettyName();
		}

		@Override
		public Object getParent(Object o) {
			return rootContainer;
		}
		
	};
	
	private IWorkbenchAdapter tradingAgentFactoryAdapter = new IWorkbenchAdapter() {
		@Override
		public Object[] getChildren(Object o) {
			TradingAgentFactoryWrapper wrapper = (TradingAgentFactoryWrapper) o;
			IStockDatabaseTradingManager manager = wrapper.getManager();
			Collection<TradingAgentConfigurationHandle> agents = manager.getChildren(wrapper.getHandle());
			Object[] children = new Object[agents.size()];
			Map<TradingAgentConfigurationHandle, TradingAgentConfigurationWrapper> updatedWrapperMap = new HashMap<TradingAgentConfigurationHandle, TradingAgentConfigurationWrapper>();
			int i = 0;
			for (TradingAgentConfigurationHandle agent : agents) {
				TradingAgentConfigurationWrapper childWrapper = getOrCreateAgentWrapper(agent, manager);
				updatedWrapperMap.put(agent, childWrapper);
				children[i++] = childWrapper;
			}
			agentWrapperMap = updatedWrapperMap;
			return children;
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return TradingAgentPlugin.getDefault().getImageRegistry().getDescriptor(TradingAgentPlugin.TRADING_AGENT_FACTORY_IMAGE_KEY);
		}

		@Override
		public String getLabel(Object o) {
			TradingAgentFactoryWrapper wrapper = (TradingAgentFactoryWrapper) o;
			return wrapper.getHandle().getPrettyName();
		}

		@Override
		public Object getParent(Object o) {
			TradingAgentFactoryWrapper wrapper = (TradingAgentFactoryWrapper) o;
			return wrapper.getManager();
		}
		
	};
	
	private IWorkbenchAdapter tradingAgentConfigurationAdapter = new IWorkbenchAdapter() {
		@Override
		public Object[] getChildren(Object o) {
			TradingAgentConfigurationWrapper wrapper = (TradingAgentConfigurationWrapper) o;
			IStockDatabaseTradingManager manager = wrapper.getManager();
			Collection<TradingAgentBindingHandle> bindings = manager.getChildren(wrapper.getHandle());
			Object[] children = new Object[bindings.size()];
			Map<TradingAgentBindingHandle, TradingAgentBindingWrapper> updatedWrapperMap = new HashMap<TradingAgentBindingHandle, TradingAgentBindingWrapper>();
			int i = 0;
			for (TradingAgentBindingHandle binding : bindings) {
				TradingAgentBindingWrapper childWrapper = getOrCreateAgentBindingWrapper(binding, manager);
				updatedWrapperMap.put(binding, childWrapper);
				children[i++] = childWrapper;
			}
			agentBindingWrapperMap = updatedWrapperMap;
			return children;
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return TradingAgentPlugin.getDefault().getImageRegistry().getDescriptor(TradingAgentPlugin.TRADING_AGENT_IMAGE_KEY);
		}

		@Override
		public String getLabel(Object o) {
			TradingAgentConfigurationWrapper wrapper = (TradingAgentConfigurationWrapper) o;
			return wrapper.getHandle().getPrettyName();
		}

		@Override
		public Object getParent(Object o) {
			TradingAgentConfigurationWrapper wrapper = (TradingAgentConfigurationWrapper) o;
			TradingAgentFactoryHandle parent = wrapper.getManager().getParent(wrapper.getHandle());
			return parent == null ? null : new TradingAgentFactoryWrapper(parent, wrapper.getManager());
		}
		
	};
	
	private IWorkbenchAdapter tradingAgentBindingAdapter = new IWorkbenchAdapter() {
		@Override
		public Object[] getChildren(Object o) {
			TradingAgentBindingWrapper wrapper = (TradingAgentBindingWrapper) o;
			IStockDatabaseTradingManager manager = wrapper.getManager();
			Collection<TradingAgentExecutionHandle> executions = manager.getChildren(wrapper.getHandle());
			Object[] children = new Object[executions.size()];
			Map<TradingAgentExecutionHandle, TradingAgentExecutionWrapper> updatedWrapperMap = new HashMap<TradingAgentExecutionHandle, TradingAgentExecutionWrapper>();
			int i = 0;
			for (TradingAgentExecutionHandle execution : executions) {
				TradingAgentExecutionWrapper childWrapper = getOrCreateAgentExecutionWrapper(execution, manager);
				updatedWrapperMap.put(execution, childWrapper);
				children[i++] = childWrapper;
			}
			agentExecutionWrapperMap = updatedWrapperMap;
			return children;
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return TradingAgentPlugin.getDefault().getImageRegistry().getDescriptor(TradingAgentPlugin.TRADING_AGENT_BINDING_IMAGE_KEY);
		}

		@Override
		public String getLabel(Object o) {
			TradingAgentBindingWrapper wrapper = (TradingAgentBindingWrapper) o;
			return wrapper.getHandle().getPrettyName();
		}

		@Override
		public Object getParent(Object o) {
			TradingAgentBindingWrapper wrapper = (TradingAgentBindingWrapper) o;
			TradingAgentConfigurationHandle parent = wrapper.getManager().getParent(wrapper.getHandle());
			return parent == null ? null : new TradingAgentConfigurationWrapper(parent, wrapper.getManager());
		}
		
	};
	
	private IWorkbenchAdapter tradingAgentExecutionAdapter = new IWorkbenchAdapter() {
		@Override
		public Object[] getChildren(Object o) {
			return new Object[0];
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return TradingAgentPlugin.getDefault().getImageRegistry().getDescriptor(TradingAgentPlugin.TRADING_AGENT_EXECUTION_IMAGE_KEY);
		}

		@Override
		public String getLabel(Object o) {
			TradingAgentExecutionWrapper wrapper = (TradingAgentExecutionWrapper) o;
			return wrapper.getHandle().getPrettyName();
		}

		@Override
		public Object getParent(Object o) {
			TradingAgentExecutionWrapper wrapper = (TradingAgentExecutionWrapper) o;
			TradingAgentBindingHandle parent = wrapper.getManager().getParent(wrapper.getHandle());
			return parent == null ? null : new TradingAgentBindingWrapper(parent, wrapper.getManager());
		}
		
	};
		
	public TradingManagersViewAdapterFactory(IMonitorableContainer<IStockDatabaseTradingManager> container) {
		this.rootContainer = container;
	}

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class) {
			if (adaptableObject instanceof IStockDatabaseTradingManagerContainer) {
				return managerContainerAdapter;
			} else if (adaptableObject instanceof IStockDatabaseTradingManager) {
				return managerAdapterAdapter;
			} else if (adaptableObject instanceof TradingAgentFactoryWrapper) {
				return tradingAgentFactoryAdapter;
			} else if (adaptableObject instanceof TradingAgentConfigurationWrapper) {
				return tradingAgentConfigurationAdapter;
			} else if (adaptableObject instanceof TradingAgentBindingWrapper) {
				return tradingAgentBindingAdapter;
			} else if (adaptableObject instanceof TradingAgentExecutionWrapper) {
				return tradingAgentExecutionAdapter;
			}
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

}
