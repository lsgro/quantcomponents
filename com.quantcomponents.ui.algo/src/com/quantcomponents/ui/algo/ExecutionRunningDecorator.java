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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;

import com.quantcomponents.algo.IManagedRunnable.RunningStatus;

public class ExecutionRunningDecorator extends LabelProvider implements ILightweightLabelDecorator {
	public static final String DECORATOR_ID = "com.quantcomponents.ui.algo.executionRunningDecorator";

	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof TradingAgentExecutionWrapper) {
			TradingAgentExecutionWrapper wrapper = (TradingAgentExecutionWrapper) element;
			if (wrapper.getManager().getRunningStatus(wrapper.getHandle()) == RunningStatus.PAUSED) {
				ImageDescriptor imageDescriptor = TradingAgentPlugin.getDefault().getImageRegistry().getDescriptor(TradingAgentPlugin.DECORATOR_PAUSED_IMAGE_KEY);
				decoration.addOverlay(imageDescriptor, IDecoration.BOTTOM_RIGHT);
			} else if (wrapper.getManager().getRunningStatus(wrapper.getHandle()) == RunningStatus.RUNNING) {
				ImageDescriptor imageDescriptor = TradingAgentPlugin.getDefault().getImageRegistry().getDescriptor(TradingAgentPlugin.DECORATOR_RUNNING_IMAGE_KEY);
				decoration.addOverlay(imageDescriptor, IDecoration.BOTTOM_RIGHT);
			}
		}
	}

	public void fireLabelProviderChanged(TradingAgentExecutionWrapper execution) {
		LabelProviderChangedEvent event = new LabelProviderChangedEvent(this, new Object[] { execution });
		fireLabelProviderChanged(event);
	}
}
