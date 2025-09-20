/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

/**
 * @author Alan Huang
 */
public class DefaultAdminScreenNameCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (fileName.endsWith(".properties") &&
			!fileName.endsWith("/portal-impl/src/portal.properties") &&
			content.contains("default.admin.screen.name=")) {

			addMessage(fileName, "Do not use \"default.admin.screen.name\"");
		}
		else if (fileName.endsWith(".java") &&
				 !fileName.endsWith(
					 "portal-impl/src/com/liferay/portal/service/impl" +
						 "/CompanyLocalServiceImpl.java") &&
				 (content.contains("PropsKeys.DEFAULT_ADMIN_SCREEN_NAME") ||
				  content.contains("PropsValues.DEFAULT_ADMIN_SCREEN_NAME"))) {

			addMessage(
				fileName,
				"Do not use \"PropsKeys.DEFAULT_ADMIN_SCREEN_NAME\" or \" + " +
					"PropsValues.DEFAULT_ADMIN_SCREEN_NAME\"");
		}

		return content;
	}

}