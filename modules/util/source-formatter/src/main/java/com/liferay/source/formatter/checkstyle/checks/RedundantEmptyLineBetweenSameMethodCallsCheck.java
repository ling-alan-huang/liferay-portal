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

package com.liferay.source.formatter.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Objects;

/**
 * @author Alan Huang
 */
public class RedundantEmptyLineBetweenSameMethodCallsCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		String classOrVariableName = getClassOrVariableName(detailAST);

		if (classOrVariableName == null) {
			return;
		}

		String variableTypeName = getVariableTypeName(
			detailAST, classOrVariableName, false);

		if (!Objects.equals(variableTypeName, "StringBundler")) {
			return;
		}

		String methodName = getMethodName(detailAST);

		if (!methodName.equals("append")) {
			return;
		}

		int previousMethodCallEndLineNumber = getEndLineNumber(detailAST);

		DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

		while ((nextSiblingDetailAST != null) &&
			   (nextSiblingDetailAST.getType() == TokenTypes.SEMI)) {

			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

			if (nextSiblingDetailAST.getType() != TokenTypes.EXPR) {
				return;
			}

			DetailAST firstChildDetailAST =
				nextSiblingDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) {
				return;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.DOT) {
				return;
			}

			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			String methodCall = fullIdent.getText();

			if (!methodCall.equals(classOrVariableName + "." + methodName)) {
				return;
			}

			if (getStartLineNumber(nextSiblingDetailAST) >
					(previousMethodCallEndLineNumber + 1)) {

				log(
					nextSiblingDetailAST, _MSG_UNNECESSARY_EMPTY_LINE,
					previousMethodCallEndLineNumber + 1);
			}

			previousMethodCallEndLineNumber = getEndLineNumber(
				nextSiblingDetailAST);

			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();
		}
	}

	private static final String _MSG_UNNECESSARY_EMPTY_LINE =
		"empty.line.unnecessary";

}