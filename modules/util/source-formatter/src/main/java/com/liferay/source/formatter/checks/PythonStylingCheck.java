/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.source.formatter.checks;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Alan Huang
 */
public class PythonStylingCheck extends BaseFileCheck {

	public static String getNestedStatementIndent(String statement) {
		String[] lines = StringUtil.splitLines(statement);

		if (lines.length <= 1) {
			return StringPool.BLANK;
		}

		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];

			String indent = line.replaceFirst("^( +).+", "$1");

			if (!indent.equals(line)) {
				return indent;
			}
		}

		return StringPool.BLANK;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		return _sortPythonIdentifiers(fileName, content, StringPool.BLANK);
	}

	private String _getName(String trimmedStatement, String identifier) {
		return trimmedStatement.replaceFirst(
			identifier + "(\\w+)?(?=[:(]).*", "$1");
	}

	private List<String> _getPythonStatements(String content, String indent) {
		List<String> definitions = new ArrayList<>();

		String[] lines = content.split("\n");

		StringBundler sb = new StringBundler();

		for (String line : lines) {
			if (line.length() == 0) {
				sb.append("\n");

				continue;
			}

			if (!line.startsWith(indent)) {
				continue;
			}

			String s = line.substring(indent.length(), indent.length() + 1);

			if (!s.equals(StringPool.SPACE) && !s.equals(StringPool.TAB) &&
				(sb.length() != 0)) {

				sb.setIndex(sb.index() - 1);

				definitions.add(sb.toString());

				sb.setIndex(0);
			}

			sb.append(line);
			sb.append("\n");
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		definitions.add(sb.toString());

		return definitions;
	}

	private int _sortIdentifiers(String statement1, String statement2) {
		String trimmedStatement1 = StringUtil.trimLeading(statement1);

		if (!trimmedStatement1.startsWith("def ") &&
			!trimmedStatement1.startsWith("class ")) {

			return 0;
		}

		String trimmedStatement2 = StringUtil.trimLeading(statement2);

		if (trimmedStatement1.startsWith("def ") &&
			trimmedStatement2.startsWith("class ")) {

			return 1;
		}

		if (trimmedStatement1.startsWith("def ") &&
			trimmedStatement2.startsWith("def ")) {

			String name1 = _getName(trimmedStatement1, "def ");
			String name2 = _getName(trimmedStatement2, "def ");

			return name1.compareTo(name2);
		}

		if (trimmedStatement1.startsWith("class ") &&
			trimmedStatement2.startsWith("class ")) {

			String name1 = _getName(trimmedStatement1, "class ");
			String name2 = _getName(trimmedStatement2, "class ");

			return name1.compareTo(name2);
		}

		return 0;
	}

	private String _sortPythonIdentifiers(
		String fileName, String content, String indent) {

		List<String> statements = _getPythonStatements(content, indent);

		List<String> oldStatements = new ArrayList<>(statements);
		//
		Collections.sort(
			statements,
			new Comparator<String>() {

				@Override
				public int compare(String statement1, String statement2) {
					return _sortIdentifiers(statement1, statement2);
				}

			});

		if (!oldStatements.equals(statements)) {
			StringBundler sb = new StringBundler();

			for (String statement : statements) {
				sb.append(statement);
				sb.append("\n");
			}

			sb.setIndex(sb.index() - 1);

			String[] lines = content.split("\n");

			if (!indent.equals("")) {
				content = lines[0] + "\n" + sb.toString();
			}
			else {
				content = sb.toString();
			}
		}

		statements = _getPythonStatements(content, indent);
		//
		for (String statement : statements) {
			String nestedDefinitionIndent = getNestedStatementIndent(statement);

			if (!nestedDefinitionIndent.equals(StringPool.BLANK)) {
				content = StringUtil.replaceFirst(
					content, statement,
					_sortPythonIdentifiers(
						fileName, statement, nestedDefinitionIndent));
			}
		}

		return content;
	}

}