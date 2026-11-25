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
public class SizeAssertionOrderCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (!absolutePath.endsWith("Test.java")) {
			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST.getType() != TokenTypes.EXPR) ||
			!Objects.equals(getClassOrVariableName(detailAST), "Assert")) {

			return;
		}

		String variableName = _getSizeVariableName(detailAST);

		if (variableName == null) {
			return;
		}

		DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

		if ((nextSiblingDetailAST != null) &&
			(nextSiblingDetailAST.getType() == TokenTypes.VARIABLE_DEF) &&
			_hasGetterCall(nextSiblingDetailAST, variableName) &&
			!_isModified(nextSiblingDetailAST, variableName)) {

			log(detailAST, _MSG_MOVE_SIZE_ASSERTION, variableName);
		}
	}

	private String _getSizeVariableName(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			String methodName = getMethodName(methodCallDetailAST);

			if (methodName.equals("length") || methodName.equals("size")) {
				return getClassOrVariableName(methodCallDetailAST);
			}
		}

		return null;
	}

	private boolean _hasGetterCall(DetailAST detailAST, String variableName) {
		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			String methodName = getMethodName(methodCallDetailAST);

			if (methodName.startsWith("get") &&
				variableName.equals(
					getClassOrVariableName(methodCallDetailAST))) {

				return true;
			}
		}

		return false;
	}

	private boolean _isAssertion(DetailAST detailAST) {
		if (detailAST.getType() != TokenTypes.EXPR) {
			return false;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if ((firstChildDetailAST.getType() == TokenTypes.METHOD_CALL) &&
			Objects.equals(
				getClassOrVariableName(firstChildDetailAST), "Assert")) {

			return true;
		}

		return false;
	}

	private boolean _isModified(DetailAST detailAST, String variableName) {
		DetailAST nextSiblingDetailAST = detailAST.getNextSibling();

		while (nextSiblingDetailAST != null) {
			if (_isAssertion(nextSiblingDetailAST)) {
				nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

				continue;
			}

			List<DetailAST> identDetailASTs = getAllChildTokens(
				nextSiblingDetailAST, true, TokenTypes.IDENT);

			for (DetailAST identDetailAST : identDetailASTs) {
				if (variableName.equals(identDetailAST.getText()) &&
					!_isReadOnlyCall(identDetailAST)) {

					return true;
				}
			}

			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();
		}

		return false;
	}

	private boolean _isReadOnlyCall(DetailAST identDetailAST) {
		DetailAST parentDetailAST = identDetailAST.getParent();

		if ((parentDetailAST.getType() != TokenTypes.DOT) ||
			!equals(parentDetailAST.getFirstChild(), identDetailAST)) {

			return false;
		}

		DetailAST methodNameDetailAST = parentDetailAST.getLastChild();

		String methodName = methodNameDetailAST.getText();

		if (methodName.equals("contains") || methodName.startsWith("get") ||
			methodName.startsWith("is") || methodName.equals("length") ||
			methodName.equals("size") || methodName.equals("toString")) {

			return true;
		}

		return false;
	}

	private static final String _MSG_MOVE_SIZE_ASSERTION =
		"size.assertion.move";

}