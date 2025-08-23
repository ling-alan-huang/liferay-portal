/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class RowCheckerClassNameCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		String absolutePath = getAbsolutePath();

		if (!absolutePath.contains("/modules/")) {
			return;
		}

		String extendedClassName = _getExtendedClassName(detailAST);

		if ((extendedClassName == null) ||
			!extendedClassName.equals("EmptyOnClickRowChecker")) {

			return;
		}

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		List<DetailAST> constructorDefinitionDetailASTList = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.CTOR_DEF);

		if (constructorDefinitionDetailASTList.size() != 1) {
			return;
		}

		DetailAST constructorDefinitionDetailAST =
			constructorDefinitionDetailASTList.get(0);

		List<DetailAST> parameterDefs = getParameterDefs(
			constructorDefinitionDetailAST);

		for (DetailAST parameterDefinitionDetailAST : parameterDefs) {
			DetailAST typeDetailAST =
				parameterDefinitionDetailAST.findFirstToken(TokenTypes.TYPE);

			FullIdent typeFullIdent = FullIdent.createFullIdentBelow(
				typeDetailAST);

			String typeName = typeFullIdent.getText();

			int x = typeName.lastIndexOf(".");

			if (x != -1) {
				typeName = typeName.substring(x + 1);
			}

			char c = typeName.charAt(0);

			if (Character.isUpperCase(c) && !typeName.equals("String")) {
				_containsTypeNameOrVariableName(
					getName(detailAST), typeName, detailAST);
			}
			else {
				DetailAST nextSiblingDetailAST = typeDetailAST.getNextSibling();

				if ((nextSiblingDetailAST == null) ||
					(nextSiblingDetailAST.getType() != TokenTypes.IDENT)) {

					continue;
				}

				_containsTypeNameOrVariableName(
					getName(detailAST), nextSiblingDetailAST.getText(),
					detailAST);
			}
		}
	}

	private void _containsTypeNameOrVariableName(
		String className, String name, DetailAST detailAST) {

		if (name.equals("PortletResponse")) {
			return;
		}

		if (name.endsWith("Id")) {
			name = name.substring(0, name.length() - 2);
		}

		if (className.contains(name)) {
			return;
		}

		log(detailAST, _MSG_INCORRECT_CLASS_NAME, className, name);
	}

	private String _getExtendedClassName(DetailAST detailAST) {
		DetailAST extendsClauseDetailAST = detailAST.findFirstToken(
			TokenTypes.EXTENDS_CLAUSE);

		if (extendsClauseDetailAST == null) {
			return null;
		}

		DetailAST firstChildDetailAST = extendsClauseDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			String[] parts = StringUtil.split(fullIdent.getText(), "\\.");

			return parts[parts.length - 1];
		}

		if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			return getName(extendsClauseDetailAST);
		}

		return null;
	}

	private static final String _MSG_INCORRECT_CLASS_NAME =
		"class.name.incorrect";

}