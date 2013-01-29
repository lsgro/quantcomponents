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

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.quantcomponents.core.utils.LangUtils;

public class TradingAgentConfigurationDialog extends Dialog {
	private static final Logger logger = Logger.getLogger(TradingAgentConfigurationDialog.class.getName());
	private final Map<String, Text> propertyMap = new HashMap<String, Text>();
	private final String[] configurationKeys;
	private final Properties configurationProperties;
	private final boolean modifyAllowed;
	private String name;
	private Composite parent;
	private Text nameEdit;
	private Button loadFileButton;

	public TradingAgentConfigurationDialog(String[] configurationKeys, Properties configurationProperties, boolean modifyAllowed, String name, Shell parentShell) {
		super(parentShell);
		this.configurationKeys = configurationKeys;
		this.configurationProperties = configurationProperties;
		this.modifyAllowed = modifyAllowed;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		this.parent = parent;
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout rootLayout = new GridLayout();
		container.setLayout(rootLayout);
		rootLayout.horizontalSpacing = 20;
		rootLayout.verticalSpacing = 20;
		rootLayout.numColumns = 2;
		Label configNameLabel = new Label(container, SWT.NULL);
		configNameLabel.setText("Configuration name" + (modifyAllowed ? " [optional]" : ""));
		nameEdit = new Text(container, modifyAllowed ? SWT.NULL : SWT.READ_ONLY);
		GridData nameEditLayoutData = new GridData();
		nameEditLayoutData.widthHint = 300;
		nameEdit.setLayoutData(nameEditLayoutData);
		if (name != null) {
			nameEdit.setText(name);
		}
		for (String configurationKey : configurationKeys) {
			String configurationValue = (String) configurationProperties.get(configurationKey);
			Label label = new Label(container, SWT.NULL);
			label.setText(configurationKey);
			Text editor = new Text(container, SWT.RIGHT | (modifyAllowed ? SWT.NULL : SWT.READ_ONLY));
			String textValue = configurationValue == null ? "" : configurationValue;
			editor.setText(textValue);
			editor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			propertyMap.put(configurationKey, editor);
		}
		if (modifyAllowed) {
			loadFileButton = new Button(container, SWT.NULL);
			loadFileButton.setText("Load from property file");
			final Properties propertiesFromFile = new Properties();
			loadFileButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					FileDialog dialog = new FileDialog(TradingAgentConfigurationDialog.this.parent.getShell(), SWT.OPEN);
					String rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
					dialog.setFilterPath(rootPath);
					dialog.setText("Choose configuration file");
					String platform = SWT.getPlatform();
					if (platform.equals("win32") || platform.equals("wpf")) {
						dialog.setFilterNames(new String[] { "Property files", "All Files (*)" });
						dialog.setFilterExtensions(new String[] { "*.properties", "*" });
					}
					String path = dialog.open();
					if (path != null) {
						File propertyFile = new File(path);
						if (!propertyFile.canRead()) {
							MessageDialog.openError(TradingAgentConfigurationDialog.this.parent.getShell(), "Could not read file", "File: " + path + " could not be read");
							return;
						}
						try {
							propertiesFromFile.load(new FileInputStream(propertyFile));
						} catch (Exception e) {
							MessageDialog.openError(TradingAgentConfigurationDialog.this.parent.getShell(), "Could not read file", "File: " + path + " could not be read. [" + LangUtils.exceptionMessage(e) + "]");
							logger.log(Level.SEVERE, "File: " + path + " could not be read", e);
							return;
						}
					} 
					displayProperties(propertiesFromFile);
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent event) {
				}
			});
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
			applyProperties();
		}
		setReturnCode(OK);
		close();
	}

	private void applyProperties() {
		for (Map.Entry<String, Text> entry : propertyMap.entrySet()) {
			String value = entry.getValue().getText();
			if (value != null && !value.equals("")) {
				configurationProperties.put(entry.getKey(), value);
			}
		}
		name = nameEdit.getText();
	}
	private void displayProperties(Properties properties) {
		for (Map.Entry<String, Text> entry : propertyMap.entrySet()) {
			String value = properties.getProperty(entry.getKey());
			if (value != null) {
				entry.getValue().setText(value);
			}
		}
	}
}
