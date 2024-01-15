/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Objects;

/**
 * @author Simon Jiang
 */
public class UnnecessaryClassConstructorCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> allCtorDefDetailASTList = getAllChildTokens(
			detailAST, true, TokenTypes.CTOR_DEF);

		if (allCtorDefDetailASTList.isEmpty()) {
			return;
		}

		int startLineNumber = 0;

		String ctorDefName = null;

		for (DetailAST ctorDetailAST : allCtorDefDetailASTList) {
			List<DetailAST> allCtorDefParametersDetailASTList =
				getAllChildTokens(ctorDetailAST, false, TokenTypes.PARAMETERS);

			for (DetailAST parametersDetailAST :
					allCtorDefParametersDetailASTList) {

				DetailAST firstChildParametersDetailAST =
					parametersDetailAST.getFirstChild();

				if (Objects.nonNull(firstChildParametersDetailAST)) {
					return;
				}
			}

			DetailAST firstChildDetailAST = ctorDetailAST.getFirstChild();

			if (Objects.isNull(firstChildDetailAST) ||
				(firstChildDetailAST.getType() != TokenTypes.MODIFIERS)) {

				continue;
			}

			DetailAST literalPrivateDetailAST =
				firstChildDetailAST.getFirstChild();

			if (Objects.isNull(literalPrivateDetailAST) ||
				(literalPrivateDetailAST.getType() !=
					TokenTypes.LITERAL_PRIVATE)) {

				return;
			}

			DetailAST parametersDetailAST = ctorDetailAST.findFirstToken(
				TokenTypes.PARAMETERS);

			if (Objects.isNull(parametersDetailAST)) {
				continue;
			}

			if (parametersDetailAST.getChildCount() > 0) {
				return;
			}

			DetailAST nextSiblingSlistDetailAST = ctorDetailAST.findFirstToken(
				TokenTypes.SLIST);

			if (Objects.isNull(nextSiblingSlistDetailAST)) {
				return;
			}

			DetailAST firstChildNextSiblingDetailAST =
				nextSiblingSlistDetailAST.getFirstChild();

			if (Objects.isNull(firstChildNextSiblingDetailAST) ||
				(firstChildNextSiblingDetailAST.getType() !=
					TokenTypes.RCURLY)) {

				return;
			}

			startLineNumber = getStartLineNumber(ctorDetailAST);

			ctorDefName = getName(ctorDetailAST);

			break;
		}

		List<DetailAST> allMethodsDetailAST = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_DEF);

		int totalStaicMethods = 0;

		for (DetailAST methodDetailAST : allMethodsDetailAST) {
			DetailAST methodModifiersDetailAST = methodDetailAST.findFirstToken(
				TokenTypes.MODIFIERS);

			int methodModifiersStaticDetailASTChildCount =
				methodModifiersDetailAST.getChildCount(
					TokenTypes.LITERAL_STATIC);

			if (methodModifiersStaticDetailASTChildCount == 0) {
				return;
			}

			totalStaicMethods++;
		}

		if (allMethodsDetailAST.size() != totalStaicMethods) {
			return;
		}

		log(startLineNumber, _MSG_CONSTRUCTOR_UNNECESSARY_DEF, ctorDefName);
	}

	private static final String _MSG_CONSTRUCTOR_UNNECESSARY_DEF =
		"constructor.unnecessary.unused";

}