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
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaDTOConverterCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		JavaClass javaClass = javaTerm.getParentJavaClass();

		String className = javaClass.getName();

		if (!className.endsWith("DTOConverter")) {
			return javaTerm.getContent();
		}

		JavaMethod javaMethod = (JavaMethod)javaTerm;

		JavaSignature javaSignature = javaMethod.getSignature();

		String methodName = javaMethod.getName();

		if (methodName.equals("toDTO") &&
			javaMethod.hasAnnotation("Override")) {

			return _sortAssignCalls(
				javaTerm.getContent(), javaSignature.getReturnType());
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_METHOD};
	}

	private String _sortAssignCalls(String content) {
		int x = 0;
		int y = -1;

		List<String> setCalls = new ArrayList<>();
		List<String> variableAssignments = new ArrayList<>();

		while (true) {
			y = content.indexOf(";\n", y + 1);

			if (y == -1) {
				break;
			}

			String statement = content.substring(x, y + 2);

			int level = getLevel(
				statement,
				new String[] {
					StringPool.OPEN_BRACKET, StringPool.OPEN_CURLY_BRACE,
					StringPool.OPEN_PARENTHESIS
				},
				new String[] {
					StringPool.CLOSE_BRACKET, StringPool.CLOSE_CURLY_BRACE,
					StringPool.CLOSE_PARENTHESIS
				});

			if (level != 0) {
				continue;
			}

			String trimmedStatement = StringUtil.trimLeading(statement, '\t');

			trimmedStatement = StringUtil.trimTrailing(trimmedStatement);

			if (trimmedStatement.matches("\t+set[A-Z]\\w*\\([\\s\\S]+")) {
				setCalls.add(trimmedStatement);
			}
			else if (trimmedStatement.matches("\t+\\w+ =[\\s\\S]+")) {
				variableAssignments.add(trimmedStatement);
			}
			else {
				return content;
			}

			x = y + 2;

			y = x;
		}

		Collections.sort(
			setCalls,
			new NaturalOrderStringComparator() {

				@Override
				public int compare(String setCall1, String setCall2) {
					String setCallName1 = StringUtil.trimLeading(
						setCall1.substring(0, setCall1.indexOf("(")));
					String setCallName2 = StringUtil.trimLeading(
						setCall2.substring(0, setCall2.indexOf("(")));

					return super.compare(setCallName1, setCallName2);
				}

			});

		Collections.sort(
			variableAssignments,
			new NaturalOrderStringComparator() {

				@Override
				public int compare(
					String variableAssignment1, String variableAssignment2) {

					String variableName1 = StringUtil.trimLeading(
						variableAssignment1.substring(
							0, variableAssignment1.indexOf(" ")));
					String variableName2 = StringUtil.trimLeading(
						variableAssignment2.substring(
							0, variableAssignment2.indexOf(" ")));

					return super.compare(variableName1, variableName2);
				}

			});

		StringBundler sb = new StringBundler(
			(setCalls.size() * 2) + (variableAssignments.size() * 2) + 1);

		for (String variableAssignment : variableAssignments) {
			sb.append(variableAssignment);
			sb.append(CharPool.NEW_LINE);
		}

		if (!variableAssignments.isEmpty() && !setCalls.isEmpty()) {
			sb.append(CharPool.NEW_LINE);
		}

		for (String setCall : setCalls) {
			sb.append(setCall);
			sb.append(CharPool.NEW_LINE);
		}

		return sb.toString();
	}

	private String _sortAssignCalls(String content, String returnType) {
		Pattern newDTOPattern = Pattern.compile(
			" new " + returnType +
				"\\(\\) \\{\\n\t+\\{\\n([\\s\\S]+?;\\n)\t+\\}\\n\t+\\};");

		Matcher matcher = newDTOPattern.matcher(content);

		while (matcher.find()) {
			String statementsBlock = matcher.group(1);

			int level = getLevel(
				statementsBlock,
				new String[] {
					StringPool.OPEN_CURLY_BRACE, StringPool.OPEN_PARENTHESIS
				},
				new String[] {
					StringPool.CLOSE_CURLY_BRACE, StringPool.CLOSE_PARENTHESIS
				});

			if (level != 0) {
				continue;
			}

			String replacement = _sortAssignCalls(statementsBlock);

			if (!statementsBlock.equals(replacement)) {
				return StringUtil.replaceFirst(
					content, statementsBlock, replacement, matcher.start());
			}
		}

		return content;
	}

}