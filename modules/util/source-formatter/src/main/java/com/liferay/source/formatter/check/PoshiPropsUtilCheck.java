/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PoshiPropsUtilCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith(".testcase") || SourceUtil.isXML(content)) {
			return content;
		}

		File file = new File(getPortalDir(), "test.properties");

		if (!file.exists()) {
			return content;
		}

		Properties properties = new Properties();

		properties.load(new FileInputStream(file));

		String password = properties.getProperty(
			"test.portal.default.admin.password");

		if (Validator.isNull(password)) {
			return content;
		}

		Pattern pattern = Pattern.compile("[Pp]assword = \"" + password + "\"");

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			addMessage(
				fileName,
				"Use 'PropsUtil.get(\"default.admin.password\")' instead of " +
					"hardcoded value",
				getLineNumber(content, matcher.start()));
		}

		return content;
	}

}