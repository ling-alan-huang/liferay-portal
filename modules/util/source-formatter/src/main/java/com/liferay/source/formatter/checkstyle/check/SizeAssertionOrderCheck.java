/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

		if ((variableName == null) ||
			_isModified(parentDetailAST, variableName)) {

			return;
		}

		int lastLineNumber = _getLastLineNumber(parentDetailAST, variableName);

		if (lastLineNumber != -1) {
			log(
				detailAST, _MSG_MOVE_SIZE_ASSERTION, variableName,
				String.valueOf(getStartLineNumber(parentDetailAST)),
				String.valueOf(lastLineNumber));
		}
	}

	private String _getAssignedVariableName(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.VARIABLE_DEF) {
			return getName(detailAST);
		}

		if (detailAST.getType() != TokenTypes.EXPR) {
			return null;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.ASSIGN) {
			return null;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
			return null;
		}

		return firstChildDetailAST.getText();
	}

	private int _getLastLineNumber(DetailAST detailAST, String variableName) {
		Set<String> elementVariableNames = new HashSet<>();

		boolean afterAssertion = false;
		int lastLineNumber = -1;

		DetailAST parentDetailAST = detailAST.getParent();

		DetailAST childDetailAST = parentDetailAST.getFirstChild();

		while (childDetailAST != null) {
			if (equals(childDetailAST, detailAST)) {
				afterAssertion = true;

				childDetailAST = childDetailAST.getNextSibling();

				continue;
			}

			boolean usesElements = _usesElements(
				childDetailAST, variableName, elementVariableNames);

			if (afterAssertion && (lastLineNumber == -1) &&
				(childDetailAST.getType() != TokenTypes.SEMI) &&
				!usesElements) {

				return -1;
			}

			if (usesElements) {
				if (afterAssertion) {
					if (childDetailAST.getType() == TokenTypes.LITERAL_RETURN) {
						return -1;
					}

					lastLineNumber = getEndLineNumber(childDetailAST);
				}

				String elementVariableName = _getAssignedVariableName(
					childDetailAST);

				if (elementVariableName != null) {
					elementVariableNames.add(elementVariableName);
				}
			}

			childDetailAST = childDetailAST.getNextSibling();
		}

		return lastLineNumber;
	}

	private String _getSizeVariableName(DetailAST detailAST) {
		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		DetailAST lastArgumentDetailAST = elistDetailAST.getLastChild();

		if (lastArgumentDetailAST == null) {
			return null;
		}

		DetailAST argumentDetailAST = lastArgumentDetailAST.getFirstChild();

		if (argumentDetailAST.getType() == TokenTypes.DOT) {
			DetailAST firstChildDetailAST = argumentDetailAST.getFirstChild();
			DetailAST lastChildDetailAST = argumentDetailAST.getLastChild();

			if ((firstChildDetailAST.getType() == TokenTypes.IDENT) &&
				Objects.equals(lastChildDetailAST.getText(), "length")) {

				return firstChildDetailAST.getText();
			}
		}
		else if (argumentDetailAST.getType() == TokenTypes.METHOD_CALL) {
			String methodName = getMethodName(argumentDetailAST);

			if (methodName.equals("length") || methodName.equals("size")) {
				return getClassOrVariableName(argumentDetailAST);
			}
		}

		return null;
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

		if ((parentDetailAST.getType() == TokenTypes.INDEX_OP) &&
			equals(parentDetailAST.getFirstChild(), identDetailAST)) {

			return true;
		}

		if ((parentDetailAST.getType() != TokenTypes.DOT) ||
			!equals(parentDetailAST.getFirstChild(), identDetailAST)) {

			return false;
		}

		DetailAST methodNameDetailAST = parentDetailAST.getLastChild();

		String methodName = methodNameDetailAST.getText();

		if (methodName.equals("contains") || methodName.equals("length") ||
			methodName.equals("size") || methodName.equals("toString") ||
			methodName.startsWith("get") || methodName.startsWith("is")) {

			return true;
		}

		return false;
	}

	private boolean _usesElements(
		DetailAST detailAST, String variableName,
		Set<String> elementVariableNames) {

		List<DetailAST> identDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.IDENT);

		for (DetailAST identDetailAST : identDetailASTs) {
			if (elementVariableNames.contains(identDetailAST.getText())) {
				return true;
			}
		}

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

		List<DetailAST> indexOpDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.INDEX_OP);

		for (DetailAST indexOpDetailAST : indexOpDetailASTs) {
			DetailAST firstChildDetailAST = indexOpDetailAST.getFirstChild();

			if ((firstChildDetailAST.getType() == TokenTypes.IDENT) &&
				variableName.equals(firstChildDetailAST.getText())) {

				return true;
			}
		}

		return false;
	}

	private static final String _MSG_MOVE_SIZE_ASSERTION =
		"size.assertion.move";

}