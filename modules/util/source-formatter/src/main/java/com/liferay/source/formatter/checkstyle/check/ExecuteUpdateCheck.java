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

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class ExecuteUpdateCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> executeUpdateMethodCallDetailASTList = getMethodCalls(
			detailAST, "executeUpdate");

		for (DetailAST executeUpdateMethodCallDetailAST :
				executeUpdateMethodCallDetailASTList) {

			_checkExecuteUpdateMethodCall(executeUpdateMethodCallDetailAST);
		}
	}

	private void _checkExecuteUpdateMethodCall(DetailAST methodCallDetailAST) {
		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		String variableName = firstChildDetailAST.getText();

		String typeName = getVariableTypeName(
			methodCallDetailAST, variableName, false);

		if ((typeName == null) || !typeName.equals("PreparedStatement")) {
			return;
		}

		DetailAST parentReturnDetailAST = getParentWithTokenType(
			methodCallDetailAST, TokenTypes.LITERAL_RETURN);

		if (parentReturnDetailAST != null) {
			return;
		}

		DetailAST parentLoopDetailAST = getParentWithTokenType(
			methodCallDetailAST, TokenTypes.LITERAL_DO, TokenTypes.LITERAL_FOR,
			TokenTypes.LITERAL_WHILE);

		if (parentLoopDetailAST == null) {
			return;
		}

		DetailAST parentElseOrIfDetailAST = getParentWithTokenType(
			methodCallDetailAST, TokenTypes.LITERAL_ELSE,
			TokenTypes.LITERAL_IF);

		if ((parentElseOrIfDetailAST != null) &&
			(parentElseOrIfDetailAST.getLineNo() >
				parentLoopDetailAST.getLineNo())) {

			return;
		}

		log(methodCallDetailAST, _MSG_USE_METHOD);
	}

	private static final String _MSG_USE_METHOD = "method.use";

}