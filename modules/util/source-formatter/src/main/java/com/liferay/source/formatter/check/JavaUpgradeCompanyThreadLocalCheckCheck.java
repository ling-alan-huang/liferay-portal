/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.Objects;

/**
 * @author Alan Huang
 */
public class JavaUpgradeCompanyThreadLocalCheckCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		if (!absolutePath.contains("/upgrade/") ||
			absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/") ||
			!isUpgradeProcess(absolutePath, fileContent)) {

			return javaTerm.getContent();
		}

		JavaClass javaClass = (JavaClass)javaTerm;

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			if (!Objects.equals(
					javaMethod.getAccessModifier(),
					JavaTerm.ACCESS_MODIFIER_PRIVATE) ||
				javaMethod.hasAnnotation("Override")) {

				continue;
			}

			_checkCompanyThreadLocalCall(fileName, javaMethod, "setCompanyId");
			_checkCompanyThreadLocalCall(
				fileName, javaMethod, "setCompanyIdWithSafeCloseable");
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private void _checkCompanyThreadLocalCall(
		String fileName, JavaMethod javaMethod, String methodName) {

		String javaMethodContent = javaMethod.getContent();

		int x = -1;

		while (true) {
			x = javaMethodContent.indexOf(
				"CompanyThreadLocal." + methodName + "(", x + 1);

			if (x == -1) {
				return;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"CompanyThreadLocal.", methodName,
					"can only be called by \"upgrade()\" and \"doUpgrade()\" ",
					"in Upgrade classes"),
				javaMethod.getLineNumber(x));
		}
	}

}