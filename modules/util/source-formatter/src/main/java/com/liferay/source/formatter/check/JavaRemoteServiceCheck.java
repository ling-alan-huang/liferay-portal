/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Seiphon Wang
 */
public class JavaRemoteServiceCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		if (!javaTerm.isJavaMethod()) {
			return javaTerm.getContent();
		}

		String className = JavaSourceUtil.getClassName(fileName);

		if (!className.endsWith("ServiceImpl") ||
			className.endsWith("LocalServiceImpl")) {

			return javaTerm.getContent();
		}

		_checkExposedUserId(fileName, javaTerm);

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_METHOD};
	}

	private void _checkExposedUserId(String fileName, JavaTerm javaTerm) {
		JavaMethod javaMethod = (JavaMethod)javaTerm;

		javaMethod.getContent();

		Pattern methodPattern = Pattern.compile(
			javaMethod.getName() + "\\s*\\(([^)]*)\\)", Pattern.MULTILINE);

		Matcher matcher = methodPattern.matcher(javaMethod.getContent());

		if (matcher.find()) {
			String parameterString = matcher.group(1);

			String[] parameters = parameterString.split(",");

			for (String parameter : parameters) {
				parameter = parameter.trim();

				if (parameter.equals("long userId")) {
					addMessage(
						fileName,
						"Please do not expose 'long userId' in remote " +
							"service, use 'getUserId()' instead.",
						javaTerm.getLineNumber());
				}
			}
		}
	}

}