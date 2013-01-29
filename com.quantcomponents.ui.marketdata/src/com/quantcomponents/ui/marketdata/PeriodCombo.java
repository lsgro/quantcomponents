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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.quantcomponents.core.model.UnitOfTime;

public class PeriodCombo extends Composite implements SelectionListener, ModifyListener {
	private Combo unitEdit;
	private Text amountEdit;
	private List<PeriodComboListener> listeners = new ArrayList<PeriodComboListener>();
	
	public static class PeriodComboEvent {
		public PeriodComboEvent(Object source, UnitOfTime unit, int amount) {
			this.source = source;
			this.unit = unit;
			this.amount = amount;
		}
		public Object source;
		public UnitOfTime unit;
		public int amount;
	}
	
	public interface PeriodComboListener {
		void onPeriodChange(PeriodComboEvent e);
	}

	public PeriodCombo(Composite parent, int style) {
		super(parent, style);
		createControls();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		unitEdit.setEnabled(enabled);
		amountEdit.setEnabled(enabled);
	}
	
	public void select(String amount, int unitIndex) {
		unitEdit.select(unitIndex);
		amountEdit.setText(amount);
	}
	
	public void addPeriodComboListener(PeriodComboListener listener) {
		listeners.add(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		listeners.remove(listener);
	}
	
	public UnitOfTime getUnit() {
		String text = unitEdit.getText();
		if (text.length() > 0)
			return UnitOfTime.valueOf(text);
		else
			return null;
	}
	
	public int getAmount() {
		String text = amountEdit.getText();
		if (text.length() > 0)
			return Integer.valueOf(amountEdit.getText());
		else
			return 0;
	}
	
	private void createControls() {
		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		amountEdit = new Text(this, SWT.RIGHT);
		
		unitEdit = new Combo(this, SWT.READ_ONLY);
		String[] units = new String[UnitOfTime.values().length];
		for (int i = 0; i < UnitOfTime.values().length; i++)
			units[i] = UnitOfTime.values()[i].name();
		unitEdit.setItems(units);
		
		unitEdit.addSelectionListener(this);
		amountEdit.addModifyListener(this);
	}

	private void notifyListeners() {
		for (PeriodComboListener listener : listeners) {
			listener.onPeriodChange(new PeriodComboEvent(this, getUnit(), getAmount()));
		}
	}
	
	@Override
	public void modifyText(ModifyEvent e) {
		notifyListeners();		
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		notifyListeners();		
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}
}
