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

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public class JSONUtilCheck extends BaseChainedMethodCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.ASSIGN, TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.METHOD_CALL) {
			_checkChainedPutCalls(detailAST);
			_checkStringValueOfCalls(detailAST);
			_checkPutCalls(detailAST);

			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST.getType() != TokenTypes.EXPR) &&
			(parentDetailAST.getType() != TokenTypes.VARIABLE_DEF)) {

			return;
		}

		DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		DetailAST methodCallDetailAST = _getMethodCallDetailAST(
			detailAST, parentDetailAST);

		if (methodCallDetailAST == null) {
			return;
		}

		DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
			TokenTypes.ELIST);

		if (elistDetailAST.getChildCount() > 0) {
			return;
		}

		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		FullIdent fullIdent1 = FullIdent.createFullIdent(firstChildDetailAST);

		String methodName = fullIdent1.getText();

		if (!methodName.equals("JSONFactoryUtil.createJSONArray") &&
			!methodName.equals("JSONFactoryUtil.createJSONObject")) {

			return;
		}

		String variableName = getVariableName(detailAST, parentDetailAST);

		if (variableName == null) {
			return;
		}

		while (true) {
			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

			if (nextSiblingDetailAST == null) {
				return;
			}

			FullIdent fullIdent2 = getMethodCallFullIdent(
				nextSiblingDetailAST, variableName, "put");

			if (fullIdent2 != null) {
				log(
					detailAST, _MSG_USE_JSON_UTIL_PUT, methodName,
					fullIdent1.getLineNo(), variableName + ".put",
					fullIdent2.getLineNo(), "JSONUtil.put");
			}

			if (containsVariableName(
					nextSiblingDetailAST, variableName, true)) {

				return;
			}
		}
	}

	private void _checkChainedPutCalls(DetailAST methodCallDetailAST) {
		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

		if (!Objects.equals(fullIdent.getText(), "JSONUtil.put")) {
			return;
		}

		DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
			TokenTypes.ELIST);

		if (elistDetailAST.getChildCount() != 1) {
			return;
		}

		DetailAST parentDetailAST = methodCallDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.METHOD_CALL) {
			return;
		}

		DetailAST nextSiblingDetailAST = methodCallDetailAST.getNextSibling();

		if ((nextSiblingDetailAST.getType() == TokenTypes.IDENT) &&
			Objects.equals(nextSiblingDetailAST.getText(), "put")) {

			log(methodCallDetailAST, _MSG_USE_JSON_UTIL_PUT_ALL);
		}
	}

	private boolean _checkParentDetailAST(
		DetailAST detailAST, String variableName) {

		DetailAST parentDetailAST = detailAST.getParent();

		int count = 0;

		while (count < 3) {
			if (parentDetailAST.getType() == TokenTypes.LITERAL_IF) {
				DetailAST firstExprDetailAST = parentDetailAST.findFirstToken(
					TokenTypes.EXPR);

				DetailAST firstChildDetailAST =
					firstExprDetailAST.getFirstChild();

				if (firstChildDetailAST.getType() != TokenTypes.NOT_EQUAL) {
					return false;
				}

				DetailAST leftDetailAST = firstChildDetailAST.getFirstChild();
				DetailAST rightDetailAST = firstChildDetailAST.getLastChild();

				if (StringUtil.equals(leftDetailAST.getText(), variableName) &&
					(rightDetailAST.getType() == TokenTypes.LITERAL_NULL)) {

					return true;
				}

				return false;
			}

			count++;

			parentDetailAST = parentDetailAST.getParent();
		}

		return false;
	}

	private void _checkPutCalls(DetailAST detailAST) {
		if (!StringUtil.equals(getMethodName(detailAST), "put")) {
			return;
		}

		String variableName = getVariableName(detailAST);

		if (Validator.isNull(variableName)) {
			return;
		}

		String variableTypeName = getVariableTypeName(
			detailAST, variableName, false);

		if (!ArrayUtil.contains(_VARIABLE_TYPE_NAMES, variableTypeName) ||
			(detailAST.getPreviousSibling() != null) ||
			(detailAST.getNextSibling() != null)) {

			return;
		}

		String[] parameterArray = _getParameterArray(detailAST);

		if ((parameterArray == null) ||
			!_checkParentDetailAST(detailAST, parameterArray[1])) {

			return;
		}

		DetailAST variableDefinitionDetailAST = getVariableDefinitionDetailAST(
			detailAST, variableName);

		if (!_checkVariableDefinition(variableDefinitionDetailAST) ||
			!_checkVariableCaller(
				detailAST, parameterArray[1],
				variableDefinitionDetailAST.getLineNo())) {

			return;
		}

		int targetLineNo = _getTargetLineNo(
			variableDefinitionDetailAST, parameterArray[0]);

		if (targetLineNo != -1) {
			log(
				detailAST, _MSG_CHAIN_PUT, detailAST.getLineNo(),
				parameterArray[0], parameterArray[1], targetLineNo);
		}
	}

	private void _checkStringValueOfCalls(DetailAST detailAST) {
		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

		if (!Objects.equals(fullIdent.getText(), "String.valueOf")) {
			return;
		}

		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		if (elistDetailAST == null) {
			return;
		}

		firstChildDetailAST = elistDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.EXPR)) {

			return;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.METHOD_CALL)) {

			return;
		}

		List<DetailAST> methodCallDetailASTList = getAllChildTokens(
			firstChildDetailAST, true, TokenTypes.METHOD_CALL);

		if (methodCallDetailASTList.isEmpty()) {
			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.DOT) {
				return;
			}

			fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

			if (Objects.equals(fullIdent.getText(), "JSONUtil.put") ||
				Objects.equals(fullIdent.getText(), "JSONUtil.putAll")) {

				log(detailAST, _MSG_USE_JSON_UTIL_TO_STRING);
			}

			return;
		}

		List<String> chainedMethodNames = new ArrayList<>();

		for (DetailAST methodCallDetailAST : methodCallDetailASTList) {
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST != null) {
				List<DetailAST> childMethodCallDetailASTList =
					getAllChildTokens(
						dotDetailAST, false, TokenTypes.METHOD_CALL);

				if (!childMethodCallDetailASTList.isEmpty()) {
					continue;
				}
			}

			BaseCheck.ChainInformation chainInformation = getChainInformation(
				methodCallDetailAST);

			chainedMethodNames = chainInformation.getMethodNames();
		}

		for (String chainedMethodName : chainedMethodNames) {
			if (!chainedMethodName.equals("put") &&
				!chainedMethodName.equals("putAll")) {

				return;
			}
		}

		DetailAST methodCallDetailAST = methodCallDetailASTList.get(
			methodCallDetailASTList.size() - 1);

		firstChildDetailAST = methodCallDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

		String methodCall = fullIdent.getText();

		if (methodCall.startsWith("JSONUtil.")) {
			log(detailAST, _MSG_USE_JSON_UTIL_TO_STRING);
		}
	}

	private boolean _checkVariableCaller(
		DetailAST detailAST, String variableName,
		int variableDefinitionLineNo) {

		int lineNo = detailAST.getLineNo();

		DetailAST methodDefDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.METHOD_DEF);

		DetailAST firstSListDetailAST = methodDefDetailAST.findFirstToken(
			TokenTypes.SLIST);

		List<DetailAST> identDetailASTs = getAllChildTokens(
			firstSListDetailAST, true, TokenTypes.IDENT);

		for (DetailAST tmpDetailAST : identDetailASTs) {
			if (!StringUtil.equals(tmpDetailAST.getText(), variableName)) {
				continue;
			}

			int identLineNo = tmpDetailAST.getLineNo();

			if (identLineNo > lineNo) {
				break;
			}

			if (identLineNo < variableDefinitionLineNo) {
				continue;
			}

			DetailAST parentDetailAST = tmpDetailAST.getParent();

			if ((identLineNo < lineNo) &&
				(((parentDetailAST.getType() == TokenTypes.ASSIGN) &&
				  (tmpDetailAST.getNextSibling() != null)) ||
				 (parentDetailAST.getType() == TokenTypes.EXPR))) {

				return false;
			}
		}

		return true;
	}

	private boolean _checkVariableDefinition(
		DetailAST variableDefinitionDetailAST) {

		if (variableDefinitionDetailAST == null) {
			return false;
		}

		DetailAST assignDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			return false;
		}

		DetailAST exprDetailAST = assignDetailAST.findFirstToken(
			TokenTypes.EXPR);

		if (exprDetailAST == null) {
			return false;
		}

		DetailAST methodCallDetailAST = exprDetailAST.findFirstToken(
			TokenTypes.METHOD_CALL);

		if (methodCallDetailAST == null) {
			return false;
		}

		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		while (firstChildDetailAST != null) {
			if (firstChildDetailAST.getType() == TokenTypes.METHOD_CALL) {
				firstChildDetailAST = firstChildDetailAST.getFirstChild();

				continue;
			}

			if (firstChildDetailAST.getType() != TokenTypes.DOT) {
				return false;
			}

			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			String methodCall = fullIdent.getText();

			if (StringUtil.equals(methodCall, "JSONUtil.put")) {
				return true;
			}
			else if (StringUtil.equals(methodCall, "(.put")) {
				firstChildDetailAST = firstChildDetailAST.getFirstChild();
			}
			else {
				return false;
			}
		}

		return false;
	}

	private DetailAST _getMethodCallDetailAST(
		DetailAST assignDetailAST, DetailAST parentDetailAST) {

		DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

		DetailAST assignValueDetailAST = null;

		if (parentDetailAST.getType() == TokenTypes.EXPR) {
			assignValueDetailAST = firstChildDetailAST.getNextSibling();
		}
		else {
			assignValueDetailAST = firstChildDetailAST.getFirstChild();
		}

		if ((assignValueDetailAST != null) &&
			(assignValueDetailAST.getType() == TokenTypes.METHOD_CALL)) {

			return assignValueDetailAST;
		}

		return null;
	}

	private String[] _getParameterArray(DetailAST detailAST) {
		DetailAST eListDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		if (eListDetailAST == null) {
			return null;
		}

		List<DetailAST> childDetailAST = getAllChildTokens(
			eListDetailAST, false, TokenTypes.EXPR);

		if (ListUtil.isEmpty(childDetailAST)) {
			return null;
		}

		String[] resultArray = new String[2];

		for (DetailAST tempDetailAST : childDetailAST) {
			DetailAST firstChildDetailAST = tempDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.STRING_LITERAL) {
				resultArray[0] = firstChildDetailAST.getText();
			}
			else if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
				resultArray[1] = firstChildDetailAST.getText();
			}
		}

		if (Validator.isNull(resultArray[0]) |
			Validator.isNull(resultArray[1])) {

			return null;
		}

		return resultArray;
	}

	private int _getTargetLineNo(
		DetailAST variableDefinitionDetailAST, String keyName) {

		int lineNo = variableDefinitionDetailAST.getLineNo();

		DetailAST assignDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.ASSIGN);

		DetailAST exprDetailAST = assignDetailAST.findFirstToken(
			TokenTypes.EXPR);

		DetailAST methodCallDetailAST = exprDetailAST.findFirstToken(
			TokenTypes.METHOD_CALL);

		while (methodCallDetailAST != null) {
			if (methodCallDetailAST.getType() == TokenTypes.DOT) {
				methodCallDetailAST = methodCallDetailAST.getFirstChild();

				continue;
			}

			DetailAST eListDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.ELIST);

			DetailAST firstChildDetailAST = eListDetailAST.getFirstChild();

			DetailAST keyNameDetailAST = firstChildDetailAST.getFirstChild();

			String currentKeyName = keyNameDetailAST.getText();

			if (keyName.compareTo(currentKeyName) > 0) {
				DetailAST lastDetailAST = methodCallDetailAST.getLastChild();

				return lastDetailAST.getLineNo();
			}
			else if ((keyName.compareTo(currentKeyName) < 0) &&
					 (methodCallDetailAST.getLineNo() == lineNo)) {

				return eListDetailAST.getLineNo() - 1;
			}

			methodCallDetailAST = methodCallDetailAST.getFirstChild();
		}

		return -1;
	}

	private static final String _MSG_CHAIN_PUT = "json.util.chain.put";

	private static final String _MSG_USE_JSON_UTIL_PUT = "json.util.put.use";

	private static final String _MSG_USE_JSON_UTIL_PUT_ALL =
		"json.util.put.all.use";

	private static final String _MSG_USE_JSON_UTIL_TO_STRING =
		"json.util.to.string.use";

	private static final String[] _VARIABLE_TYPE_NAMES = {
		"JSONArray", "JSONObject"
	};

}