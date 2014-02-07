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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.quantcomponents.marketdata.IMarketDataManager;
import com.quantcomponents.marketdata.IRealTimeMarketDataManager;
import com.quantcomponents.ui.core.IMonitorableContainerListener;
import com.quantcomponents.ui.core.IMutableMonitorableContainer;

public class MarketDataManagerContainerWrappingDecorator implements IMutableMonitorableContainer<IMarketDataManager, MarketDataManagerPresentationWrapper> {
	private final Map<IMarketDataManager, MarketDataManagerPresentationWrapper> wrapperByManager = new ConcurrentHashMap<IMarketDataManager, MarketDataManagerPresentationWrapper>(); 
	private final IMutableMonitorableContainer<MarketDataManagerPresentationWrapper, MarketDataManagerPresentationWrapper> container;
	
	public MarketDataManagerContainerWrappingDecorator(IMutableMonitorableContainer<MarketDataManagerPresentationWrapper, MarketDataManagerPresentationWrapper> container) {
		this.container = container;
	}

	@Override
	public Collection<MarketDataManagerPresentationWrapper> getElements() {
		return container.getElements();
	}
	
	@Override
	public void addElement(IMarketDataManager manager) {
		MarketDataManagerPresentationWrapper wrapper;
		if (manager instanceof IRealTimeMarketDataManager) {
			wrapper = new RealTimeMarketDataManagerPresentationWrapper((IRealTimeMarketDataManager) manager, this);			
		} else {
			wrapper = new MarketDataManagerPresentationWrapper(manager, this);
		}
		wrapperByManager.put(manager, wrapper);
		container.addElement(wrapper);
	}

	@Override
	public boolean removeElement(IMarketDataManager element) {
		MarketDataManagerPresentationWrapper wrapper = wrapperByManager.get(element);
		if (wrapper != null) {
			return container.removeElement(wrapper);
		}
		return false;
	}

	@Override
	public boolean removeWrapper(MarketDataManagerPresentationWrapper element) {
		return container.removeElement(element);
	}

	@Override
	public void addListener(IMonitorableContainerListener<MarketDataManagerPresentationWrapper> listener) {
		container.addListener(listener);
	}

	@Override
	public void removeListener(IMonitorableContainerListener<MarketDataManagerPresentationWrapper> listener) {
		container.removeListener(listener);
	}

	@Override
	public void dispose() {
		container.dispose();
		wrapperByManager.clear();
	}
}
