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

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.quantcomponents.ui.marketdata.MarketDataManagersView;

public class DefaultPerspectiveFactory implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		IFolderLayout topLeftFolder = layout.createFolder("topLeft", IPageLayout.LEFT, 0.35f, IPageLayout.ID_EDITOR_AREA);
		IFolderLayout bottomLeftFolder = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.30f, "topLeft");
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.30f, IPageLayout.ID_EDITOR_AREA);
		bottom.addView(EquityCurveView.VIEW_ID);
		
		IFolderLayout topRightFolder = layout.createFolder("topRight", IPageLayout.RIGHT, 0.50f, IPageLayout.ID_EDITOR_AREA);
		
		topLeftFolder.addView(MarketDataManagersView.VIEW_ID);
		
		bottomLeftFolder.addView(TradingManagersView.VIEW_ID);
		
		topRightFolder.addView(PositionView.VIEW_ID);
		topRightFolder.addView(TradingStatsView.VIEW_ID);
	}

}
