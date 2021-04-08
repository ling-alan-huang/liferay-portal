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
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alan Huang
 */
public class UnnecessaryMethodCallCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		Map<String, String> methodReturnsMap = _getMethodReturnsMap(detailAST);

		List<DetailAST> methodCallDetailASTList = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTList) {
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST != null) {
				continue;
			}

			DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.ELIST);

			List<DetailAST> exprDetailASTList = getAllChildTokens(
				elistDetailAST, false, TokenTypes.EXPR);

			if (!exprDetailASTList.isEmpty()) {
				continue;
			}

			String methodName = getMethodName(methodCallDetailAST);

			if (methodReturnsMap.containsKey(methodName)) {
				log(
					methodCallDetailAST, _MSG_UNNECESSARY_METHOD_CALL,
					methodReturnsMap.get(methodName), methodName);
			}
		}
	}

	private String _getMethodName(DetailAST detailAST) {
		DetailAST nameDetailAST = detailAST.findFirstToken(TokenTypes.IDENT);

		return nameDetailAST.getText();
	}

	private Map<String, String> _getMethodReturnsMap(DetailAST detailAST) {
		Map<String, String> methodReturnsMap = new HashMap<>();

		List<DetailAST> methodDefinitionDetailASTList = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_DEF);

		for (DetailAST methodDefinitionDetailAST :
				methodDefinitionDetailASTList) {

			List<DetailAST> parameterDefs = getParameterDefs(detailAST);

			if (!parameterDefs.isEmpty()) {
				continue;
			}

			DetailAST slistDetailAST = methodDefinitionDetailAST.findFirstToken(
				TokenTypes.SLIST);

			if (slistDetailAST == null) {
				continue;
			}

			DetailAST firstChildDetailAST = slistDetailAST.getFirstChild();

			if ((firstChildDetailAST == null) ||
				(firstChildDetailAST.getType() != TokenTypes.LITERAL_RETURN)) {

				continue;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
				continue;
			}

			DetailAST nextSiblingDetailAST =
				firstChildDetailAST.getNextSibling();

			if ((nextSiblingDetailAST == null) ||
				(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

				continue;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
				continue;
			}

			methodReturnsMap.put(
				_getMethodName(methodDefinitionDetailAST),
				firstChildDetailAST.getText());
		}

		return methodReturnsMap;
	}

	private static final String _MSG_UNNECESSARY_METHOD_CALL =
		"method.call.unnecessary";

}