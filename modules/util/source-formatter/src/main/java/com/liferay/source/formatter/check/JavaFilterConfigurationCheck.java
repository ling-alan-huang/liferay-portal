/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.io.IOException;

import java.util.Properties;

/**
 * @author Alan Huang
 */
public class JavaFilterConfigurationCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith("Filter.java")) {
			return content;
		}

		String packageName = JavaSourceUtil.getPackageName(content);

		if (!packageName.startsWith("com.liferay.")) {
			return content;
		}

		String fullyQualifiedClassName =
			packageName + "." + JavaSourceUtil.getClassName(fileName);

		Properties properties = new Properties();

		PropertiesUtil.load(
			properties,
			getPortalContent(
				"portal-impl/src/portal.properties", absolutePath));

		if (isDerivedFrom(
				absolutePath, content,
				"com.liferay.portal.kernel.servlet.BaseFilter") &&
			(properties.getProperty(fullyQualifiedClassName) != null)) {

			addMessage(
				fileName,
				"Do not add property \"" + fullyQualifiedClassName +
					"\" in portal.properties, see LPD-69645");

			return content;
		}

		if (isDerivedFrom(
				absolutePath, content,
				"com.liferay.portal.servlet.filters.BasePortalFilter") &&
			(properties.getProperty(fullyQualifiedClassName) == null)) {

			addMessage(
				fileName,
				"Missing property \"" + fullyQualifiedClassName +
					"\" in portal.properties, see LPD-69645");

			return content;
		}

		return content;
	}

}