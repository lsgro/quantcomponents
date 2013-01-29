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

import java.util.Currency;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.quantcomponents.core.model.BareDate;
import com.quantcomponents.core.model.IdentifierType;
import com.quantcomponents.core.model.OptionRight;
import com.quantcomponents.core.model.SecurityType;
import com.quantcomponents.core.model.beans.ContractBean;

public class NewHistoricalDataPage1 extends WizardPage {
	private ContractBean contractCriteria;
	
	// Contract specification container
	private TabFolder contractSpecFolder;
	private TabItem tab1;
	private TabItem tab2;
	
	// Contract specification 1 (description)
	private Text tickerEdit;
	private Combo typeEdit;
	private DateCombo expiryEdit;
	private Combo currencyEdit;
	private Text exchangeEdit;
	private Combo rightEdit;
	private Text multiplierEdit;
	private Text strikeEdit;
	
	// Contract specification 2 (ID)
	private Combo idTypeEdit;
	private Text idEdit;
	
	public NewHistoricalDataPage1(ContractBean contractCriteria) {
		super("New historical data");
		this.contractCriteria = contractCriteria;
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		// root container
		Composite rootContainer = new Composite(parent, SWT.NULL);
		GridLayout rootLayout = new GridLayout();
		rootLayout.verticalSpacing = 15;
		rootLayout.numColumns = 1;
		rootContainer.setLayout(rootLayout);
		
		// top container: folder
		Composite topContainer = new Composite(rootContainer, SWT.NULL);
		GridData topContainerLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		topContainer.setLayoutData(topContainerLayoutData);
		GridLayout topContainerLayout = new GridLayout();
		topContainerLayout.numColumns = 3;
		topContainer.setLayout(topContainerLayout);
		
		// contract specification container
		contractSpecFolder = new TabFolder(rootContainer, SWT.NULL);
		
		tab1 = new TabItem(contractSpecFolder, SWT.NULL);
		tab1.setText("Description");
		
		tab2 = new TabItem(contractSpecFolder, SWT.NULL);
		tab2.setText("Identifier");
		
		// tab 1: contract description
		Composite tab1Container = new Composite(contractSpecFolder, SWT.NULL);
		tab1Container.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridLayout tab1Layout = new GridLayout();
		tab1Layout.numColumns = 2;
		tab1Container.setLayout(tab1Layout);
		
		tab1.setControl(tab1Container);
		
		Label typeLabel = new Label(tab1Container, SWT.NULL);
		typeLabel.setText("Security Type");
		typeEdit = new Combo(tab1Container, SWT.READ_ONLY);
		String[] securityTypes = new String[SecurityType.values().length];
		for (int i = 0; i < SecurityType.values().length; i++) {
			securityTypes[i] = SecurityType.values()[i].name();
		}
		typeEdit.setItems(securityTypes);
		typeEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}});
		
		Label rightLabel = new Label(tab1Container, SWT.NULL);
		rightLabel.setText("Right");
		rightEdit = new Combo(tab1Container, SWT.READ_ONLY);
		rightEdit.setItems(new String[] {OptionRight.CALL.name(),OptionRight.PUT.name()});
		rightEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}});
		
		Label tickerLabel = new Label(tab1Container, SWT.NULL);
		tickerLabel.setText("Ticker");
		tickerEdit = new Text(tab1Container, SWT.NULL);
		tickerEdit.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}});
		
		Label expiryLabel = new Label(tab1Container, SWT.NULL);
		expiryLabel.setText("Expiry");
		expiryEdit = new DateCombo(tab1Container, SWT.DROP_DOWN);
		expiryEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}});
		
		Label currencyLabel = new Label(tab1Container, SWT.NULL);
		currencyLabel.setText("Currency");
		currencyEdit = new Combo(tab1Container, SWT.READ_ONLY);
		currencyEdit.setItems(new String[] {"USD","EUR","CHF","JPY","AUD","GBP"}); // TODO: add preferences for currency setup - or writable combo + state store
		currencyEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}});
		
		Label strikeLabel = new Label(tab1Container, SWT.NULL);
		strikeLabel.setText("Strike");
		strikeEdit = new Text(tab1Container, SWT.NULL);
		strikeEdit.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}});

		Label exchangeLabel = new Label(tab1Container, SWT.NULL);
		exchangeLabel.setText("Exchange");
		exchangeEdit = new Text(tab1Container, SWT.NULL);
		exchangeEdit.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}});
		
		Label multiplierLabel = new Label(tab1Container, SWT.NULL);
		multiplierLabel.setText("Multiplier");
		multiplierEdit = new Text(tab1Container, SWT.NULL);
		multiplierEdit.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}});
		
		// tab 2: contract ID
		Composite tab2Container = new Composite(contractSpecFolder, SWT.NULL);
		tab2Container.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridLayout tab2Layout = new GridLayout();
		tab2Layout.numColumns = 2;
		tab2Container.setLayout(tab2Layout);
		
		Label idTypeLabel = new Label(tab2Container, SWT.NULL);
		idTypeLabel.setText("Contract ID Type");
		
		idTypeEdit = new Combo(tab2Container, SWT.READ_ONLY);
		String[] idTypes = new String[IdentifierType.values().length];
		for (int i = 0; i < IdentifierType.values().length; i++) {
			idTypes[i] = IdentifierType.values()[i].name();
		}
		idTypeEdit.setItems(idTypes);
		idTypeEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}});
		
		Label idLabel = new Label(tab2Container, SWT.NULL);
		idLabel.setText("Contract ID");
		
		idEdit = new Text(tab2Container, SWT.NULL);
		GridData idEditLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		idEdit.setLayoutData(idEditLayoutData);
		idEdit.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}});
		
		tab2.setControl(tab2Container);

		initialize();
		
		setControl(rootContainer);
	}
	
	public boolean isExactContractSpec() {
		return tab2 == contractSpecFolder.getSelection()[0];
	}
	
	public SecurityType getSecurityType() {
		String str = typeEdit.getText();
		if (str == null || str.trim().length() == 0) {
			return null;
		} else {
			return SecurityType.valueOf(str);
		}
	}
	
	public String getTicker() {
		return tickerEdit.getText();
	}
	
	public Currency getCurrency() {
		String str = currencyEdit.getText();
		if (str == null || str.trim().length() == 0) {
			return null;
		} else {
			return Currency.getInstance(str);
		}
	}
	
	public String getExchange() {
		return exchangeEdit.getText();
	}
	
	public OptionRight getOptionRight() {
		String str = rightEdit.getText();
		if (str == null || str.trim().length() == 0) {
			return null;
		} else {
			return OptionRight.valueOf(str);
		}
	}
	
	public double getOptionStrike() {
		if (strikeEdit.getText().length() > 0)
			return Double.valueOf(strikeEdit.getText());
		else
			return 0.0;
	}
	
	public int getExpiryDay() {
		return expiryEdit.getDay();
	}

	public int getExpiryMonth() {
		return expiryEdit.getMonth();
	}

	public int getExpiryYear() {
		return expiryEdit.getYear();
	}

	public BareDate getExpiry() {
		if (getExpiryDay() > 0 && getExpiryYear() > 0) {
			return new BareDate(getExpiryYear(), getExpiryMonth(), getExpiryDay());
		} else {
			return null;
		}
	}
	
	public Integer getMultiplier() {
		String str = multiplierEdit.getText();
		if (str == null || str.trim().length() == 0) {
			return null;
		} else {
			return new Integer(multiplierEdit.getText());
		}
	}

	public IdentifierType getIdentifierType() {
		String str = idTypeEdit.getText();
		if (str == null || str.trim().length() == 0) {
			return null;
		} else {
			return IdentifierType.valueOf(str);
		}
	}
	
	public String getIdentifier() {
		return idEdit.getText();
	}
	
	private boolean isExpiryEnabled() {
		String type = typeEdit.getText();
		if ("OPT".equals(type) || "FUT".equals(type))
			return true;
		else
			return false;
	}
	
	private boolean isOption() {
		return SecurityType.OPT.equals(getSecurityType());
	}
	
	private boolean isFuture() {
		return SecurityType.FUT.equals(getSecurityType());
	}
	
	private boolean isMultiplierEnabled() {
		return isOption() || isFuture();
	}
	
	private void initialize() {
		typeEdit.select(0);
		rightEdit.select(0);
		currencyEdit.select(0);
		idTypeEdit.select(0);
	}
	
	private boolean checkPageComplete() {
		if (isExactContractSpec())
			return getIdentifierType() != null && getIdentifier().length() > 0;
		else
			return getSecurityType() != null && getTicker().length() > 0;
	}
	
	private void update() {
		expiryEdit.setEnabled(isExpiryEnabled());
		rightEdit.setEnabled(isOption());
		multiplierEdit.setEnabled(isMultiplierEnabled());
		strikeEdit.setEnabled(isOption());
		if (isExactContractSpec()) {
			// reset descriptive criteria
			contractCriteria.setSecurityType(null);
			contractCriteria.setSymbol(null);
			contractCriteria.setExchange(null);
			contractCriteria.setCurrency(null);
			contractCriteria.setOptionRight(null);
			contractCriteria.setMultiplier(null);
			contractCriteria.setStrike(null);
			contractCriteria.setExpiryDate(null);
			// set security ID
			contractCriteria.setIdentifierType(getIdentifierType());
			contractCriteria.setIdentifier(getIdentifier());
		} else {
			// reset security ID
			contractCriteria.setIdentifierType(null);
			contractCriteria.setIdentifier(null);
			// reset descriptive criteria
			contractCriteria.setSecurityType(getSecurityType());
			contractCriteria.setSymbol(getTicker());
			contractCriteria.setExchange(getExchange());
			contractCriteria.setCurrency(getCurrency());
			if (isOption()) {
				contractCriteria.setOptionRight(getOptionRight());
				contractCriteria.setStrike(getOptionStrike());
			} else {
				contractCriteria.setOptionRight(null);
				contractCriteria.setStrike(null);
			}
			if (isOption() || isFuture()) {
				contractCriteria.setMultiplier(getMultiplier());
				contractCriteria.setExpiryDate(getExpiry());
			} else {
				contractCriteria.setMultiplier(null);
				contractCriteria.setExpiryDate(null);
			}
		}
		getWizard().getContainer().updateButtons();
		setPageComplete(canFlipToNextPage());
	}
	
	@Override
	public boolean isPageComplete() {
		return checkPageComplete();
	}
}
