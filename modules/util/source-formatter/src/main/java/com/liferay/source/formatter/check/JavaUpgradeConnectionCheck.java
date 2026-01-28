/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaTerm;

import java.io.IOException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Shin
 */
public class JavaUpgradeConnectionCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws IOException {

		if (!absolutePath.contains("/upgrade/") ||
			absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/") ||
			!isUpgradeProcess(absolutePath, fileContent)) {

			return javaTerm.getContent();
		}


		
		_checkDataAccessGetConnectionCall(fileName, javaTerm, fileContent);
		
		return _formatReusableConnection();
	}

	private String _addReusableConnection(String fileName, JavaTerm javaTerm) {
		String content = javaTerm.getContent();

		Matcher matcher = _runSQLPattern.matcher(content);

		while (matcher.find()) {
			String matched = matcher.group(1);

			if (matched != null) {
				String variableName = matched.substring(
						0, matched.length() - 1);

				if (!StringUtil.equals(
						getVariableTypeName(
								content, null, content, fileName, variableName),
						"DB")) {

					continue;
				}
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
					content.substring(matcher.start()));

			if (parameterList.size() != 1) {
				continue;
			}

			String newContent = StringUtil.insert(
					content, "connection, ", matcher.end());

			if (!content.equals(newContent)) {
				return newContent;
			}
		}
	}
	private void _checkDataAccessGetConnectionCall(String fileName, JavaTerm javaTerm,String fileContent) {
		String methodName = javaTerm.getName();

		if (methodName.equals("upgrade") &&
				javaTerm.hasAnnotation("Override")) {

			return;
		}

		String content = javaTerm.getContent();

		int x = content.indexOf("DataAccess.getConnection(");

		if (x == -1) {
			return;
		}

		addMessage(
				fileName,
				"Use existing connection field instead of calling DataAccess." +
						"getConnection",
				getLineNumber(fileContent, x));


	}
	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_METHOD};
	}

	private static final Pattern _runSQLPattern = Pattern.compile(
		"\\b(\\w+\\.)?runSQL\\(");

}