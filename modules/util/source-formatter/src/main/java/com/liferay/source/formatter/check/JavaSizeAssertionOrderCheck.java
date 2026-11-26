/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaSizeAssertionOrderCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.endsWith("Test.java")) {
			return content;
		}

		Matcher matcher = _sizeAssertionPattern.matcher(content);

		while (matcher.find()) {
			String newContent = _moveSizeAssertion(
				content, matcher.start(), matcher.group(1), matcher.group(2));

			if (!newContent.equals(content)) {
				return newContent;
			}
		}

		return content;
	}

	private int _getStatementEndIndex(String content, String indent, int x) {
		int y = content.indexOf(CharPool.NEW_LINE, x) + 1;

		int endIndex = y;

		while (y < content.length()) {
			int z = content.indexOf(CharPool.NEW_LINE, y);

			if (z == -1) {
				break;
			}

			String line = content.substring(y, z);

			if (line.isEmpty()) {
				y = z + 1;

				continue;
			}

			int tabCount = _getTabCount(line);

			if (tabCount < indent.length()) {
				break;
			}

			if (tabCount == indent.length()) {
				String s = line.substring(tabCount);

				if (!s.startsWith(")") && !s.startsWith("catch ") &&
					!s.startsWith("else") && !s.startsWith("finally ") &&
					!s.startsWith("}")) {

					break;
				}
			}

			endIndex = z + 1;
			y = z + 1;
		}

		return endIndex;
	}

	private int _getTabCount(String line) {
		int count = 0;

		while ((count < line.length()) && (line.charAt(count) == '\t')) {
			count++;
		}

		return count;
	}

	private boolean _hasVariableName(String s, Set<String> variableNames) {
		for (String variableName : variableNames) {
			Pattern pattern = Pattern.compile("\\b" + variableName + "\\b");

			Matcher matcher = pattern.matcher(s);

			if (matcher.find()) {
				return true;
			}
		}

		return false;
	}

	private boolean _isModifiable(
		String statement, String indent, String variableName) {

		if (statement.startsWith(indent + "Assert.")) {
			return false;
		}

		Pattern pattern = Pattern.compile(
			"\\b" + variableName +
				"\\b(?!\\.(?:get\\w*|length|size|toString)\\()");

		Matcher matcher = pattern.matcher(statement);

		return matcher.find();
	}

	private String _moveSizeAssertion(
		String content, int start, String indent, String variableName) {

		int end = content.indexOf(";\n", start) + 2;

		if (!content.startsWith("\n" + indent, end)) {
			return content;
		}

		String nextStatement = content.substring(
			end + 1, _getStatementEndIndex(content, indent, end + 1));

		Matcher matcher = _variableDeclarationPattern.matcher(nextStatement);

		Pattern pattern = Pattern.compile(
			"\\b" + variableName + "\\.get\\w*\\(");

		Matcher getterMatcher = pattern.matcher(nextStatement);

		if (!matcher.lookingAt() || !getterMatcher.find()) {
			return content;
		}

		String sizeAssertion = content.substring(start, end);

		Set<String> variableNames = new HashSet<>();

		variableNames.add(variableName);

		int lastIndex = -1;

		int x = end;

		while (true) {
			while (content.startsWith("\n", x)) {
				x++;
			}

			if (!content.startsWith(indent, x) ||
				(content.charAt(x + indent.length()) == '\t') ||
				(content.charAt(x + indent.length()) == '}')) {

				break;
			}

			int y = _getStatementEndIndex(content, indent, x);

			String statement = content.substring(x, y);

			if (_isModifiable(statement, indent, variableName)) {
				return content;
			}

			if (_hasVariableName(statement, variableNames)) {
				lastIndex = y;

				matcher = _variableDeclarationPattern.matcher(statement);

				if (matcher.lookingAt()) {
					variableNames.add(matcher.group(1));
				}
			}

			x = y;
		}

		if (lastIndex == -1) {
			return content;
		}

		int removeEnd = end + 1;

		if (content.startsWith(";\n", start - 2)) {
			removeEnd = end;
		}

		String trailing = StringPool.BLANK;

		if (content.startsWith(indent, lastIndex) &&
			(content.charAt(lastIndex + indent.length()) != '\t') &&
			(content.charAt(lastIndex + indent.length()) != '}')) {

			trailing = StringPool.NEW_LINE;
		}

		return StringBundler.concat(
			content.substring(0, start),
			content.substring(removeEnd, lastIndex), StringPool.NEW_LINE,
			sizeAssertion, trailing, content.substring(lastIndex));
	}

	private static final Pattern _sizeAssertionPattern = Pattern.compile(
		"(?<=\n)(\t+)Assert\\.assert\\w+\\([^;]*?\\b(\\w+)\\." +
			"(?:length|size)\\(\\)[^;]*?\\);\n");
	private static final Pattern _variableDeclarationPattern = Pattern.compile(
		"\t+[\\w<>,?\\[\\]\\s.]+\\s(\\w+) =\\s");

}