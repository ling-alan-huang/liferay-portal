/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class TransformUtilCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> forEachClauseDetailASTList = getAllChildTokens(
			detailAST, true, TokenTypes.FOR_EACH_CLAUSE);

		outerLoop:
		for (DetailAST forEachClauseDetailAST : forEachClauseDetailASTList) {
			DetailAST exprDetailAST = forEachClauseDetailAST.findFirstToken(
				TokenTypes.EXPR);

			DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
				continue;
			}

			String variableTypeName = getVariableTypeName(
				firstChildDetailAST, firstChildDetailAST.getText(), false);

			if ((variableTypeName == null) ||
				!variableTypeName.equals("List")) {

				continue;
			}

			DetailAST parentDetailAST = forEachClauseDetailAST.getParent();

			DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

			if ((nextSiblingDetailAST == null) ||
				(nextSiblingDetailAST.getType() != TokenTypes.LITERAL_RETURN)) {

				continue;
			}

			firstChildDetailAST = nextSiblingDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
				continue;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
				continue;
			}

			String returnVariableName = firstChildDetailAST.getText();

			String returnVariableTypeName = getVariableTypeName(
				firstChildDetailAST, returnVariableName, false);

			if ((returnVariableTypeName == null) ||
				!returnVariableTypeName.equals("List")) {

				continue;
			}

			DetailAST returnVariableDefinitionDetailAST =
				getVariableDefinitionDetailAST(
					firstChildDetailAST, returnVariableName, false);

			if ((returnVariableDefinitionDetailAST == null) ||
				!isAssignNewArrayList(returnVariableDefinitionDetailAST)) {

				continue;
			}

			List<DetailAST> variableCallerDetailASTList =
				getVariableCallerDetailASTList(
					returnVariableDefinitionDetailAST, returnVariableName);

			for (DetailAST variableCallerDetailAST :
					variableCallerDetailASTList) {

				int lineNumber = variableCallerDetailAST.getLineNo();

				if (lineNumber < forEachClauseDetailAST.getLineNo()) {
					continue outerLoop;
				}

				break;
			}

			nextSiblingDetailAST = forEachClauseDetailAST.getNextSibling();

			if (nextSiblingDetailAST.getType() != TokenTypes.RPAREN) {
				continue;
			}

			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

			if (nextSiblingDetailAST.getType() != TokenTypes.SLIST) {
				continue;
			}

			List<DetailAST> branchingStatementDetailASTList = getAllChildTokens(
				nextSiblingDetailAST, true, TokenTypes.LITERAL_RETURN);

			if (ListUtil.isNotEmpty(branchingStatementDetailASTList) ||
				!_containsOnlyAddMethodCalls(
					nextSiblingDetailAST, returnVariableName)) {

				continue;
			}

			List<DetailAST> assignDetailASTList = getAllChildTokens(
				nextSiblingDetailAST, true, TokenTypes.ASSIGN);

			for (DetailAST assignDetailAST : assignDetailASTList) {
				parentDetailAST = assignDetailAST.getParent();

				if (parentDetailAST.getType() != TokenTypes.EXPR) {
					continue;
				}

				String name = getName(assignDetailAST);

				if (name == null) {
					continue outerLoop;
				}

				DetailAST variableDefinitionDetailAST =
					getVariableDefinitionDetailAST(
						assignDetailAST, name, false);

				if ((variableDefinitionDetailAST == null) ||
					(variableDefinitionDetailAST.getLineNo() <
						forEachClauseDetailAST.getLineNo())) {

					continue outerLoop;
				}
			}

			log(forEachClauseDetailAST, _MSG_USE_TRANSFORM_UTIL_TRANSFORM);
		}
	}

	private boolean _containsOnlyAddMethodCalls(
		DetailAST detailAST, String returnVariableName) {

		List<DetailAST> methodCallDetailASTList = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTList) {
			DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

			if ((firstChildDetailAST == null) ||
				(firstChildDetailAST.getType() != TokenTypes.DOT)) {

				continue;
			}

			List<String> names = getNames(firstChildDetailAST, false);

			if ((names.size() != 2) ||
				!StringUtil.equals(returnVariableName, names.get(0))) {

				continue;
			}

			if (!StringUtil.equals(names.get(1), "add")) {
				return false;
			}
		}

		return true;
	}

	private static final String _MSG_USE_TRANSFORM_UTIL_TRANSFORM =
		"transform.util.transform.use";

}