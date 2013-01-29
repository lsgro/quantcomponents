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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;


public class AutoUpdateIconDecorator extends LabelProvider implements ILightweightLabelDecorator {
	public static final String DECORATOR_ID = "com.quantcomponents.ui.marketdata.autoUpdateDecorator";
	
	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof StockDatabasePresentationWrapper) {
			StockDatabasePresentationWrapper stockDatabase = (StockDatabasePresentationWrapper) element;
			if (stockDatabase.isRealtimeUpdate()) {
				ImageDescriptor imageDescriptor = MarketDataPlugin.getDefault().getImageRegistry().getDescriptor(MarketDataPlugin.DECORATOR_AUTOUPDATE_IMAGE_KEY);
				decoration.addOverlay(imageDescriptor, IDecoration.BOTTOM_RIGHT);
			}
		}
	}

	public void fireLabelProviderChanged(StockDatabasePresentationWrapper stockDatabase) {
		LabelProviderChangedEvent event = new LabelProviderChangedEvent(this, new Object[] { stockDatabase });
		fireLabelProviderChanged(event);
	}
}
