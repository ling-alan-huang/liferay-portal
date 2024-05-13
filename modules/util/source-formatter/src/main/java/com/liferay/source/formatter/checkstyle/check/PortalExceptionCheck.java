/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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
public class PortalExceptionCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST != null) ||
			!_isExtendedPortalException(detailAST)) {

			return;
		}

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		List<DetailAST> classDefinitionDetailASTList = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.CLASS_DEF);
		List<DetailAST> constructorDefinitionDetailASTList = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.CTOR_DEF);

		if (_hasPublicConstructor(constructorDefinitionDetailASTList) ^
			_hasPublicStaticInnerClass(classDefinitionDetailASTList)) {

			return;
		}

		log(detailAST, _MSG_INCORRECT_CLASS_DEFINITION);
	}

	private boolean _hasPublicConstructor(List<DetailAST> detailASTList) {
		for (DetailAST detailAST : detailASTList) {
			DetailAST modifiersDetailAST = detailAST.findFirstToken(
				TokenTypes.MODIFIERS);

			if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PUBLIC)) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasPublicStaticInnerClass(List<DetailAST> detailASTList) {
		for (DetailAST detailAST : detailASTList) {
			DetailAST modifiersDetailAST = detailAST.findFirstToken(
				TokenTypes.MODIFIERS);

			if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PUBLIC) &&
				modifiersDetailAST.branchContains(TokenTypes.LITERAL_STATIC)) {

				return true;
			}
		}

		return false;
	}

	private boolean _isExtendedPortalException(DetailAST detailAST) {
		DetailAST extendsClauseDetailAST = detailAST.findFirstToken(
			TokenTypes.EXTENDS_CLAUSE);

		if (extendsClauseDetailAST == null) {
			return false;
		}

		String extendsClassName = null;
		DetailAST firstChildDetailAST = extendsClauseDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			String[] parts = StringUtil.split(fullIdent.getText(), "\\.");

			extendsClassName = parts[parts.length - 1];
		}
		else if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			extendsClassName = getName(extendsClauseDetailAST);
		}

		return extendsClassName.equals("PortalException");
	}

	private static final String _MSG_INCORRECT_CLASS_DEFINITION =
		"class.definition.incorrect";

}