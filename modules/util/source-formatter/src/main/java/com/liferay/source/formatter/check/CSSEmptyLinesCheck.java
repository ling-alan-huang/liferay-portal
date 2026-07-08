/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.CSSSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class CSSEmptyLinesCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		Matcher matcher = _sassVariablePattern.matcher(content);

		while (matcher.find()) {
			Tuple variableDeclarationTuple =
				CSSSourceUtil.getVariableDeclarationTuple(content, matcher);

			if (variableDeclarationTuple == null) {
				return content;
			}

			String variableDeclaration =
				(String)variableDeclarationTuple.getObject(0);

			String newVariableDeclaration =
				_fixMissingEmptyLinesAroundChildSassMap(variableDeclaration);

			if (newVariableDeclaration.equals(variableDeclaration)) {
				continue;
			}

			return StringUtil.replaceFirst(
				content, variableDeclaration, newVariableDeclaration,
				matcher.start());
		}

		return content;
	}

	private String _fixMissingEmptyLineAfterChildSassMap(String s, int x) {
		if (s.charAt(x) == '\n') {
			return s;
		}

		String line = getLine(s, getLineNumber(s, x));

		String trimmedLine = line.trim();

		if (trimmedLine.equals("),")) {
			return s;
		}

		return StringUtil.insert(s, "\n", x);
	}

	private String _fixMissingEmptyLineBeforeChildSassMap(String s, int x) {
		if (s.charAt(x - 1) == '\n') {
			return s;
		}

		String line = getLine(s, getLineNumber(s, x - 1));

		String trimmedLine = line.trim();

		if (trimmedLine.endsWith("(")) {
			return s;
		}

		return StringUtil.insert(s, "\n", x);
	}

	private String _fixMissingEmptyLinesAroundChildSassMap(String content) {
		Matcher matcher = _sassMapPattern.matcher(content);

		while (matcher.find()) {
			int x = matcher.end();

			while (true) {
				x = content.indexOf("),\n", x + 1);

				if ((x == -1) ||
					CSSSourceUtil.isComment(
						SourceUtil.getLine(
							content, SourceUtil.getLineNumber(content, x)))) {

					continue;
				}

				String s = content.substring(matcher.start(), x + 3);

				if (ToolsUtil.getLevel(s) == 0) {
					break;
				}
			}

			String newContent = _fixMissingEmptyLineAfterChildSassMap(
				content, x + 3);

			if (!content.equals(newContent)) {
				return newContent;
			}

			newContent = _fixMissingEmptyLineBeforeChildSassMap(
				newContent, matcher.start());

			if (!content.equals(newContent)) {
				return newContent;
			}
		}

		return content;
	}

	private static final Pattern _sassMapPattern = Pattern.compile(
		"\n[\t ]+(\\w[\\w-]*):.*\\(\n");
	private static final Pattern _sassVariablePattern = Pattern.compile(
		"^\\$([\\w-]+):", Pattern.MULTILINE);

}