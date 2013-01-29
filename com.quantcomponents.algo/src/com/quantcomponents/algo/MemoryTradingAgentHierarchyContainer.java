/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.algo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Non-persistent hierarchy container
 */
public class MemoryTradingAgentHierarchyContainer implements ITradingAgentHierarchyContainer {
	private static final Logger logger = Logger.getLogger(MemoryTradingAgentHierarchyContainer.class.getName());
	protected static class HierarchyItem implements Serializable {
		private static final long serialVersionUID = 1106458602956414169L;
		public HierarchyItem(IHierarchyItemHandle parent) {
			this.parent = parent;
		}
		public IHierarchyItemHandle parent;
		public Collection<IHierarchyItemHandle> children = new CopyOnWriteArrayList<IHierarchyItemHandle>();
	}
	protected final Map<IHierarchyItemHandle, HierarchyItem> hierarchy = new ConcurrentHashMap<IHierarchyItemHandle, HierarchyItem>();
	protected final Map<TradingAgentFactoryHandle, ITradingAgentFactory> factoriesByHandle = new ConcurrentHashMap<TradingAgentFactoryHandle, ITradingAgentFactory>();
	protected final Map<ITradingAgentFactory, TradingAgentFactoryHandle> handlesByFactory = new ConcurrentHashMap<ITradingAgentFactory, TradingAgentFactoryHandle>();
	protected final Map<TradingAgentConfigurationHandle, TradingAgentConfiguration> configurationByHandle = new ConcurrentHashMap<TradingAgentConfigurationHandle, TradingAgentConfiguration>();
	protected final Map<TradingAgentBindingHandle, TradingAgentBinding> bindingByHandle = new ConcurrentHashMap<TradingAgentBindingHandle, TradingAgentBinding>();
	protected final Map<TradingAgentExecutionHandle, ITradingAgentExecution> executionByHandle = new ConcurrentHashMap<TradingAgentExecutionHandle, ITradingAgentExecution>();

	public synchronized void addTradingAgentFactory(ITradingAgentFactory factory) {
		logger.log(Level.INFO, "Found: " + factory.getClass().getName());
		if (!handlesByFactory.containsKey(factory)) {
			TradingAgentFactoryHandle handle = new TradingAgentFactoryHandle(factory.getClass().getName(), factory.getConfigurationKeys());
			hierarchy.put(handle, new HierarchyItem(null));
			handlesByFactory.put(factory, handle);
			factoriesByHandle.put(handle, factory);
		}
	}
	
	public synchronized void removeTradingAgentFactory(ITradingAgentFactory factory) {
		logger.log(Level.INFO, "Removed: " + factory.getClass().getName());
		TradingAgentFactoryHandle handle = handlesByFactory.remove(factory);
		if (handle != null) {
			HierarchyItem item = hierarchy.remove(handle);
			for (IHierarchyItemHandle child : item.children) {
				removeTradingAgentConfiguration((TradingAgentConfigurationHandle) child);
			}
			factoriesByHandle.remove(handle);
		}
	}
	
	@Override
	public synchronized Collection<TradingAgentFactoryHandle> getAllTradingAgentFactories() {
		return new ArrayList<TradingAgentFactoryHandle>(handlesByFactory.values());
	}

	@Override
	public synchronized ITradingAgentFactory getTradingAgentFactory(TradingAgentFactoryHandle handle) {
		return factoriesByHandle.get(handle);
	}

	@Override
	public synchronized void putTradingAgentConfiguration(TradingAgentConfigurationHandle handle, TradingAgentConfiguration tradingAgentConfiguration, TradingAgentFactoryHandle parentHandle) {
		HierarchyItem parentItem = hierarchy.get(parentHandle);
		parentItem.children.add(handle);
		hierarchy.put(handle, new HierarchyItem(parentHandle));
		configurationByHandle.put(handle, tradingAgentConfiguration);
	}

	@Override
	public synchronized TradingAgentConfiguration getTradingAgentConfiguration(TradingAgentConfigurationHandle handle) {
		return configurationByHandle.get(handle);
	}

