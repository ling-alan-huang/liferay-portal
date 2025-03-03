/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser;

import com.liferay.petra.string.StringBundler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alan Huang
 */
public class JavaSwitchRuleStatement extends BaseJavaTerm {

	public void addSwitchRuleJavaExpression(
		JavaExpression switchRuleJavaExpression) {

		_switchRuleJavaExpressions.add(switchRuleJavaExpression);
	}

	public void setDefault(boolean isDefault) {
		_isDefault = isDefault;
	}

	public void setLambdaActionJavaExpression(
		JavaExpression lambdaActionJavaExpression) {

		_lambdaActionJavaExpression = lambdaActionJavaExpression;
	}

	@Override
	public String toString(
		String indent, String prefix, String suffix, int maxLineLength) {

		StringBundler sb = new StringBundler();

		if (_isDefault) {
			//			sb.append("default ");
			appendNewLine(
				sb, _switchRuleJavaExpressions, indent, "default ",
				" -> " + suffix, maxLineLength);
		}
		else {
			//			sb.append("case ");

			appendNewLine(
				sb, _switchRuleJavaExpressions, indent, "case ",
				" -> " + suffix, maxLineLength);
		}

		if (_lambdaActionJavaExpression != null) {
			append(sb, _lambdaActionJavaExpression, indent, maxLineLength);
		}
		//		sb.append(" -> ");

		return sb.toString();
	}

	private boolean _isDefault;
	private JavaExpression _lambdaActionJavaExpression;
	private final List<JavaExpression> _switchRuleJavaExpressions =
		new ArrayList<>();

}