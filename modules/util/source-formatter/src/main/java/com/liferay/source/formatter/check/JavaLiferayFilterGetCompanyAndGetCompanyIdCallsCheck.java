/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaLiferayFilterGetCompanyAndGetCompanyIdCallsCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	protected boolean isLiferayFilter(String absolutePath, String content) {
		return _isDerivedFrom(
				absolutePath, content,
				"com.liferay.portal.kernel.servlet.BaseFilter");
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		if (!fileName.endsWith("Filter.java") ||
				!isLiferayFilter(absolutePath, fileContent)) {
			return javaTerm.getContent();
		}

		_checkGetCompanyCall(fileName, fileContent, "PortalUtil.getCompany");

		_checkGetCompanyIdCall(fileName, fileContent, "PortalInstances.getCompanyId");
		_checkGetCompanyIdCall(fileName, fileContent, "PortalUtil.getCompanyId");

		JavaClass javaClass = (JavaClass)javaTerm;

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaVariable()) {
				continue;
			}

			JavaVariable javaVariable = (JavaVariable)childJavaTerm;

			String variableName = javaVariable.getName();

			String variableTypeName = getVariableTypeName(
					javaVariable.getContent(), childJavaTerm, fileContent, fileName,
					variableName, true, false);

			if (!variableTypeName.equals("Portal")) {
				continue;
			}

			_checkGetCompanyCall(fileName, fileContent, variableName + ".getCompany");
			_checkGetCompanyIdCall(fileName, fileContent, variableName + ".getCompanyId");

		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private void _checkGetCompanyCall(String fileName, String content, String methodCall) {
		int x = -1;

		while (true) {
			x = content.indexOf(methodCall + "(", x + 1);

			if (x == -1) {
				return;
			}

			if (ToolsUtil.isInsideQuotes(content, x)) {
				continue;
			}

			addMessage(
					fileName,
					"Use \"PortalInstances.getCompanyId\" and \"CompanyLocalServiceUtil.fetchCompanyById\" instead of \"" + methodCall + "\"",
					getLineNumber(content, x));
		}
	}
	private void _checkGetCompanyIdCall(String fileName, String content, String methodCall) {

		int x = -1;

		while (true) {
			x = content.indexOf(methodCall + "(", x + 1);

			if (x == -1) {
				return;
			}

			if (ToolsUtil.isInsideQuotes(content, x)) {
				continue;
			}

			addMessage(
					fileName,
					"Use \"PortalInstances.getCompanyId\" instead of \"" + methodCall + "\"",
					getLineNumber(content, x));
		}

	}
	private void _checkAnnotationForMethod(
		String fileName, JavaTerm javaTerm, String requiredMethodNameRegex,
		boolean staticRequired, String... annotations) {

		String methodName = javaTerm.getName();

		Pattern pattern = Pattern.compile(requiredMethodNameRegex);

		Matcher matcher = pattern.matcher(methodName);

		boolean hasAnnotation = false;

		for (String annotation : annotations) {
			if (javaTerm.hasAnnotation(annotation)) {
				hasAnnotation = true;

				break;
			}
		}

		if (hasAnnotation) {
			if (!matcher.find()) {
				addMessage(
					fileName, "Incorrect method name \"" + methodName + "\"",
					javaTerm.getLineNumber());
			}
			else if (javaTerm.isStatic() != staticRequired) {
				addMessage(
					fileName,
					"Incorrect method type for \"" + methodName + "\"",
					javaTerm.getLineNumber());
			}

			return;
		}

		if (!matcher.find()) {
			return;
		}

		JavaSignature signature = javaTerm.getSignature();

		List<JavaParameter> parameters = signature.getParameters();

		if (!parameters.isEmpty()) {
			return;
		}

		JavaClass javaClass = javaTerm.getParentJavaClass();

		if (javaClass.isAnonymous()) {
			return;
		}

		JavaClass parentJavaClass = javaClass.getParentJavaClass();

		if (parentJavaClass == null) {
			StringBundler sb = new StringBundler();

			for (String annotation : annotations) {
				sb.append("@");
				sb.append(annotation);
				sb.append(" or ");
			}

			sb.setIndex(sb.index() - 1);

			addMessage(
				fileName,
				StringBundler.concat(
					"Annotation ", sb, " required for \"", methodName, "\""),
				javaTerm.getLineNumber());
		}
	}

	private void _checkFeatureFlagsAnnotation(
		String fileName, JavaTerm javaTerm) {

		String javaTermContent = javaTerm.getContent();

		int x = -1;

		while (true) {
			x = javaTermContent.indexOf("PropsUtil.addProperties(", x + 1);

			if (x == -1) {
				break;
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
				JavaSourceUtil.getMethodCall(javaTerm.getContent(), x));

			if ((parameterList.size() != 1) ||
				!StringUtil.startsWith(
					parameterList.get(0),
					"UnicodePropertiesBuilder.setProperty(")) {

				continue;
			}

			parameterList = JavaSourceUtil.getParameterList(
				JavaSourceUtil.getMethodCall(parameterList.get(0), 0));

			if ((parameterList.size() == 2) &&
				StringUtil.startsWith(
					parameterList.get(0), "\"feature.flag.")) {

				addMessage(
					fileName,
					"Use annotation \"@FeatureFlags\" instead of \"PropsUtil." +
						"addProperties\" for feature flag",
					javaTerm.getLineNumber() +
						getLineNumber(javaTermContent, x) - 1);
			}
		}
	}

}