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
import com.liferay.source.formatter.check.util.CSSSourceUtil;

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

			Tuple variableDeclarationTuple =
				CSSSourceUtil.getVariableDeclarationTuple(content, matcher1);

			if (variableDeclarationTuple == null) {
				return content;
			}

			_checkPropertiesOrder(
				fileName, (String)variableDeclarationTuple.getObject(0),
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

			String firstLine = null;

			int index = followingCode.indexOf(StringPool.NEW_LINE);

			if (index == -1) {
				firstLine = followingCode.substring(0);
			}
			else {
				firstLine = followingCode.substring(0, index);
			}

			Matcher matcher2 = _sassVariablePattern.matcher(firstLine);

			if (!matcher2.find()) {
				continue;
			}

			Tuple nextVariableDeclarationTuple =
				CSSSourceUtil.getVariableDeclarationTuple(
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
					"\"$", variableName, "\" should come after \"$",
					nextVariableName, "\""),
				getLineNumber(content, matcher1.start()));
		}

		return content;
	}

	private void _checkPropertiesOrder(
		String fileName, String s, int lineNumber) {

		Matcher matcher1 = _propertyKeyPattern.matcher(s);

		while (matcher1.find()) {
			String propertyKey = matcher1.group(1);

			int x = matcher1.start();

			while (true) {
				x = s.indexOf(",\n", x + 1);

				if (x == -1) {
					return;
				}

				if (CSSSourceUtil.isComment(getLine(s, getLineNumber(s, x)))) {
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

	private static final NaturalOrderStringComparator _comparator =
		new NaturalOrderStringComparator();
	private static final Pattern _propertyKeyPattern = Pattern.compile(
		"^\\s+(\\w[\\w-]*):.*[^(]$", Pattern.MULTILINE);
	private static final Pattern _sassVariablePattern = Pattern.compile(
		"^\\$([\\w-]+):", Pattern.MULTILINE);

}