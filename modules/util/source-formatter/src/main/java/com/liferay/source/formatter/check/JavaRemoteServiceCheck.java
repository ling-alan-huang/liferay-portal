/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;

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
		String fileContent) throws Exception {

		if (!javaTerm.isJavaMethod()) {
			return javaTerm.getContent();
		}

		String className = JavaSourceUtil.getClassName(fileName);

		if (!className.endsWith("ServiceImpl") ||
				className.endsWith("LocalServiceImpl")) {
			return javaTerm.getContent();
		}

		JavaMethod javaMethod = (JavaMethod)javaTerm;

		javaMethod.getContent();

		Pattern methodPattern = Pattern.compile(
			javaMethod.getName() + "\\s*\\(([^)]*)\\)",
			Pattern.MULTILINE);

		Matcher matcher = methodPattern.matcher(
			javaMethod.getContent());

		if (matcher.find()) {
			String parameterString = matcher.group(1);

			String[] parameters =
				parameterString.split(",");

			for (String parameter : parameters) {
				parameter = parameter.trim();

				if (parameter.equals("long userId")) {
					addMessage(
						fileName,
						StringBundler.concat(
							"Please don't expose 'long userId' in remote ", 
							"service, use 'getUserId()' instead."),
						javaTerm.getLineNumber());
				}
			}
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_METHOD};
	}

}
