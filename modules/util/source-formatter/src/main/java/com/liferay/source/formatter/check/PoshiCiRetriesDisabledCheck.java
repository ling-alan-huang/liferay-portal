/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qi Zhang
 */
public class PoshiCiRetriesDisabledCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith(".testcase")) {
			return content;
		}

		String testCaseName = fileName.substring(
			fileName.lastIndexOf(CharPool.SLASH) + 1);

		if (!testCaseName.contains("smoke") &&
			!testCaseName.contains("Smoke")) {

			return content;
		}

		boolean ciRetiresDisabled = false;

		Matcher matcher1 = _ciRetriesDisabledPattern.matcher(content);

		outLooper:
		while (matcher1.find()) {
			int x = matcher1.start();

			while (true) {
				x = content.lastIndexOf(StringPool.OPEN_CURLY_BRACE, x);

				if (x == -1) {
					continue outLooper;
				}

				int level = getLevel(
					content.substring(0, x + 1), StringPool.OPEN_CURLY_BRACE,
					StringPool.CLOSE_CURLY_BRACE);

				if (level != 1) {
					x--;

					continue;
				}

				break;
			}

			int newLinePos = content.lastIndexOf(StringPool.NEW_LINE, x);

			if (newLinePos == -1) {
				newLinePos = 0;
			}

			String methodNameBlock = content.substring(newLinePos, x + 1);

			Matcher matcher2 = _methodNamePattern.matcher(methodNameBlock);

			if (!matcher2.find()) {
				continue;
			}

			String methodName = matcher2.group(1);

			String value = matcher1.group(1);

			if (value.startsWith("\"")) {
				value = StringUtil.unquote(value);
			}

			if (methodName.equals("definition") && value.equals("true")) {
				ciRetiresDisabled = true;
			}
			else if (!methodName.equals("setUp {") &&
					 !methodName.equals("tearDown {")) {

				int startPos = matcher1.start();

				int endPos = content.indexOf(StringPool.NEW_LINE, startPos + 1);

				if (endPos == -1) {
					continue;
				}

				return content.substring(0, startPos) +
					content.substring(endPos);
			}
		}

		if (!ciRetiresDisabled) {
			addMessage(
				fileName,
				"Should add property ci.retries.disabled = \"true\" in " +
					"definition");
		}

		return content;
	}

	private static final Pattern _ciRetriesDisabledPattern = Pattern.compile(
		"\n\t*property ci\\.retries\\.disabled = ([^;]+);");
	private static final Pattern _methodNamePattern = Pattern.compile(
		"([\\w ]+) \\{");

}