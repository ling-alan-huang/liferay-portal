/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
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

		Properties properties = new Properties();

		properties.load(new StringReader(content));

		Enumeration<String> enumeration =
			(Enumeration<String>)properties.propertyNames();

		while (enumeration.hasMoreElements()) {
			List<String> duplicateDescriptions = new ArrayList<>();

			String description = properties.getProperty(
				enumeration.nextElement());

			try (UnsyncBufferedReader unsyncBufferedReader =
					new UnsyncBufferedReader(new UnsyncStringReader(content))) {

				String line = null;

				while ((line = unsyncBufferedReader.readLine()) != null) {
					String[] array = line.split("=", 2);

					if (StringUtil.equals(description, array[1])) {
						duplicateDescriptions.add(array[0]);
					}
				}

				if (duplicateDescriptions.size() >= 2) {
					addMessage(
						fileName,
						"Same descriptions found in the following language " +
							"keys: " +
								StringUtil.merge(duplicateDescriptions, ", "));
				}
			}
		}

		return content;
	}

}