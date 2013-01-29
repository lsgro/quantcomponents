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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class DateCombo extends Composite {
	private static final int NO_YEARS = 10;
	private Label dayLabel;
	private Combo dayEdit;
	private Label monthLabel;
	private Combo monthEdit;
	private Label yearLabel;
	private Combo yearEdit;
	private int startYear;

	public DateCombo(Composite parent, int style) {
		super(parent, style);
		createControls();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		dayLabel.setEnabled(enabled);
		dayEdit.setEnabled(enabled);
		monthLabel.setEnabled(enabled);
		monthEdit.setEnabled(enabled);
		yearLabel.setEnabled(enabled);
		yearEdit.setEnabled(enabled);
	}
	
	public int getDay() {
		int selectedIndex = dayEdit.getSelectionIndex();
		if (selectedIndex > -1)
			selectedIndex++;
		return selectedIndex;
	}
	
	public int getMonth() {
		int selectedIndex = monthEdit.getSelectionIndex();
		if (selectedIndex > -1)
			selectedIndex++;
		return selectedIndex;
	}
	
	public int getYear() {
		int selectedIndex = yearEdit.getSelectionIndex();
		if (selectedIndex > -1)
			selectedIndex += startYear;
		return selectedIndex;
	}
	
	public void addSelectionListener(SelectionListener listener) {
		dayEdit.addSelectionListener(listener);
		monthEdit.addSelectionListener(listener);
		yearEdit.addSelectionListener(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		dayEdit.removeSelectionListener(listener);
		monthEdit.removeSelectionListener(listener);
		yearEdit.removeSelectionListener(listener);
	}
	
	private String[] calculateYears() {
		NumberFormat nfYear = new DecimalFormat("0000");
		String[] years = new String[NO_YEARS];
		Calendar calendar = Calendar.getInstance();
		startYear = calendar.get(Calendar.YEAR);
		for (int i = 0; i < NO_YEARS; i++) {
			years[i] = nfYear.format(startYear + i);
		}
		return years;
	}

	private void createControls() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		setLayout(layout);
				
		dayLabel = new Label(this, SWT.NULL);
		dayLabel.setText("d");

		dayEdit = new Combo(this, SWT.READ_ONLY);
		dayEdit.setItems(new String[] {"01","02","03","04","05","06","07","08","09","10",
				"11","12","13","14","15","16","17","18","19","20",
				"21","22","23","24","25","26","27","28","29","30","31"});
		
		monthLabel = new Label(this, SWT.NULL);
		monthLabel.setText("m");

		monthEdit = new Combo(this, SWT.READ_ONLY);
		monthEdit.setItems(new String[] {"01","02","03","04","05","06","07","08","09","10","11","12"});
		
		yearLabel = new Label(this, SWT.NULL);
		yearLabel.setText("y");

		yearEdit = new Combo(this, SWT.READ_ONLY);
		yearEdit.setItems(calculateYears());
	}
}
