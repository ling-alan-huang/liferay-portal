/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * @author Alan Huang
 */
public class PropertiesDuplicateLanguageDescriptionsCheck
	extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith("/content/Language.properties")) {
			return content;
		}

		String s = content + "\n";

		Properties properties = new Properties();

		properties.load(new StringReader(content));

		Enumeration<String> enumeration =
			(Enumeration<String>)properties.propertyNames();

		while (enumeration.hasMoreElements()) {
			List<String> duplicateDescriptions = new ArrayList<>();

			String description = properties.getProperty(
				enumeration.nextElement());

			int x = 0;

			while (true) {
				x = s.indexOf("=" + description + "\n", x);

				if (x == -1) {
					break;
				}

				int y = s.lastIndexOf("\n", x);

				String key = s.substring(y + 1, x);

				if (!key.matches("\\w+(-\\w+)*") ||
					key.endsWith("-configuration-name") ||
					key.startsWith("portlet-display-template-")) {

					x = x + description.length() + 2;

					continue;
				}

				duplicateDescriptions.add(key);

				x = x + description.length() + 2;
			}

			if (duplicateDescriptions.size() >= 2) {
				addMessage(
					fileName,
					"Same descriptions found in the following language keys: " +
						StringUtil.merge(duplicateDescriptions, ", "));
			}
		}

		return content;
	}

}