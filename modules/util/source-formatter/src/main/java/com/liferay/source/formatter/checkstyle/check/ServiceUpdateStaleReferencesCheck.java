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

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Qi Zhang
 */
public class ServiceUpdateStaleReferencesCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		_updateCallCheck(detailAST, "Layout", "LocalService");
	}

	private boolean _checkNextSiblingStatementUseVariable(
		DetailAST detailAST, DetailAST slistDetailAST,
		DetailAST variableDefineDetailAST, String variableName) {

		int result = _checkUseVariable(detailAST, variableName);

		if (result == 1) {
			return true;
		}
		else if (result == -1) {
			return false;
		}

		DetailAST rootSlistDetailAST = variableDefineDetailAST.getParent();

		int tokenType = rootSlistDetailAST.getType();

		if (tokenType == TokenTypes.OBJBLOCK) {
			DetailAST parentSlistDetailAST = slistDetailAST;

			while (getParentWithTokenType(
						parentSlistDetailAST, TokenTypes.SLIST) != null) {

				parentSlistDetailAST = getParentWithTokenType(
					parentSlistDetailAST, TokenTypes.SLIST);
			}

			rootSlistDetailAST = parentSlistDetailAST;
		}
		else if ((tokenType == TokenTypes.PARAMETERS) ||
				 (tokenType == TokenTypes.FOR_EACH_CLAUSE)) {

			DetailAST tmpDetailAST = rootSlistDetailAST.getParent();

			rootSlistDetailAST = tmpDetailAST.findFirstToken(TokenTypes.SLIST);
		}

		if ((rootSlistDetailAST == null) ||
			(rootSlistDetailAST.getType() != TokenTypes.SLIST) ||
			equals(slistDetailAST, rootSlistDetailAST)) {

			return false;
		}

		DetailAST parentDetailAST = detailAST;

		while (true) {
			parentDetailAST = parentDetailAST.getParent();

			if ((parentDetailAST == null) ||
				equals(parentDetailAST, rootSlistDetailAST)) {

				break;
			}

			result = _checkUseVariable(parentDetailAST, variableName);

			if (result == 1) {
				return true;
			}
			else if (result == -1) {
				return false;
			}
		}

		return false;
	}

	private int _checkUseVariable(DetailAST detailAST, String variableName) {
		DetailAST nextSiblingDetailAST = detailAST.getNextSibling();

		while (nextSiblingDetailAST != null) {
			if (nextSiblingDetailAST.getType() == TokenTypes.LITERAL_RETURN) {
				return -1;
			}

			List<DetailAST> identDetailASTS = getAllChildTokens(
				nextSiblingDetailAST, true, TokenTypes.IDENT);

			for (DetailAST idntDetailAST : identDetailASTS) {
				if (!StringUtil.equals(variableName, idntDetailAST.getText())) {
					continue;
				}

				if (idntDetailAST.getPreviousSibling() != null) {
					return 1;
				}

				DetailAST parentDetailAST = idntDetailAST.getParent();

				if (parentDetailAST.getType() != TokenTypes.ASSIGN) {
					return 1;
				}

				parentDetailAST = parentDetailAST.getParent();

				if (parentDetailAST.getType() != TokenTypes.EXPR) {
					return 1;
				}

				return -1;
			}

			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();
		}

		return 0;
	}

	private void _updateCallCheck(
		DetailAST detailAST, String typeName, String baseName) {

		DetailAST dotDetailAST = detailAST.getFirstChild();

		if ((dotDetailAST.getType() != TokenTypes.DOT) ||
			(dotDetailAST.getChildCount() != 2)) {

			return;
		}

		DetailAST methodNameDetailAST = dotDetailAST.getLastChild();

		String methodName = methodNameDetailAST.getText();

		if (!methodName.equals("update" + typeName)) {
			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		DetailAST slistDetailAST = parentDetailAST.getParent();

		if (slistDetailAST.getType() != TokenTypes.SLIST) {
			return;
		}

		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		if (elistDetailAST.getChildCount() == 0) {
			return;
		}

		DetailAST firstChildDetailAST = elistDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		DetailAST nameDetailAST = firstChildDetailAST.getFirstChild();

		if (nameDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		String variableName = nameDetailAST.getText();

		DetailAST typeDetailAST = getVariableTypeDetailAST(
			detailAST, variableName);

		String variableTypeName = getTypeName(typeDetailAST, false);

		if ((typeDetailAST == null) ||
			(Validator.isNotNull(typeName) &&
			 !StringUtil.equals(variableTypeName, typeName))) {

			return;
		}

		DetailAST methodCallerDetailAST = dotDetailAST.getFirstChild();

		if (methodCallerDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		String callerName = methodCallerDetailAST.getText();

		callerName = StringUtil.removeSubstring(callerName, baseName);

		if (callerName.startsWith("_")) {
			callerName = callerName.substring(1);
		}

		if (!StringUtil.equalsIgnoreCase(callerName, variableTypeName) ||
			!_checkNextSiblingStatementUseVariable(
				parentDetailAST, slistDetailAST, typeDetailAST.getParent(),
				variableName)) {

			return;
		}

		log(detailAST, _MSG_REASSIGN_UPDATE_CALL, variableName);
	}

	private static final String _MSG_REASSIGN_UPDATE_CALL =
		"update.call.reassign";

}