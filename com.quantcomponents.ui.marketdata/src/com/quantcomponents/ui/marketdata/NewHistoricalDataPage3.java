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

import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.quantcomponents.core.model.BarSize;
import com.quantcomponents.core.model.DataType;
import com.quantcomponents.core.model.IContract;
import com.quantcomponents.core.model.UnitOfTime;

public class NewHistoricalDataPage3 extends WizardPage {
	private static final Currency DOLLAR = Currency.getInstance("USD");

	private PeriodCombo periodEdit;
	private Combo dataTypeEdit;
	private Combo barSizeEdit;
	private Button afterHoursButton;
	private Button realtimeButton;
	
	private Label endDateLabel;
	private DateTime endDateEdit;
	private Label endTimeLabel; 
	private DateTime endTimeEdit;
	private Label timeZoneLabel;
	private Text timeZoneEdit;
	private Button midnightButton;
	private Button nowButton;

	private TimeZone contractTimeZone;

	protected NewHistoricalDataPage3() {
		super("Download historical data");
	}

	private void initialize() {
		periodEdit.select("1", 1);
		barSizeEdit.select(8);
		dataTypeEdit.select(DataType.TRADES.ordinal());
	}

	@Override
	public void createControl(Composite parent) {		
		// root container
		Composite rootContainer = new Composite(parent, SWT.NULL);
		GridLayout rootLayout = new GridLayout();
		rootLayout.verticalSpacing = 15;
		rootLayout.numColumns = 1;
		rootContainer.setLayout(rootLayout);		

		// Data parameters
		Composite dataSpecContainer = new Composite(rootContainer, SWT.NULL);
		GridLayout dataSpecContainerLayout = new GridLayout();
		dataSpecContainerLayout.numColumns = 4;
		dataSpecContainer.setLayout(dataSpecContainerLayout);
		
		Label periodLabel = new Label(dataSpecContainer, SWT.NULL);
		periodLabel.setText("Period Length");
		
		periodEdit = new PeriodCombo(dataSpecContainer, SWT.NULL);
		GridData periodLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		periodLayoutData.horizontalSpan = 3;
		periodEdit.setLayoutData(periodLayoutData);
		
		Label dataTypeLabel = new Label(dataSpecContainer, SWT.NULL);
		dataTypeLabel.setText("Data Type");
		
		dataTypeEdit = new Combo(dataSpecContainer, SWT.READ_ONLY);
		GridData dataTypeLayoutData = new GridData();
		dataTypeLayoutData.horizontalSpan = 3;
		dataTypeEdit.setLayoutData(dataTypeLayoutData);
		String[] dataTypeValues = new String[DataType.values().length];
		for (int i = 0; i < DataType.values().length; i++) {
			dataTypeValues[i] = DataType.values()[i].name();
		}
		dataTypeEdit.setItems(dataTypeValues);

		Label barSizeLabel = new Label(dataSpecContainer, SWT.NULL);
		barSizeLabel.setText("Bar Size");
		
		barSizeEdit = new Combo(dataSpecContainer, SWT.READ_ONLY);
		String[] barSizeValues = new String[BarSize.values().length];
		for (int i = 0; i < BarSize.values().length; i++) {
			barSizeValues[i] = BarSize.values()[i].name();
		}
		barSizeEdit.setItems(barSizeValues);
		
		afterHoursButton = new Button(dataSpecContainer, SWT.CHECK);
		afterHoursButton.setText("Include after-hours");
		afterHoursButton.setSelection(false);
		
		realtimeButton = new Button(dataSpecContainer, SWT.CHECK);
		realtimeButton.setText("Realtime update");
		realtimeButton.setSelection(false);
		
		endDateLabel = new Label(dataSpecContainer, SWT.NULL);
		endDateLabel.setText("End Date");
		
		endDateEdit = new DateTime(dataSpecContainer, SWT.DROP_DOWN | SWT.DATE | SWT.LONG);
		GridData endDateLayoutData = new GridData();
		endDateLayoutData.horizontalSpan = 3;
		endDateEdit.setLayoutData(endDateLayoutData);
		
		endTimeLabel = new Label(dataSpecContainer, SWT.NULL);
		endTimeLabel.setText("End Time");
		endTimeEdit = new DateTime(dataSpecContainer, SWT.DROP_DOWN | SWT.TIME | SWT.MEDIUM);
		
		midnightButton = new Button(dataSpecContainer, SWT.PUSH);
		midnightButton.setText("midnight");
		GridData midnightLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		midnightButton.setLayoutData(midnightLayoutData);
		midnightButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				endTimeEdit.setTime(0, 0, 0);
			}});
		
		nowButton = new Button(dataSpecContainer, SWT.PUSH);
		nowButton.setText("now");
		GridData nowLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		nowButton.setLayoutData(nowLayoutData);
		nowButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Calendar now = Calendar.getInstance(contractTimeZone);
				endDateEdit.setDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
				endTimeEdit.setTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));
			}});
		
		realtimeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enableEndDateTime = !realtimeButton.getSelection();
				endDateLabel.setEnabled(enableEndDateTime);
				endDateEdit.setEnabled(enableEndDateTime);
				endTimeLabel.setEnabled(enableEndDateTime);
				endTimeEdit.setEnabled(enableEndDateTime);
				timeZoneLabel.setEnabled(enableEndDateTime);
				timeZoneEdit.setEnabled(enableEndDateTime);
				midnightButton.setEnabled(enableEndDateTime);
				nowButton.setEnabled(enableEndDateTime);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
			});
		
		timeZoneLabel = new Label(dataSpecContainer, SWT.NULL);
		timeZoneLabel.setText("Time zone");
		
		timeZoneEdit = new Text(dataSpecContainer, SWT.NULL);
		GridData timeZoneEditLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		timeZoneEdit.setLayoutData(timeZoneEditLayoutData);
		
		initialize();
		
		setControl(rootContainer);
	}
	
	public void setSelectedContract(IContract selectedContract) {
		if (selectedContract.getContractDescription() != null && selectedContract.getContractDescription().getTimeZone() != null) {
			contractTimeZone = selectedContract.getContractDescription().getTimeZone();
		} else {
			if (DOLLAR.equals(selectedContract.getCurrency())) {
				contractTimeZone = TimeZone.getTimeZone("America/New_York");
			} else {
				contractTimeZone = TimeZone.getTimeZone("GMT");
			}
		}
		timeZoneEdit.setText(contractTimeZone.getID());
		Calendar now = Calendar.getInstance(contractTimeZone);
		endDateEdit.setDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
		endTimeEdit.setTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));
	}
	
	public int getPeriodAmount() {
		return periodEdit.getAmount();
	}
	
	public UnitOfTime getPeriodUnit() {
		return periodEdit.getUnit();
	}
	
	public Date getEndDate() {
		Calendar date = Calendar.getInstance();
		date.setTimeZone(getTimeZone());
		date.set(Calendar.MILLISECOND, 0);
		date.set(Calendar.YEAR, endDateEdit.getYear());
		date.set(Calendar.MONTH, endDateEdit.getMonth());
		date.set(Calendar.DATE, endDateEdit.getDay());
		date.set(Calendar.HOUR_OF_DAY, endTimeEdit.getHours());
		date.set(Calendar.MINUTE, endTimeEdit.getMinutes());
		date.set(Calendar.SECOND, endTimeEdit.getSeconds());
		return date.getTime();
	}
	
	public TimeZone getTimeZone() {
		String timeZoneId = timeZoneEdit.getText();
		if (timeZoneId.length() > 0) {
			return TimeZone.getTimeZone(timeZoneId);
		} else {
			return TimeZone.getDefault();
		}
	}
	
	public DataType getDataType() {
		return DataType.valueOf(dataTypeEdit.getText());
	}
	
	public BarSize getBarSize() {
		return BarSize.valueOf(barSizeEdit.getText());
	}
	
	public boolean isAfterHoursIncluded() {
		return afterHoursButton.getSelection();
	}
	
	public boolean isRealtimeUpdate() {
		return realtimeButton.getSelection();
	}
}
