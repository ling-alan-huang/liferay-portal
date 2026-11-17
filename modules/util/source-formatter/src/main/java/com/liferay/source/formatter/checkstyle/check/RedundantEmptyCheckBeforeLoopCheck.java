/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Objects;

/**
 * @author Alan Huang
 */
public class RedundantEmptyCheckBeforeLoopCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_IF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (detailAST.findFirstToken(TokenTypes.LITERAL_ELSE) != null) {
			return;
		}

		DetailAST slistDetailAST = detailAST.findFirstToken(TokenTypes.SLIST);

		if ((slistDetailAST == null) || (slistDetailAST.getChildCount() != 2)) {
			return;
		}

		DetailAST literalForDetailAST = slistDetailAST.getFirstChild();

		if (literalForDetailAST.getType() != TokenTypes.LITERAL_FOR) {
			return;
		}

		DetailAST forEachClauseDetailAST = literalForDetailAST.findFirstToken(
			TokenTypes.FOR_EACH_CLAUSE);

		if (forEachClauseDetailAST == null) {
			return;
		}

		DetailAST exprDetailAST = forEachClauseDetailAST.getLastChild();

		DetailAST identDetailAST = exprDetailAST.getFirstChild();

		if (identDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		String variableName = identDetailAST.getText();

		exprDetailAST = detailAST.findFirstToken(TokenTypes.EXPR);

		DetailAST conditionDetailAST = exprDetailAST.getFirstChild();

		if (_isLengthCheck(conditionDetailAST, variableName) ||
			_isIsEmptyCheck(conditionDetailAST, variableName) ||
			(_isUtilIsNotEmptyCheck(conditionDetailAST, variableName) &&
			 _isNonnullVariable(detailAST, variableName))) {

			log(detailAST, _MSG_REDUNDANT_EMPTY_CHECK, variableName);
		}
	}

	private String _getFieldAccessName(DetailAST dotDetailAST) {
		DetailAST firstChildDetailAST = dotDetailAST.getFirstChild();
		DetailAST lastChildDetailAST = dotDetailAST.getLastChild();

		if ((firstChildDetailAST.getType() != TokenTypes.IDENT) ||
			(lastChildDetailAST.getType() != TokenTypes.IDENT)) {

			return null;
		}

		return firstChildDetailAST.getText() + "." +
			lastChildDetailAST.getText();
	}

	private String _getMethodCallName(DetailAST methodCallDetailAST) {
		DetailAST dotDetailAST = methodCallDetailAST.getFirstChild();

		if (dotDetailAST.getType() != TokenTypes.DOT) {
			return null;
		}

		return _getFieldAccessName(dotDetailAST);
	}

	private boolean _isIsEmptyCheck(DetailAST detailAST, String variableName) {
		if (detailAST.getType() != TokenTypes.LNOT) {
			return false;
		}

		DetailAST methodCallDetailAST = detailAST.getFirstChild();

		if ((methodCallDetailAST.getType() != TokenTypes.METHOD_CALL) ||
			!_isMethodCall(methodCallDetailAST, variableName + ".isEmpty", 0)) {

			return false;
		}

		return true;
	}

	private boolean _isLengthCheck(DetailAST detailAST, String variableName) {
		if (detailAST.getType() != TokenTypes.GT) {
			return false;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();
		DetailAST lastChildDetailAST = detailAST.getLastChild();

		if ((firstChildDetailAST.getType() != TokenTypes.DOT) ||
			(lastChildDetailAST.getType() != TokenTypes.NUM_INT) ||
			!Objects.equals(lastChildDetailAST.getText(), "0")) {

			return false;
		}

		String fieldAccessName = _getFieldAccessName(firstChildDetailAST);

		if ((fieldAccessName == null) ||
			!fieldAccessName.equals(variableName + ".length")) {

			return false;
		}

		return true;
	}

	private boolean _isMethodCall(
		DetailAST methodCallDetailAST, String methodCallName,
		int parameterCount) {

		if (!methodCallName.equals(_getMethodCallName(methodCallDetailAST))) {
			return false;
		}

		List<DetailAST> parameterExprDetailASTs = getParameterExprDetailASTs(
			methodCallDetailAST);

		if (parameterExprDetailASTs.size() != parameterCount) {
			return false;
		}

		return true;
	}

	private boolean _isNonnullMethodCall(DetailAST methodCallDetailAST) {
		String methodCallName = _getMethodCallName(methodCallDetailAST);

		if (methodCallName == null) {
			return false;
		}

		if (methodCallName.equals("StringUtil.split")) {
			return true;
		}

		if (methodCallName.matches("ParamUtil\\.get\\w+Values")) {
			List<DetailAST> parameterExprDetailASTs =
				getParameterExprDetailASTs(methodCallDetailAST);

			if (parameterExprDetailASTs.size() == 2) {
				return true;
			}
		}

		return false;
	}

	private boolean _isNonnullVariable(
		DetailAST detailAST, String variableName) {

		DetailAST variableDefinitionDetailAST = getVariableDefinitionDetailAST(
			detailAST, variableName, false);

		if ((variableDefinitionDetailAST == null) ||
			(variableDefinitionDetailAST.getType() !=
				TokenTypes.VARIABLE_DEF)) {

			return false;
		}

		DetailAST parentDetailAST = variableDefinitionDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.SLIST) {
			return false;
		}

		DetailAST assignDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			return false;
		}

		DetailAST valueDetailAST = assignDetailAST.getFirstChild();

		if (valueDetailAST.getType() == TokenTypes.EXPR) {
			valueDetailAST = valueDetailAST.getFirstChild();
		}

		if ((valueDetailAST.getType() != TokenTypes.LITERAL_NEW) &&
			((valueDetailAST.getType() != TokenTypes.METHOD_CALL) ||
			 !_isNonnullMethodCall(valueDetailAST))) {

			return false;
		}

		for (DetailAST variableCallerDetailAST :
				getVariableCallerDetailASTs(variableDefinitionDetailAST)) {

			parentDetailAST = variableCallerDetailAST.getParent();

			if ((parentDetailAST.getType() == TokenTypes.ASSIGN) &&
				equals(
					parentDetailAST.getFirstChild(), variableCallerDetailAST)) {

				return false;
			}
		}

		return true;
	}

	private boolean _isUtilIsNotEmptyCheck(
		DetailAST detailAST, String variableName) {

		if (detailAST.getType() != TokenTypes.METHOD_CALL) {
			return false;
		}

		String methodCallName = _getMethodCallName(detailAST);

		if ((methodCallName == null) ||
			!methodCallName.matches("(Array|List|Set)Util\\.isNotEmpty")) {

			return false;
		}

		DetailAST firstParameterExprDetailAST = getFirstParameterExprDetailAST(
			detailAST);

		if ((firstParameterExprDetailAST == null) ||
			(firstParameterExprDetailAST.getChildCount() != 1)) {

			return false;
		}

		DetailAST identDetailAST = firstParameterExprDetailAST.getFirstChild();

		if ((identDetailAST.getType() != TokenTypes.IDENT) ||
			!variableName.equals(identDetailAST.getText())) {

			return false;
		}

		return true;
	}

	private static final String _MSG_REDUNDANT_EMPTY_CHECK =
		"empty.check.redundant";

}