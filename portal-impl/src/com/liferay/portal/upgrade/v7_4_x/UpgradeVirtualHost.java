/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Christopher Kian
 */
public class UpgradeVirtualHost extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement p1 = connection.prepareStatement(
				"select notificationRecipientId, userName from ABC")

		) {

			p1.setString(1, NotificationConstants.TYPE_EMAIL);
		}
	}

	private void _deleteNotificationQueueEntries() throws Exception {
		try (PreparedStatement deletePreparedStatement1 =
				connection.prepareStatement(
					"select ctCollectionId, virtualHostId, hostname from " +
						"VirtualHost where hostname != LOWER(hostname)");
			PreparedStatement selectPreparedStatement2 =
				connection.prepareStatement(
					"select ctCollectionId, virtualHostId, hostname from " +
						"VirtualHost where hostname != LOWER(hostname)");

			ResultSet resultSet1 = selectPreparedStatement2.executeQuery()) {

			while (resultSet1.next()) {
				long ctCollectionId = resultSet1.getLong("ctCollectionId");
				long a = ctCollectionId + 1;
			}

			deletePreparedStatement1.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeVirtualHost.class);

}