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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

/**
 * @author Alan Huang
 */
public class JavaUnnecessaryMethodCallsCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		JavaClass rootJavaClass = (JavaClass)javaTerm;

		String classContent = rootJavaClass.getContent();

		List<JavaTerm> allJavaClassTerms = _getAllJavaClassTerms(rootJavaClass);

		allJavaClassTerms.add(0, javaTerm);;
//		List<JavaTerm> childJavaTerms = javaClass.getChildJavaTerms();

		for (JavaTerm javaClassTerm : allJavaClassTerms) {
//			List<JavaTerm> childJavaTerms = _getJavaMethodTerms(javaClassTerm);
			JavaClass javaClass = (JavaClass)javaClassTerm;
			
			List<JavaTerm> childJavaTerms = javaClass.getChildJavaTerms();

			Map<String, String> methodReturnsMap = new HashMap<>();

			for (JavaTerm childJavaTerm : childJavaTerms) {
				if (childJavaTerm.isJavaMethod()) {
					JavaMethod javaMethod = (JavaMethod)childJavaTerm;

					String name = javaMethod.getName();
					
					JavaSignature javaSignature = javaMethod.getSignature();

					List<JavaParameter> parameters = javaSignature.getParameters();

					if (!parameters.isEmpty()) {
						continue;
					}
					
					if (Objects.equals(javaSignature.getReturnType(), StringPool.BLANK)) {
						continue;
					}

					String content = javaMethod.getContent();
					
					String[] lines = content.split("\n");
					if (lines.length == 3 && lines[1].matches("\s*return \\w+;")) {
						
					}
				}
			}
			int a = 0;
		}

		return fileContent;
	}

	private List<JavaTerm> _getAllJavaClassTerms(JavaClass javaClass) {
		List<JavaTerm> childJavaTerms = new ArrayList<>();

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
//			childJavaTerms.add(childJavaTerm);

			if (childJavaTerm.isJavaClass()) {
				childJavaTerms.add(childJavaTerm);
				JavaClass childJavaClass = (JavaClass)childJavaTerm;

				childJavaTerms.addAll(_getAllJavaClassTerms(childJavaClass));
			}
		}

		return childJavaTerms;
	}

	private List<JavaTerm> _getJavaMethodTerms(JavaTerm javaTerm) {

		JavaClass javaClass = (JavaClass)javaTerm;

		List<JavaTerm> childJavaTerms = javaClass.getChildJavaTerms();

		return childJavaTerms;
	}
	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private String _fixJavaTermDivider(
		String classContent, JavaTerm previousJavaTerm, JavaTerm javaTerm) {

		String javaTermContent = javaTerm.getContent();

		String previousJavaTermContent = previousJavaTerm.getContent();

		String afterPreviousJavaTerm = StringUtil.trim(
			classContent.substring(
				classContent.indexOf("\n" + previousJavaTermContent) +
					previousJavaTermContent.length() + 1));

		if (!afterPreviousJavaTerm.startsWith(
				StringUtil.trim(javaTermContent))) {

			return classContent;
		}

		if (!javaTerm.isJavaVariable() || !previousJavaTerm.isJavaVariable()) {
			return _fixJavaTermDivider(classContent, javaTermContent, true);
		}

		if (previousJavaTerm.isStatic() ^ javaTerm.isStatic()) {
			return _fixJavaTermDivider(classContent, javaTermContent, true);
		}

		String javaTermAccessModifier = javaTerm.getAccessModifier();
		String previousJavaTermAccessModifier =
			previousJavaTerm.getAccessModifier();

		if (!previousJavaTermAccessModifier.equals(javaTermAccessModifier)) {
			return _fixJavaTermDivider(classContent, javaTermContent, true);
		}

		String javaTermName = javaTerm.getName();
		String previousJavaTermName = previousJavaTerm.getName();

		if ((StringUtil.isUpperCase(javaTermName) &&
			 !StringUtil.isLowerCase(javaTermName)) ||
			(StringUtil.isUpperCase(previousJavaTermName) &&
			 !StringUtil.isLowerCase(previousJavaTermName))) {

			return _fixJavaTermDivider(classContent, javaTermContent, true);
		}

		if (javaTermContent.matches("\\s*[/@*][\\S\\s]*") ||
			previousJavaTermContent.matches("\\s*[/@*][\\S\\s]*")) {

			return _fixJavaTermDivider(classContent, javaTermContent, true);
		}

		if (javaTermContent.contains("\n\n\t") ||
			previousJavaTermContent.contains("\n\n\t")) {

			return _fixJavaTermDivider(classContent, javaTermContent, true);
		}

		if (previousJavaTerm.isStatic() &&
			(previousJavaTermName.equals("_instance") ||
			 previousJavaTermName.equals("_log") ||
			 previousJavaTermName.equals("_logger"))) {

			return _fixJavaTermDivider(classContent, javaTermContent, true);
		}

		return _fixJavaTermDivider(classContent, javaTermContent, false);
	}

	private String _fixJavaTermDivider(
		String classContent, String javaTermContent,
		boolean requiresEmptyLine) {

		if (requiresEmptyLine) {
			if (classContent.contains("\n\n" + javaTermContent)) {
				return classContent;
			}

			return StringUtil.replace(
				classContent, "\n" + javaTermContent, "\n\n" + javaTermContent);
		}

		if (!classContent.contains("\n\n" + javaTermContent)) {
			return classContent;
		}

		return StringUtil.replace(
			classContent, "\n\n" + javaTermContent, "\n" + javaTermContent);
	}

	private static final Pattern _missingEmptyLinePattern = Pattern.compile(
		"([^{}\n]\n)(\t*\\}\n?)$");

}