/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ListUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class TransformUtilUtilCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> forEachClauseDetailASTList = getAllChildTokens(
			detailAST, true, TokenTypes.FOR_EACH_CLAUSE);

		for (DetailAST forEachClauseDetailAST : forEachClauseDetailASTList) {
			DetailAST exprDetailAST = forEachClauseDetailAST.findFirstToken(
				TokenTypes.EXPR);

			DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
				return;
			}

			String variableTypeName = getVariableTypeName(
				firstChildDetailAST, firstChildDetailAST.getText(), false);

			if ((variableTypeName == null) ||
				!variableTypeName.equals("List")) {

				return;
			}

			DetailAST parentDetailAST = forEachClauseDetailAST.getParent();

			DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

			if ((nextSiblingDetailAST == null) ||
				(nextSiblingDetailAST.getType() != TokenTypes.LITERAL_RETURN)) {

				return;
			}

			firstChildDetailAST = nextSiblingDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
				return;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
				return;
			}

			String returnVariableName = firstChildDetailAST.getText();

			String returnVariableTypeName = getVariableTypeName(
				firstChildDetailAST, returnVariableName, false);

			if ((returnVariableTypeName == null) ||
				!returnVariableTypeName.equals("List")) {

				return;
			}

			nextSiblingDetailAST = forEachClauseDetailAST.getNextSibling();

			if (nextSiblingDetailAST.getType() != TokenTypes.RPAREN) {
				return;
			}

			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

			if (nextSiblingDetailAST.getType() != TokenTypes.SLIST) {
				return;
			}

			List<DetailAST> branchingStatementDetailASTList = getAllChildTokens(
				nextSiblingDetailAST, true, TokenTypes.LITERAL_RETURN);

			if (ListUtil.isNotEmpty(branchingStatementDetailASTList)) {
				return;
			}

			List<DetailAST> methodCallDetailASTList = getMethodCalls(
				nextSiblingDetailAST, returnVariableName, "add");

			if (ListUtil.isEmpty(methodCallDetailASTList)) {
				return;
			}

			log(forEachClauseDetailAST, _MSG_USE_TRANSFORM_UTIL_TRANSFORM);
		}
	}

	private static final String _MSG_USE_TRANSFORM_UTIL_TRANSFORM =
		"transform.util.transform.use";

}