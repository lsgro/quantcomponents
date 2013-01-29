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

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class MarketDataViewAdapterFactory implements IAdapterFactory {
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};

	private IWorkbenchAdapter managerContainerAdapter = new IWorkbenchAdapter() {

		@Override
		public Object[] getChildren(Object o) {
			return ((IMarketDataManagerContainer) o).getElements().toArray();
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
		}};
		
	private IWorkbenchAdapter marketDataManagerAdapter = new IWorkbenchAdapter() {

		@Override
		public Object[] getChildren(Object o) {
			MarketDataManagerPresentationWrapper wrapper = (MarketDataManagerPresentationWrapper) o;
			wrapper.synchronizeStockDatabases();
			return wrapper.allStockDatabases().toArray();
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return MarketDataPlugin.getDefault().getImageRegistry().getDescriptor(MarketDataPlugin.MARKET_DATA_MANAGER_IMAGE_KEY);
		}

		@Override
		public String getLabel(Object o) {
			return ((MarketDataManagerPresentationWrapper) o).getPrettyName();
		}

		@Override
		public Object getParent(Object o) {
			return ((MarketDataManagerPresentationWrapper) o).getParent();
		}
		
	};
	
	private IWorkbenchAdapter stockDatabaseAdapter = new IWorkbenchAdapter() {

		@Override
		public Object[] getChildren(Object o) {
			return EMPTY_OBJECT_ARRAY;
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return MarketDataPlugin.getDefault().getImageRegistry().getDescriptor(MarketDataPlugin.STOCK_DATABASE_IMAGE_KEY);
		}

		@Override
		public String getLabel(Object o) {
			return ((StockDatabasePresentationWrapper) o).getPrettyName();
		}

		@Override
		public Object getParent(Object o) {
			return ((StockDatabasePresentationWrapper) o).getParent();
		}
		
	};

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class) {
			if (adaptableObject instanceof IMarketDataManagerContainer) {
				return managerContainerAdapter;
			} else if (adaptableObject instanceof MarketDataManagerPresentationWrapper) {
				return marketDataManagerAdapter;
			} else if (adaptableObject instanceof StockDatabasePresentationWrapper) {
				return stockDatabaseAdapter;
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

}
