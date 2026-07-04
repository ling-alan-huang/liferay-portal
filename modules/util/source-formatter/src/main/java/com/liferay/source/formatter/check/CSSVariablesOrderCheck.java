/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class CSSVariablesOrderCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!absolutePath.endsWith(".scss")) {
			return content;
		}

		Matcher matcher1 = _sassVariablePattern.matcher(content);

		while (matcher1.find()) {
			String variableName = matcher1.group(1);

			Tuple variableDeclarationTuple = _getVariableDeclarationTuple(
				content, matcher1);

			if (variableDeclarationTuple == null) {
				return content;
			}

			_checkPropertiesOrder(
				fileName, absolutePath, variableName,
				(String)variableDeclarationTuple.getObject(0),
				getLineNumber(content, matcher1.start()));

			int endIndex = (int)variableDeclarationTuple.getObject(1);

			if (endIndex == (content.length() - 1)) {
				return content;
			}

			String followingCode = content.substring(endIndex);

			if (Validator.isBlank(followingCode)) {
				return content;
			}

			char c = followingCode.charAt(0);

			if (c != CharPool.DOLLAR) {
				continue;
			}

			int index = followingCode.indexOf(StringPool.NEW_LINE);

			if (index == -1) {
				continue;
			}

			String firstLine = followingCode.substring(0, index);

			Matcher matcher2 = _sassVariablePattern.matcher(firstLine);

			if (!matcher2.find()) {
				continue;
			}

			Tuple nextVariableDeclarationTuple = _getVariableDeclarationTuple(
				followingCode, matcher2);

			if (nextVariableDeclarationTuple == null) {
				return content;
			}

			String nextVariableDeclaration =
				(String)nextVariableDeclarationTuple.getObject(0);

			if (nextVariableDeclaration.contains("$" + variableName)) {
				continue;
			}

			String nextVariableName = matcher2.group(1);

			if (_comparator.compare(variableName, nextVariableName) <= 0) {
				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"\"", nextVariableName, "\" should come after \"",
					variableName, "\""),
				getLineNumber(content, matcher1.start()));
		}

		return content;
	}

	private void _checkPropertiesOrder(
		String fileName, String absolutePath, String variableName, String s,
		int lineNumber) {

		List<String> skipSortVariableNames = getAttributeValues(
			_SKIP_SORT_VARIABLE_NAMES_KEY, absolutePath);

		for (String skipSortVariableName : skipSortVariableNames) {
			if (variableName.contains(skipSortVariableName)) {
				return;
			}
		}

		Matcher matcher1 = _propertyKeyPattern.matcher(s);

		while (matcher1.find()) {
			String propertyKey = matcher1.group(1);

			int x = matcher1.start();

			while (true) {
				x = s.indexOf(",\n", x + 1);

				if (x == -1) {
					return;
				}

				if (_isComment(getLine(s, getLineNumber(s, x)))) {
					continue;
				}

				if (getLevel(s.substring(matcher1.start(), x + 2)) == 0) {
					break;
				}
			}

			int propertyKeyLineNumber = getLineNumber(s, matcher1.start() + 1);

			String followingCode = s.substring(x + 2);

			char c = followingCode.charAt(0);

			if (c == CharPool.NEW_LINE) {
				continue;
			}

			int index = followingCode.indexOf(StringPool.NEW_LINE);

			if (index == -1) {
				return;
			}

			String firstLine = followingCode.substring(0, index);

			Matcher matcher2 = _propertyKeyPattern.matcher(firstLine);

			if (!matcher2.find()) {
				continue;
			}

			String nextPropertyKey = matcher2.group(1);

			if (_comparator.compare(propertyKey, nextPropertyKey) <= 0) {
				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"\"", propertyKey, "\" should come after \"",
					nextPropertyKey, "\""),
				lineNumber + propertyKeyLineNumber - 1);
		}
	}

	private Tuple _getVariableDeclarationTuple(String s, Matcher matcher) {
		int x = matcher.end();

		while (true) {
			x = s.indexOf(StringPool.SEMICOLON, x + 1);

			if (x == -1) {
				return null;
			}

			if (x == (s.length() - 1)) {
				return new Tuple(s.substring(matcher.start()), s.length() - 1);
			}

			if (_isComment(getLine(s, getLineNumber(s, x)))) {
				continue;
			}

			String variableDeclaration = s.substring(matcher.start(), x + 1);

			if (getLevel(variableDeclaration) != 0) {
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

	private boolean _isComment(String line) {
		String trimmedLine = line.trim();

		if (trimmedLine.endsWith("*/") || trimmedLine.startsWith("/*") ||
			trimmedLine.startsWith(StringPool.DOUBLE_SLASH) ||
			trimmedLine.startsWith(StringPool.STAR)) {

			return true;
		}

		return false;
	}

	private static final String _SKIP_SORT_VARIABLE_NAMES_KEY =
		"skipSortVariableNames";

	private static final NaturalOrderStringComparator _comparator =
		new NaturalOrderStringComparator();
	private static final Pattern _propertyKeyPattern = Pattern.compile(
		"^\\s+(\\w[\\w-]*):.*[^(]$", Pattern.MULTILINE);
	private static final Pattern _sassVariablePattern = Pattern.compile(
		"^\\$([\\w-]+):", Pattern.MULTILINE);

}