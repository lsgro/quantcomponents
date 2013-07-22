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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.quantcomponents.marketdata.IStockDatabase;
import com.quantcomponents.ui.core.IMonitorableContainer;
import com.quantcomponents.ui.marketdata.MarketDataManagerPresentationWrapper;
import com.quantcomponents.ui.marketdata.MarketDataPlugin;
import com.quantcomponents.ui.marketdata.StockDatabasePresentationWrapper;

public class TradingAgentBindingDialog extends Dialog {
	private final IMonitorableContainer<MarketDataManagerPresentationWrapper> marketDataManagerContainer;
	private final Map<String, IStockDatabase> inputStockDatabasesByName;
	private final boolean modifyAllowed;
	private final Map<String, StockDatabasePresentationWrapper> stockDatabasesByName = new HashMap<String, StockDatabasePresentationWrapper>();
	private final Map<IStockDatabase, String> nameByStockDatabases = new HashMap<IStockDatabase, String>();
	private final List<String> stockDatabaseNameList = new LinkedList<String>();
	private String name;
	private Text nameEdit;
	private Combo[] inputValueEditors;
	private String[] stockDatabaseNames;

	public TradingAgentBindingDialog(Map<String, IStockDatabase> inputStockDatabases, boolean modifyAllowed, String name, Shell parentShell) {
		super(parentShell);
		this.inputStockDatabasesByName = inputStockDatabases;
		this.modifyAllowed = modifyAllowed;
		this.name = name;
		this.marketDataManagerContainer = MarketDataPlugin.getDefault().getMarketDataManagerContainer();
		for (MarketDataManagerPresentationWrapper dataManager : marketDataManagerContainer.getElements()) {
			for (StockDatabasePresentationWrapper stockDatabaseWrapper : dataManager.getElements()) {
				String stockDbName = stockDatabaseWrapper.getPrettyName();
				stockDatabasesByName.put(stockDbName, stockDatabaseWrapper);
				nameByStockDatabases.put(stockDatabaseWrapper.getInner(), stockDbName);
				stockDatabaseNameList.add(stockDbName);
			}
		}
		stockDatabaseNames = stockDatabaseNameList.toArray(new String[stockDatabaseNameList.size()]);
	}
	
	public String getName() {
		return name;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout rootLayout = new GridLayout();
		container.setLayout(rootLayout);
		rootLayout.horizontalSpacing = 20;
		rootLayout.verticalSpacing = 20;
		rootLayout.numColumns = 2;
		Label bindingNameLabel = new Label(container, modifyAllowed ? SWT.NULL : SWT.READ_ONLY);
		bindingNameLabel.setText("Binding name" + (modifyAllowed ? "[optional]" : ""));
		nameEdit = new Text(container, modifyAllowed ? SWT.NULL : SWT.READ_ONLY);
		GridData nameEditLayoutData = new GridData();
		nameEditLayoutData.widthHint = 300;
		nameEdit.setLayoutData(nameEditLayoutData);
		if (name != null) {
			nameEdit.setText(name);
		} 
		Set<String> inputNames = inputStockDatabasesByName.keySet();
		inputValueEditors = new Combo[inputNames.size()];
		int row = 0;
		for (String inputName : inputNames) {
			Label label = new Label(container, SWT.NULL);
			label.setText(inputName);
			if (modifyAllowed) {
				inputValueEditors[row] = new Combo(container, SWT.NULL); 
				inputValueEditors[row].setItems(stockDatabaseNames);
				GridData gridData = new GridData(GridData.FILL_HORIZONTAL); 
				gridData.widthHint = 400;
				inputValueEditors[row].setLayoutData(gridData);
				row++;
			} else {
				IStockDatabase selectedStockDb = inputStockDatabasesByName.get(inputName);
				String selectedStockDbName = nameByStockDatabases.get(selectedStockDb);
				if (selectedStockDbName != null) {
					Text text = new Text(container, SWT.READ_ONLY);
					text.setText(selectedStockDbName);
				}	
			}
		}
		
		return container;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
	}
	
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}
	
	@Override
	protected void okPressed() {
		if (modifyAllowed) {
			applyChanges();
		}
		setReturnCode(OK);
		close();
	}
	
	private void applyChanges() {
		int row = 0;
		for (String inputName : inputStockDatabasesByName.keySet()) {
			String stockDatabaseName = inputValueEditors[row].getText();
			StockDatabasePresentationWrapper stockDatabaseWrapper = stockDatabasesByName.get(stockDatabaseName);
			if (stockDatabaseWrapper == null) {
				MessageDialog.openError(getParentShell(), "Stock Database not found", "Input: '" + inputName + "' not bound");
				return;
			}
			inputStockDatabasesByName.put(inputName, stockDatabaseWrapper.getInner());
			row++;
		}
		name = nameEdit.getText();
	}

}
