/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaEmptyLineInTryWithResourcesStatementCheck
	extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		int x = -1;

		while (true) {
			x = content.indexOf("\ttry (", x + 1);

			if (x == -1) {
				return content;
			}

			int y = x;

			while (true) {
				y = content.indexOf(") {\n", y + 1);

				if (y == -1) {
					break;
				}

				String tryWithResources = content.substring(x + 6, y);

				int level = getLevel(
					tryWithResources, new String[] {"(", "{"},
					new String[] {")", "}"});

				if (level != 0) {
					continue;
				}

				String newTryWithResources = _fixMissingEmptyLine(
					tryWithResources);

				if (tryWithResources.equals(newTryWithResources)) {
					break;
				}

				content = StringUtil.replaceFirst(
					content, tryWithResources, newTryWithResources, x);

				y = x + newTryWithResources.length() + 4;
			}
		}
	}

	private String _fixMissingEmptyLine(String content) {
		int x = -1;

		int length = x;

		while (true) {
			String resource = _getResource(content, length + 1);

			if (!resource.endsWith(";")) {
				return content;
			}

			length = resource.length();

			int y = content.indexOf(resource);

			String s = content.substring(length + y, length + y + 2);

			if (s.equals("\n\n")) {
				length = length + y;

				continue;
			}

			Matcher matcher = _variableDefinitionPattern.matcher(resource);

			if (!matcher.find()) {
				continue;
			}

			String followingCode = content.substring(length + y);

			String nextResource = _getResource(followingCode, 0);

			String variableName = matcher.group(1);

			if (!nextResource.matches("(?s).*\\b" + variableName + "\\b.*")) {
				y = content.indexOf(nextResource);

				length = y + nextResource.length();

				continue;
			}

			content = StringUtil.replaceFirst(
				content, resource, resource + "\n");

			content = _fixMissingEmptyLine(content);

			return content;
		}
	}

	private String _getResource(String content, int x) {
		if (x > content.length()) {
			return content;
		}

		int y = x;

		while (true) {
			y = content.indexOf(";", y + 1);

			if (y == -1) {
				return content.substring(x);
			}

			String s = content.substring(x, y + 1);

			int level = getLevel(
				s, new String[] {"(", "{"}, new String[] {")", "}"});

			if (level != 0) {
				continue;
			}

			return s;
		}
	}

	private static final Pattern _variableDefinitionPattern = Pattern.compile(
		".+?[ \t](\\w+) =.+", Pattern.DOTALL);

}