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

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaReturnStatementCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		String newContent = _simplyReturnStatement(javaTerm);

		if (!StringUtil.equals(newContent, javaTerm.getContent())) {
			return newContent;
		}

		return _formatReturnStatements(javaTerm);
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CONSTRUCTOR, JAVA_METHOD};
	}

	private String _formatReturnStatement(
		String javaTermContent, String returnStatement, String tabs,
		String ifCondition, String trueValue, String falseValue) {

		StringBundler sb = new StringBundler(15);

		sb.append("\n");
		sb.append(tabs);
		sb.append("if (");
		sb.append(ifCondition);
		sb.append(") {\n\n");
		sb.append(tabs);
		sb.append("\treturn ");
		sb.append(trueValue);
		sb.append(";\n");
		sb.append(tabs);
		sb.append("}\n\n");
		sb.append(tabs);
		sb.append("return ");
		sb.append(falseValue);
		sb.append(";\n");

		return StringUtil.replace(
			javaTermContent, returnStatement, sb.toString());
	}

	private String _formatReturnStatements(JavaTerm javaTerm) {
		String javaTermContent = javaTerm.getContent();

		JavaSignature signature = javaTerm.getSignature();

		String returnType = signature.getReturnType();

		Matcher matcher1 = _returnPattern.matcher(javaTermContent);

		while (matcher1.find()) {
			String returnStatement = matcher1.group();

			if (returnStatement.contains("\t//") ||
				returnStatement.contains(" {\n")) {

				continue;
			}

			String[] ternaryOperatorParts = getTernaryOperatorParts(
				matcher1.group(2));

			if (ternaryOperatorParts != null) {
				String falseValue = ternaryOperatorParts[2];
				String ifCondition = ternaryOperatorParts[0];
				String trueValue = ternaryOperatorParts[1];

				return _formatReturnStatement(
					javaTermContent, returnStatement, matcher1.group(1),
					ifCondition, trueValue, falseValue);
			}

			if ((returnType == null) || !returnType.equals("boolean")) {
				continue;
			}

			String strippedReturnStatement = stripQuotes(returnStatement);

			if (strippedReturnStatement.contains("|") ||
				strippedReturnStatement.contains("&") ||
				strippedReturnStatement.contains("^")) {

				return _formatReturnStatement(
					javaTermContent, returnStatement, matcher1.group(1),
					matcher1.group(2), "true", "false");
			}

			Matcher matcher2 = _relationalOperatorPattern.matcher(
				returnStatement);

			if (matcher2.find() &&
				!ToolsUtil.isInsideQuotes(returnStatement, matcher2.start(1))) {

				return _formatReturnStatement(
					javaTermContent, returnStatement, matcher1.group(1),
					matcher1.group(2), "true", "false");
			}
		}

		return javaTermContent;
	}

	private String _simplyReturnStatement(JavaTerm javaTerm) {
		String javaTermContent = javaTerm.getContent();

		JavaSignature signature = javaTerm.getSignature();

		String returnType = signature.getReturnType();

		if (Validator.isNull(returnType) ||
			StringUtil.equals(returnType, "void")) {

			return javaTermContent;
		}

		Matcher matcher = _elsePattern.matcher(javaTermContent);

		outLoop:
		while (matcher.find()) {
			String elseStatement = null;

			int x = javaTermContent.indexOf(
				StringPool.CLOSE_CURLY_BRACE, matcher.start());

			while (true) {
				if (x == -1) {
					continue outLoop;
				}

				int statementEndPos = x + 1;

				elseStatement = javaTermContent.substring(
					matcher.start(), statementEndPos);

				int level = getLevel(
					elseStatement, StringPool.OPEN_CURLY_BRACE,
					StringPool.CLOSE_CURLY_BRACE);

				if (level != 0) {
					x = javaTermContent.indexOf(
						StringPool.CLOSE_CURLY_BRACE, statementEndPos);

					continue;
				}

				int closeCurlyBraceCount = 0;

				while (statementEndPos < javaTermContent.length()) {
					char nextChar = javaTermContent.charAt(statementEndPos);

					if ((nextChar != CharPool.CLOSE_CURLY_BRACE) &&
						(nextChar != CharPool.NEW_LINE) &&
						(nextChar != CharPool.TAB)) {

						continue outLoop;
					}

					statementEndPos++;

					if (nextChar == CharPool.CLOSE_CURLY_BRACE) {
						closeCurlyBraceCount++;
					}

					if (closeCurlyBraceCount > 1) {
						continue outLoop;
					}
				}

				break;
			}

			String replaceStatement = javaTermContent.substring(
				matcher.end(), x);

			replaceStatement = StringUtil.trimTrailing(replaceStatement);

			String[] lines = replaceStatement.split(StringPool.NEW_LINE);

			StringBundler sb = new StringBundler();

			sb.append(StringPool.NEW_LINE);

			for (String line : lines) {
				if (line.startsWith(StringPool.TAB)) {
					line = line.substring(1);
				}

				sb.append(StringPool.NEW_LINE);
				sb.append(line);
			}

			return StringUtil.replaceFirst(
				javaTermContent,
				javaTermContent.substring(matcher.start(), x + 1),
				sb.toString(), matcher.start());
		}

		return javaTermContent;
	}

	private static final Pattern _elsePattern = Pattern.compile(
		"\n(\t+)else \\{\n");
	private static final Pattern _relationalOperatorPattern = Pattern.compile(
		".* (==|!=|<|>|>=|<=)[ \n].*");
	private static final Pattern _returnPattern = Pattern.compile(
		"\n(\t+)return (.*?);\n", Pattern.DOTALL);

}