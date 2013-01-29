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
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.quantcomponents.core.model.BareDate;
import com.quantcomponents.core.model.IContract;

public class NewHistoricalDataPage2 extends WizardPage {
	private static final int SORT_ASCENDING = 1;
	private IContract selectedContract;
	private IStructuredSelection selection;
	
	private TableViewer contractListViewer;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	protected NewHistoricalDataPage2() {
		super("Select contract");
	}

	public TableViewer getContractListViewer() {
		return contractListViewer;
	}

	@Override
	public void createControl(Composite parent) {		
		// root container
		Composite rootContainer = new Composite(parent, SWT.NULL);
		GridLayout rootLayout = new GridLayout();
		rootLayout.verticalSpacing = 15;
		rootLayout.numColumns = 1;
		rootContainer.setLayout(rootLayout);
		
		Composite contractTableContainer = new Composite(rootContainer, SWT.NULL);
		GridData contractTableLayoutData = new GridData(GridData.FILL_BOTH);
		contractTableContainer.setLayoutData(contractTableLayoutData);
		contractTableContainer.setLayout(new FillLayout());
		
		contractListViewer = new TableViewer(contractTableContainer, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		TableViewerColumn viewColId = new TableViewerColumn(contractListViewer, SWT.NONE);
		viewColId.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IContract c = (IContract)element;
				return c.getBrokerID();
			}});
		TableColumn columnId = viewColId.getColumn();
		columnId.setText("Broker ID");
		columnId.setWidth(100);
		columnId.setResizable(true);

		TableViewerColumn viewColDesc = new TableViewerColumn(contractListViewer, SWT.NONE);
		viewColDesc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IContract c = (IContract)element;
				return c.getContractDescription().getLongName();
			}});
		TableColumn columnDesc = viewColDesc.getColumn();
		columnDesc.setText("Description");
		columnDesc.setWidth(140);
		columnDesc.setResizable(true);

		TableViewerColumn viewColTicker = new TableViewerColumn(contractListViewer, SWT.NONE);
		viewColTicker.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IContract c = (IContract)element;
				return c.getSymbol();
			}});
		TableColumn columnTicker = viewColTicker.getColumn();
		columnTicker.setText("Ticker");
		columnTicker.setWidth(50);
		columnTicker.setResizable(true);
		
		TableViewerColumn viewColType = new TableViewerColumn(contractListViewer, SWT.NONE);
		viewColType.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IContract c = (IContract)element;
				return c.getSecurityType().name();
			}});
		TableColumn columnType = viewColType.getColumn();
		columnType.setText("Type");
		columnType.setWidth(40);
		columnType.setResizable(true);
		
		TableViewerColumn viewColCcy = new TableViewerColumn(contractListViewer, SWT.NONE);
		viewColCcy.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IContract c = (IContract)element;
				return c.getCurrency().getCurrencyCode();
			}});
		TableColumn columnCcy = viewColCcy.getColumn();
		columnCcy.setText("Ccy");
		columnCcy.setWidth(40);
		columnCcy.setResizable(true);
		
		TableViewerColumn viewColExchange = new TableViewerColumn(contractListViewer, SWT.NONE);
		viewColExchange.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IContract c = (IContract)element;
				return c.getExchange();
			}});
		TableColumn columnExchange = viewColExchange.getColumn();
		columnExchange.setText("Exchange");
		columnExchange.setWidth(100);
		columnExchange.setResizable(true);
		
		TableViewerColumn viewColExpiry = new TableViewerColumn(contractListViewer, SWT.NONE);
		viewColExpiry.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IContract c = (IContract)element;
				BareDate expiryDate = c.getExpiryDate();
				if (expiryDate != null) {
					return sdf.format(expiryDate.getDate(TimeZone.getDefault()));
				} else {
					return "";
				}
			}});
		TableColumn columnExpiry = viewColExpiry.getColumn();
		columnExpiry.setText("Expiry");
		columnExpiry.setWidth(90);
		columnExpiry.setResizable(true);
		
		TableViewerColumn viewColStrike = new TableViewerColumn(contractListViewer, SWT.NONE);
		viewColStrike.setLabelProvider(new ColumnLabelProvider() {
			private DecimalFormat doubleFormat = new DecimalFormat("0.000");
			@Override
			public String getText(Object element) {
				IContract c = (IContract)element;
				Double strike = c.getStrike();
				if (strike != null) {
					return doubleFormat.format(strike);
				} else {
					return "";
				}
			}});
		TableColumn columnStrike = viewColStrike.getColumn();
		columnStrike.setText("Strike");
		columnStrike.setWidth(80);
		columnStrike.setResizable(true);
		
		Table table = contractListViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setSortColumn(columnDesc);
		table.setSortDirection(SORT_ASCENDING);
		
		contractListViewer.setContentProvider(new ArrayContentProvider());
		contractListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selection = (IStructuredSelection)event.getSelection();
				if (selection != null) {
					Object firstElement = selection.getFirstElement();
					if (firstElement != null && firstElement instanceof IContract) {
						selectedContract = (IContract)firstElement;
						setPageComplete(true);
					}
				}
			}
		});
		contractListViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				IContract c1 = (IContract) e1;
				IContract c2 = (IContract) e2;
				int cmp = c1.getContractDescription().getLongName().compareToIgnoreCase(c2.getContractDescription().getLongName());
				if (cmp != 0)  {
					return cmp;
				}
				if (c1.getExpiryDate() != null && c2.getExpiryDate() != null) {
					cmp = c1.getExpiryDate().compareTo(c2.getExpiryDate());
					if (cmp != 0) {
						return cmp;
					}
				}
				if (c1.getExchange() != null && c2.getExchange() != null) {
					cmp = c1.getExchange().compareTo(c2.getExchange());
					if (cmp != 0) {
						return cmp;
					}
				}
				return 0;
			}	
		});
		
		setControl(rootContainer);
	}
	
	public IContract getSelectedContract() {
		return selectedContract;
	}
	
	@Override
	public boolean isPageComplete() {
		return selectedContract != null;
	}
}
