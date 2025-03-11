/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser;

import com.liferay.petra.string.StringBundler;

/**
 * @author Hugo Huijser
 */
public class JavaReturnStatement extends BaseJavaTerm {

	public JavaExpression getReturnJavaExpression() {
		return _returnJavaExpression;
	}

	public void setReturnJavaExpression(JavaExpression returnJavaExpression) {
		_returnJavaExpression = returnJavaExpression;
	}

	public void setSwithchJavaTerm(JavaTerm swithchJavaTerm) {
		_swithchJavaTerm = swithchJavaTerm;
	}

	@Override
	public String toString(
		String indent, String prefix, String suffix, int maxLineLength) {

		if (_swithchJavaTerm != null) {
			return indent + "return " +
				_swithchJavaTerm.toString("", "", maxLineLength);
		}

		if (_returnJavaExpression == null) {
			return StringBundler.concat(indent, prefix, "return", suffix);
		}

		return _returnJavaExpression.toString(
			indent, prefix + "return ", suffix, maxLineLength);
	}

	private JavaExpression _returnJavaExpression;
	private JavaTerm _swithchJavaTerm;

}