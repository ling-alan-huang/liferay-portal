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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class PersistenceUpdateCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		_updateCallCheck(detailAST, StringPool.BLANK, "Persistence");
		_updateCallCheck(detailAST, "Layout", "LocalService");
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

		if (!StringUtil.equalsIgnoreCase(callerName, variableTypeName)) {
			return;
		}

		List<DetailAST> variableCallerDetailASTList =
			getVariableCallerDetailASTList(
				typeDetailAST.getParent(), variableName);

		int size = variableCallerDetailASTList.size();

		for (int i = 0; i < size; i++) {
			if (!equals(variableCallerDetailASTList.get(i), nameDetailAST)) {
				continue;
			}

			if (i == (size - 1)) {
				return;
			}

			DetailAST firstNextVariableCallerDetailAST =
				variableCallerDetailASTList.get(i + 1);

			if (firstNextVariableCallerDetailAST.getPreviousSibling() != null) {
				break;
			}

			parentDetailAST = firstNextVariableCallerDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.ASSIGN) {
				break;
			}

			parentDetailAST = parentDetailAST.getParent();

			if ((parentDetailAST.getType() != TokenTypes.EXPR) ||
				!equals(parentDetailAST.getParent(), slistDetailAST)) {

				break;
			}

			if (i <= (size - 2)) {
				DetailAST secondNextVariableCallerDetailAST =
					variableCallerDetailASTList.get(i + 2);

				if (secondNextVariableCallerDetailAST.getLineNo() >
						getEndLineNumber(parentDetailAST)) {

					return;
				}
			}

			break;
		}

		log(detailAST, _MSG_REASSIGN_UPDATE_CALL, variableName);
	}

	private static final String _MSG_REASSIGN_UPDATE_CALL =
		"update.call.reassign";

}