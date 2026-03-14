/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import java.sql.PreparedStatement;

/**
 * @author Christopher Kian
 */
public class UpgradeVirtualHost {

	public String method(String month) {
		String abc = switch (month) {
			case "Jan", "Feb", "Mar" -> "Q1";
			case "Apr", "May", "Jun" -> "Q2";
			case "Jul", "Aug", "Sep" -> "Q3";
			case "Oct", "Nov", "Dec" -> "Q4";
			default -> "";
		};

		return abc;
	}

	protected void doUpgrade() {
		setName(() -> "columnId");
		setAge(
			() -> {
				int a = 0;

				return a;
			});

		try (PreparedStatement p1 = connection.
				prepareStatement(
				"select notificationRecipientId, userName from ABC");

		A a =
				null

		) {

			p1.setString(1, NotificationConstants.TYPE_EMAIL);
		}

		try {
			int a = 0;
		}
		catch (Exception e) {
		}

		long ctCollectionId = resultSet1.getLong("ctCollectionId");
		long a = ctCollectionId + 1;
	}

}