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

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class StringCastCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTList = getMethodCalls(
			detailAST, "toString");

		for (DetailAST methodCallDetailAST : methodCallDetailASTList) {
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.DOT);

			String variableTypeName = getVariableTypeName(
				methodCallDetailAST, getName(dotDetailAST), false);

			if (variableTypeName.equals("String")) {
				log(methodCallDetailAST, _MSG_UNNEEDED_STRING_CAST);
			}
		}

		_checkUsePetraStringBundler(detailAST);
	}

	private void _checkUsePetraStringBundler(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "StringBundler", "concat");

		if (ListUtil.isEmpty(methodCallDetailASTs)) {
			return;
		}

		List<String> importNames = getImportNames(detailAST);

		if (!importNames.contains("com.liferay.petra.string.StringBundler")) {
			return;
		}

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST eListDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.ELIST);

			DetailAST childDetailAST = eListDetailAST.getFirstChild();

			while (childDetailAST != null) {
				if ((childDetailAST.getType() == TokenTypes.COMMA) ||
					(childDetailAST.getType() != TokenTypes.EXPR)) {

					childDetailAST = childDetailAST.getNextSibling();

					continue;
				}

				DetailAST grandChildDetailAST = childDetailAST.getFirstChild();

				if (grandChildDetailAST.getType() != TokenTypes.METHOD_CALL) {
					childDetailAST = childDetailAST.getNextSibling();

					continue;
				}

				DetailAST dotDetailAST = grandChildDetailAST.getFirstChild();

				if (dotDetailAST.getType() != TokenTypes.DOT) {
					childDetailAST = childDetailAST.getNextSibling();

					continue;
				}

				FullIdent fullIdent = FullIdent.createFullIdent(dotDetailAST);

				String fullIdentText = fullIdent.getText();

				DetailAST childEListDetailAST =
					grandChildDetailAST.findFirstToken(TokenTypes.ELIST);

				if ((fullIdentText.endsWith("toString") &&
					 (childEListDetailAST.getChildCount() == 0)) ||
					(StringUtil.equals(fullIdentText, "String.valueOf") &&
					 (childEListDetailAST.getChildCount() == 1))) {

					log(grandChildDetailAST, _USE_PETRA_STRING_STRINGBUNDLER);
				}

				childDetailAST = childDetailAST.getNextSibling();
			}
		}
	}

	private static final String _MSG_UNNEEDED_STRING_CAST =
		"string.cast.unneeded";

	private static final String _USE_PETRA_STRING_STRINGBUNDLER =
		"use.petra.string.string.bundler";

}