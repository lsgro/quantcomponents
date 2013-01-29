/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/
package com.quantcomponents.series.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public class DaoSupport implements IFlushable {
	private final Connection connection;
	
	public DaoSupport(Connection connection) {
		this.connection = connection;
	}
	
	protected Connection getConnection() {
		return connection;
	}

	@Override
	public void flush() throws SQLException {
		connection.commit();
	}
}
