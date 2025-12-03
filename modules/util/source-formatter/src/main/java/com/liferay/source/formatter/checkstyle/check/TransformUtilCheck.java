/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class TransformUtilCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.VARIABLE_DEF};
	}

	private boolean isListTypeLoop(DetailAST detailAST) {
		DetailAST exprDetailAST = detailAST.findFirstToken(TokenTypes.EXPR);

		DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
			return false;
		}

		String variableTypeName = getVariableTypeName(
			firstChildDetailAST, firstChildDetailAST.getText(), true);

		if ((variableTypeName == null) ||
			(!variableTypeName.startsWith("List<") &&
			 !variableTypeName.endsWith(">"))) {

			return false;
		}

		String typeArguments = variableTypeName.substring(
			5, variableTypeName.length() - 1);

		if (typeArguments.contains("<") || typeArguments.contains("[")) {
			return false;
		}

		return true;
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String variableName = getName(detailAST);

		//		DetailAST variableDefinitionDetailAST =
		//				getVariableDefinitionDetailAST(
		//						detailAST, variableName, false);

		String variableTypeName = getVariableTypeName(
			detailAST, variableName, true);

		if ((variableTypeName == null) || !variableTypeName.endsWith(">") ||
			!variableTypeName.startsWith("List<")) {

			return;
		}

		String typeArguments = variableTypeName.substring(
			5, variableTypeName.length() - 1);

		if (typeArguments.contains("<") || typeArguments.contains("[")) {
			return;
		}

		//
		List<DetailAST> variableCallerDetailASTs = getVariableCallerDetailASTs(
			detailAST, variableName);

		if (variableCallerDetailASTs.isEmpty()) {
			return;
		}

		DetailAST variableCallerDetailAST = variableCallerDetailASTs.get(0);

		DetailAST parentDetailAST = variableCallerDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(parentDetailAST);

		if (!StringUtil.equals(fullIdent.getText(), variableName + ".add")) {
			return;
		}

		//
		DetailAST literalForDetailAST = getParentWithTokenType(
			parentDetailAST, TokenTypes.LITERAL_FOR);

		if ((literalForDetailAST == null) ||
			(literalForDetailAST.getLineNo() < detailAST.getLineNo())) {

			return;
		}

		DetailAST forEachClauseDetailAST = literalForDetailAST.findFirstToken(
			TokenTypes.FOR_EACH_CLAUSE);

		if (forEachClauseDetailAST == null) {
			return;
		}

		String typeName = getTypeName(
				forEachClauseDetailAST.findFirstToken(TokenTypes.VARIABLE_DEF), false);

		if (typeName.contains("[") || typeName.equals("Cell")) {
			return;
		}

		DetailAST nextSiblingDetailAST =
			forEachClauseDetailAST.getNextSibling();

		if (nextSiblingDetailAST.getType() != TokenTypes.RPAREN) {
			return;
		}

		nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

		if (nextSiblingDetailAST.getType() != TokenTypes.SLIST) {
			return;
		}

		List<DetailAST> childDetailASTs = getAllChildTokens(
			nextSiblingDetailAST, true, TokenTypes.DEC, TokenTypes.DO_WHILE,
			TokenTypes.INC, TokenTypes.LITERAL_FOR, TokenTypes.LITERAL_RETURN,
			TokenTypes.POST_DEC, TokenTypes.LITERAL_WHILE, TokenTypes.POST_INC);

		if (ListUtil.isNotEmpty(childDetailASTs)) {
			return;
		}

		// All assigns

		List<DetailAST> assignDetailASTs = getAllChildTokens(
			nextSiblingDetailAST, true, TokenTypes.ASSIGN);

		for (DetailAST assignDetailAST : assignDetailASTs) {
			parentDetailAST = assignDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.EXPR) {
				continue;
			}

			String name = getName(assignDetailAST);

			if (name == null) {
				return;
			}

			DetailAST variableDefinitionDetailAST =
				getVariableDefinitionDetailAST(assignDetailAST, name, false);

			if ((variableDefinitionDetailAST == null) ||
				(variableDefinitionDetailAST.getLineNo() <
					forEachClauseDetailAST.getLineNo())) {

				return;
			}
		}

		// All method calls

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			nextSiblingDetailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
//			if (hasParentWithTokenType(methodCallDetailAST, TokenTypes.METHOD_CALL)) {
//				continue;
//			}
			parentDetailAST = getParentWithTokenType(methodCallDetailAST, TokenTypes.METHOD_CALL);
			
			if (parentDetailAST != null && parentDetailAST.getLineNo() > forEachClauseDetailAST.getLineNo()) {
				continue;
			}
			
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST == null) {
				continue;
			}

			List<String> names = getNames(dotDetailAST, false);

			if (names.size() != 2) {
				continue;
			}

			String methodCallClassName = names.get(0);

			if (Character.isUpperCase(methodCallClassName.charAt(0))) {
				continue;
			}

			String methodCallMethodName = names.get(1);
			
			if (methodCallClassName.equals(variableName)) {
				if (methodCallMethodName.equals("add")) {
					continue;
				}
				return;
			}

//			Character.isUpperCase(methodCallClassName.charAt(0))
			DetailAST lastChildDetailAST = nextSiblingDetailAST.getLastChild();
			DetailAST variableDefinitionDetailAST =
				getVariableDefinitionDetailAST(
					dotDetailAST, methodCallClassName, true);


			if (variableDefinitionDetailAST == null || (variableDefinitionDetailAST.getLineNo() <
					forEachClauseDetailAST.getLineNo() || variableDefinitionDetailAST.getLineNo() > lastChildDetailAST.getLineNo())) {
//				String methodCallMethodName = names.get(1);

				if (methodCallMethodName.startsWith("add") ||
						methodCallMethodName.startsWith("put") ||
						methodCallMethodName.startsWith("set")) {

					return;
				}
			}

			
//			if (variableDefinitionDetailAST.getLineNo() <
//					forEachClauseDetailAST.getLineNo() || variableDefinitionDetailAST.getLineNo() > lastChildDetailAST.getLineNo()) {
//
//				String methodCallMethodName = names.get(1);
//
//				if (methodCallMethodName.startsWith("add") ||
//					methodCallMethodName.startsWith("put") ||
//					methodCallMethodName.startsWith("set")) {
//
//					return;
//				}
//			}
		}

		String absolutePath = getAbsolutePath();

		if (absolutePath.endsWith("ResourceImpl.java") &&
			absolutePath.matches(".+/internal/resource/v\\d*(_\\d+)+/.+")) {

			log(forEachClauseDetailAST, _MSG_USE_TRANSFORM);
		}
		else {
			log(forEachClauseDetailAST, _MSG_USE_TRANSFORM_UTIL_TRANSFORM);
		}
	}

	private static final String _MSG_USE_TRANSFORM = "transform.use";

	private static final String _MSG_USE_TRANSFORM_UTIL_TRANSFORM =
		"transform.util.transform.use";

}