	@Override
	public synchronized TradingAgentConfiguration removeTradingAgentConfiguration(TradingAgentConfigurationHandle handle) {
		TradingAgentConfiguration configuration = configurationByHandle.remove(handle);
		if (configuration != null) {
			HierarchyItem item = hierarchy.remove(handle);
			for (IHierarchyItemHandle child : item.children) {
				removeTradingAgentBinding((TradingAgentBindingHandle) child);
			}
			HierarchyItem parentItem = hierarchy.get(item.parent);
			if (parentItem != null) {
				parentItem.children.remove(handle);
			}
		}
		return configuration;
	}

	@Override
	public synchronized void putTradingAgentBinding(TradingAgentBindingHandle handle, TradingAgentBinding tradingAgentBinding, TradingAgentConfigurationHandle parentHandle) {
		HierarchyItem parentItem = hierarchy.get(parentHandle);
		parentItem.children.add(handle);
		hierarchy.put(handle, new HierarchyItem(parentHandle));
		bindingByHandle.put(handle, tradingAgentBinding);
	}

	@Override
	public synchronized TradingAgentBinding getTradingAgentBinding(TradingAgentBindingHandle handle) {
		return bindingByHandle.get(handle);
	}

	@Override
	public synchronized TradingAgentBinding removeTradingAgentBinding(TradingAgentBindingHandle handle) {
		TradingAgentBinding binding = bindingByHandle.remove(handle);
		if (binding != null) {
			HierarchyItem item = hierarchy.remove(handle);
			for (IHierarchyItemHandle child : item.children) {
				removeTradingAgentExecution((TradingAgentExecutionHandle) child);
			}
			HierarchyItem parentItem = hierarchy.get(item.parent);
			if (parentItem != null) {
				parentItem.children.remove(handle);
			}
		}
		return binding;
	}

	@Override
	public synchronized void putTradingAgentExecution(TradingAgentExecutionHandle handle, ITradingAgentExecution tradingAgentExecution, TradingAgentBindingHandle parentHandle) {
		HierarchyItem parentItem = hierarchy.get(parentHandle);
		parentItem.children.add(handle);
		hierarchy.put(handle, new HierarchyItem(parentHandle));
		executionByHandle.put(handle, tradingAgentExecution);
	}

	@Override
	public synchronized ITradingAgentExecution getTradingAgentExecution(TradingAgentExecutionHandle handle) {
		return executionByHandle.get(handle);
	}

	@Override
	public synchronized ITradingAgentExecution removeTradingAgentExecution(TradingAgentExecutionHandle handle) {
		ITradingAgentExecution execution = executionByHandle.remove(handle);
		if (execution != null) {
			HierarchyItem item = hierarchy.remove(handle);
			HierarchyItem parentItem = hierarchy.get(item.parent);
			if (parentItem != null) {
				parentItem.children.remove(handle);
			}
		}
		return execution;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Collection<TradingAgentConfigurationHandle> getChildrenHandles(TradingAgentFactoryHandle handle) {
		return new ArrayList<TradingAgentConfigurationHandle>((Collection<TradingAgentConfigurationHandle>) getChildren(handle));
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Collection<TradingAgentBindingHandle> getChildrenHandles(TradingAgentConfigurationHandle handle) {
		return new ArrayList<TradingAgentBindingHandle>((Collection<TradingAgentBindingHandle>) getChildren(handle));
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Collection<TradingAgentExecutionHandle> getChildrenHandles(TradingAgentBindingHandle handle) {
		return new ArrayList<TradingAgentExecutionHandle>((Collection<TradingAgentExecutionHandle>) getChildren(handle));
	}
	
	@Override
	public synchronized TradingAgentFactoryHandle getParentHandle(TradingAgentConfigurationHandle handle) {
		return (TradingAgentFactoryHandle) getParent(handle);
	}

	@Override
	public synchronized TradingAgentConfigurationHandle getParentHandle(TradingAgentBindingHandle handle) {
		return (TradingAgentConfigurationHandle) getParent(handle);
	}

	@Override
	public synchronized TradingAgentBindingHandle getParentHandle(TradingAgentExecutionHandle handle) {
		return (TradingAgentBindingHandle) getParent(handle);
	}
	
	private Collection<?> getChildren(IHierarchyItemHandle handle) {
		HierarchyItem item = hierarchy.get(handle);
		if (item == null) {
			return null;
		}
		return item.children;
	}
	
	private IHierarchyItemHandle getParent(IHierarchyItemHandle handle) {
		HierarchyItem item = hierarchy.get(handle);
		if (item == null) {
			return null;
		}
		return item.parent;
	}
}
