/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class UnnecessaryEmptyLineCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTList = getMethodCalls(
			detailAST, "append");

		for (DetailAST methodCallDetailAST : methodCallDetailASTList) {
			DetailAST parentDetailAST = methodCallDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.EXPR) {
				continue;
			}

			String variableName = getVariableName(methodCallDetailAST);

			DetailAST firstParameterExprDetailAST =
				getFirstParameterExprDetailAST(methodCallDetailAST);

			if ((firstParameterExprDetailAST == null) ||
				!_isLiteralStringOrStringPool(firstParameterExprDetailAST)) {

				continue;
			}

			DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

			if ((nextSiblingDetailAST == null) ||
				(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

				continue;
			}

			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

			if ((nextSiblingDetailAST == null) ||
				(nextSiblingDetailAST.getType() != TokenTypes.EXPR)) {

				continue;
			}

			DetailAST firstChildDetailAST =
				nextSiblingDetailAST.getFirstChild();

			if ((firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) ||
				((getEndLineNumber(methodCallDetailAST) + 2) !=
					getStartLineNumber(firstChildDetailAST))) {

				continue;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.DOT) {
				continue;
			}

			List<String> names = getNames(firstChildDetailAST, false);

			if ((names.size() != 2) ||
				!StringUtil.equals(names.get(0), variableName) ||
				!StringUtil.equals(names.get(1), "append")) {

				continue;
			}

			firstParameterExprDetailAST = getFirstParameterExprDetailAST(
				firstChildDetailAST.getParent());

			if ((firstParameterExprDetailAST == null) ||
				!_isLiteralStringOrStringPool(firstParameterExprDetailAST)) {

				continue;
			}

			log(
				methodCallDetailAST, _MSG_UNNECESSARY_EMPTY_LINE,
				getStartLineNumber(firstChildDetailAST) - 1);
		}
	}

	private boolean _isLiteralStringOrStringPool(DetailAST detailAST) {
		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if ((firstChildDetailAST.getType() != TokenTypes.DOT) &&
			(firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL)) {

			return false;
		}

		if (firstChildDetailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			String text = fullIdent.getText();

			if (!text.startsWith("StringPool.")) {
				return false;
			}
		}

		return true;
	}

	private static final String _MSG_UNNECESSARY_EMPTY_LINE =
		"empty.line.unnecessary";

}