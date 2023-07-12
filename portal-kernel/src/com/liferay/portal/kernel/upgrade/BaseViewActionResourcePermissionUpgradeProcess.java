/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseViewActionResourcePermissionUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		long bitwiseValue = _getBitwiseValue();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select resourcePermissionId, actionIds from ",
					"ResourcePermission where name = ? and primKeyId != ? and ",
					"viewActionId = ?"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update ResourcePermission set actionIds = ? where " +
						"resourcePermissionId = ?")) {

			preparedStatement1.setString(1, getClassName());
			preparedStatement1.setLong(2, 0L);
			preparedStatement1.setBoolean(3, true);

			ResultSet resultSet = preparedStatement1.executeQuery();

			while (resultSet.next()) {
				preparedStatement2.setLong(
					1, bitwiseValue | resultSet.getLong("actionIds"));
				preparedStatement2.setLong(
					2, resultSet.getLong("resourcePermissionId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	protected abstract String getActionId();

	protected abstract String getClassName();

	private long _getBitwiseValue() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select bitwiseValue from ResourceAction where name = ? and " +
					"actionId = ?")) {

			preparedStatement.setString(1, getClassName());
			preparedStatement.setString(2, getActionId());

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getLong("bitwiseValue");
			}

			return 0;
		}
	}

}