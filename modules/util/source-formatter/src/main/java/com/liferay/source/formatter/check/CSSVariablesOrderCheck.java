/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;

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

			String s = followingCode;

			int index = followingCode.indexOf('\n');

			if (index != -1) {
				s = followingCode.substring(0, index);
			}

			Matcher matcher2 = _sassVariablePattern.matcher(s);

			if (!matcher2.find()) {
				continue;
			}

			String nextVariableName = matcher2.group(1);

			if (_comparator.compare(variableName, nextVariableName) <= 0) {
				continue;
			}

			Tuple nextVariableDeclarationTuple = _getVariableDeclarationTuple(
				followingCode, matcher2);

			String nextVariableDeclaration =
				(String)nextVariableDeclarationTuple.getObject(0);

			if (nextVariableDeclaration.contains("$" + variableName)) {
				continue;
			}

			content = StringUtil.replaceFirst(
				content, nextVariableDeclaration,
				(String)variableDeclarationTuple.getObject(0),
				matcher1.start());
			content = StringUtil.replaceFirst(
				content, (String)variableDeclarationTuple.getObject(0),
				nextVariableDeclaration, matcher1.start());

			return content;
		}

		return content;
	}

	private Tuple _getVariableDeclarationTuple(String s, Matcher matcher) {
		int x = matcher.end();

		while (true) {
			x = s.indexOf(StringPool.SEMICOLON, x + 1);

			if ((x == -1) || (x == (s.length() - 1))) {
				return new Tuple(s.substring(matcher.start()), s.length() - 1);
			}

			String line = getLine(s, getLineNumber(s, x));

			String trimmedLine = line.trim();

			if (trimmedLine.endsWith("*/") || trimmedLine.startsWith("/*") ||
				trimmedLine.startsWith(StringPool.DOUBLE_SLASH) ||
				trimmedLine.startsWith(StringPool.STAR)) {

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

	private static final NaturalOrderStringComparator _comparator =
		new NaturalOrderStringComparator();
	private static final Pattern _sassVariablePattern = Pattern.compile(
		"^\\$([\\w-]+):", Pattern.MULTILINE);

}