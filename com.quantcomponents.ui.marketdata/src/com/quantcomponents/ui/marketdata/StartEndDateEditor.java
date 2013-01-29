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
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

public class StartEndDateEditor extends Composite {

	private Date startDate;
	private Date endDate;
	private boolean movingWindow;

	private Label startDateLabel;
	private DateTime startDateEdit;
	private Label startTimeLabel; 
	private DateTime startTimeEdit;

	private Label endDateLabel;
	private DateTime endDateEdit;
	private Label endTimeLabel; 
	private DateTime endTimeEdit;
	
	private Button movingWindowEdit;

	public StartEndDateEditor(Composite parent, Date startDate, Date endDate, boolean movingWindow) {
		super(parent, SWT.NULL);
		this.startDate = startDate;
		this.endDate = endDate;
		this.movingWindow = movingWindow;
		createContents(this);
	}

	private Control createContents(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 5;
		parent.setLayout(layout);
		
		startDateLabel = new Label(parent, SWT.NULL);
		startDateLabel.setText("Start Date");
		
		endDateLabel = new Label(parent, SWT.NULL);
		endDateLabel.setText("End Date");
		
		startDateEdit = new DateTime(parent, SWT.CALENDAR);
		GridData startDateLayoutData = new GridData();
		startDateEdit.setLayoutData(startDateLayoutData);
		startDateEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateValues();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}});
		
		endDateEdit = new DateTime(parent, SWT.CALENDAR);
		GridData endDateLayoutData = new GridData();
		endDateEdit.setLayoutData(endDateLayoutData);
		endDateEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateValues();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}});
		
		startTimeLabel = new Label(parent, SWT.NULL);
		startTimeLabel.setText("Start Time");
		
		endTimeLabel = new Label(parent, SWT.NULL);
		endTimeLabel.setText("End Time");
		
		startTimeEdit = new DateTime(parent, SWT.DROP_DOWN | SWT.TIME | SWT.MEDIUM);
		startTimeEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateValues();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}});
		
		endTimeEdit = new DateTime(parent, SWT.DROP_DOWN | SWT.TIME | SWT.MEDIUM);
		endTimeEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateValues();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}});
		
		movingWindowEdit = new Button(parent, SWT.CHECK);
		movingWindowEdit.setText("Moving window");
		movingWindowEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshEndDateTime();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}});
		
		initValues(); 
		
		return parent;	
	}
	
	public void refreshWidgets() {
		initValues();
	}
	
	private void refreshEndDateTime() {
		updateValues();
		boolean enableEndDateTime = !movingWindow;
		endDateLabel.setEnabled(enableEndDateTime);
		endDateEdit.setEnabled(enableEndDateTime);
		endTimeLabel.setEnabled(enableEndDateTime);
		endTimeEdit.setEnabled(enableEndDateTime);
	}
	
	private void initValues() {
		Calendar cal = Calendar.getInstance();
		if (startDate != null) {
			cal.setTime(startDate);
		}
		startDateEdit.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
		startTimeEdit.setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		if (endDate != null) {
			cal.setTime(endDate);
		}
		endDateEdit.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
		endTimeEdit.setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		movingWindowEdit.setSelection(movingWindow);
		refreshEndDateTime();
	}
	
	private void updateValues() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.YEAR, startDateEdit.getYear());
		cal.set(Calendar.MONTH, startDateEdit.getMonth());
		cal.set(Calendar.DATE, startDateEdit.getDay());
		cal.set(Calendar.HOUR_OF_DAY, startTimeEdit.getHours());
		cal.set(Calendar.MINUTE, startTimeEdit.getMinutes());
		cal.set(Calendar.SECOND, startTimeEdit.getSeconds());
		startDate = cal.getTime();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.YEAR, endDateEdit.getYear());
		cal.set(Calendar.MONTH, endDateEdit.getMonth());
		cal.set(Calendar.DATE, endDateEdit.getDay());
		cal.set(Calendar.HOUR_OF_DAY, endTimeEdit.getHours());
		cal.set(Calendar.MINUTE, endTimeEdit.getMinutes());
		cal.set(Calendar.SECOND, endTimeEdit.getSeconds());
		endDate = cal.getTime();
		movingWindow = movingWindowEdit.getSelection();
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public boolean isMovingWindow() {
		return movingWindow;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setMovingWindow(boolean movingWindow) {
		this.movingWindow = movingWindow;
	}
}
