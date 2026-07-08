/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.tools.ToolsUtil;

import java.util.regex.Matcher;

/**
 * @author Alan Huang
 */
public class CSSSourceUtil {

	public static Tuple getVariableDeclarationTuple(String s, Matcher matcher) {
		int x = matcher.end();

		while (true) {
			x = s.indexOf(StringPool.SEMICOLON, x + 1);

			if (x == -1) {
				return null;
			}

			if (x == (s.length() - 1)) {
				return new Tuple(s.substring(matcher.start()), s.length() - 1);
			}

			if (isComment(
					SourceUtil.getLine(s, SourceUtil.getLineNumber(s, x)))) {

				continue;
			}

			String variableDeclaration = s.substring(matcher.start(), x + 1);

			if (ToolsUtil.getLevel(variableDeclaration) != 0) {
				continue;
			}

			char c = s.charAt(x + 1);

			if (c == CharPool.NEW_LINE) {
				return new Tuple(variableDeclaration, x + 2);
			}

			int index = s.indexOf(StringPool.NEW_LINE, x + 1);

			if (index != -1) {
				return new Tuple(
					s.substring(matcher.start(), index), index + 1);
			}

			return new Tuple(s.substring(matcher.start()), s.length() - 1);
		}
	}

	public static boolean isComment(String line) {
		String trimmedLine = line.trim();

		if (trimmedLine.endsWith("*/") || trimmedLine.startsWith("/*") ||
			trimmedLine.startsWith(StringPool.DOUBLE_SLASH) ||
			trimmedLine.startsWith(StringPool.STAR)) {

			return true;
		}

		return false;
	}

}