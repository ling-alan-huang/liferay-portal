/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;

/**
 * @author Alan Huang
 */
public class JavaResourceTestInjectionCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		JavaClass javaClass = (JavaClass)javaTerm;

		String className = javaClass.getName();

		if (!className.endsWith("ResourceTest.java") ||
			(javaClass.getParentJavaClass() != null)) {

			return javaTerm.getContent();
		}

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaVariable()) {
				continue;
			}

			JavaVariable javaVariable = (JavaVariable)childJavaTerm;

			if (!javaVariable.hasAnnotation("Inject")) {
				return javaTerm.getContent();
			}

			String variableName = javaVariable.getName();

			String variableTypeName = getVariableTypeName(
				javaTerm.getContent(), javaTerm, fileContent, fileName,
				variableName, false, true);

			if ((variableTypeName == null) ||
				!variableTypeName.contains(".resource.") ||
				variableTypeName.contains(".client.resource.")) {

				return javaTerm.getContent();
			}

			addMessage(
				fileName,
				"Do not inject another resource that is not a \"client\"",
				childJavaTerm.getLineNumber());
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

}