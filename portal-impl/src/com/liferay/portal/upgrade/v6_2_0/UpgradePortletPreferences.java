/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringBundler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Julio Camarero
 */
public class UpgradePortletPreferences extends UpgradeProcess {

	protected void deletePortletPreferences() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			try (PreparedStatement preparedStatement1 =
					connection.prepareStatement(
						StringBundler.concat(
							"select PortletPreferences.portletPreferencesId, ",
							"PortletPreferences.plid, PortletPreferences.",
							"portletId, Layout.typeSettings from ",
							"PortletPreferences inner join Layout on ",
							"PortletPreferences.plid = Layout.plid where ",
							"preferences like '%<portlet-preferences />%' or ",
							"preferences like '' or preferences is null"));
				PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						"delete from PortletPreferences where " +
							"portletPreferencesId = ?");
				ResultSet resultSet = preparedStatement1.executeQuery()) {

				while (resultSet.next()) {
					String portletId = GetterUtil.getString(
						resultSet.getString("portletId"));
					String typeSettings = GetterUtil.getString(
						resultSet.getString("typeSettings"));

					if (typeSettings.contains(portletId)) {
						continue;
					}

					long portletPreferencesId = resultSet.getLong(
						"portletPreferencesId");

					if (_log.isDebugEnabled()) {
						_log.debug(
							"Deleting portlet preferences " +
								portletPreferencesId);
					}

					preparedStatement2.setLong(1, portletPreferencesId);

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		deletePortletPreferences();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePortletPreferences.class);

}