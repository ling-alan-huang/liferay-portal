/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.util.LoggingTimer;

import java.sql.PreparedStatement;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseRepositoryUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updateRepositoryPortletId();
	}

	protected abstract String[][] getRenamePortletNamesArray();

	protected void updateRepositoryPortletId() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update Repository set portletId = ?, name = ? where " +
						"portletId = ?")) {

			for (String[] renamePortletNames : getRenamePortletNamesArray()) {
				preparedStatement.setString(1, renamePortletNames[1]);
				preparedStatement.setString(2, renamePortletNames[1]);
				preparedStatement.setString(3, renamePortletNames[0]);

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
	}

}