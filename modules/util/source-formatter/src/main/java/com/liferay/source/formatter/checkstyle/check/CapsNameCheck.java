/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.source.formatter.check.util.SourceUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

/**
 * @author Alan Huang
 */
public class CapsNameCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF, TokenTypes.INTERFACE_DEF,
			TokenTypes.METHOD_DEF, TokenTypes.PARAMETER_DEF,
			TokenTypes.RESOURCE, TokenTypes.VARIABLE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.METHOD_DEF) {
			if (AnnotationUtil.containsAnnotation(detailAST, "Override")) {
				return;
			}

			String absolutePath = getAbsolutePath();

			if (absolutePath.contains("/taglib/") &&
				absolutePath.endsWith("Tag.java")) {

				return;
			}
		}

		String name = getName(detailAST);

		String allCapsName = SourceUtil.getAllCapsName(name);

		if (allCapsName.equals(name)) {
			return;
		}

		log(
			detailAST, _MSG_RENAME, getTokenTypeName(detailAST), name,
			allCapsName);
	}

	private static final String _MSG_RENAME = "rename";

}