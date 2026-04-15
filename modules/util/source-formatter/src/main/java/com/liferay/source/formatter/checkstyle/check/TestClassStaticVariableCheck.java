/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class TestClassStaticVariableCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (!absolutePath.contains("/test/") &&
			!absolutePath.contains("/testIntegration/")) {

			return;
		}

		String className = getName(detailAST);

		if (!className.matches(".*Test(Case)?")) {
			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		if (objBlockDetailAST == null) {
			return;
		}

		DetailAST setUpMethodDefDetailAST = _getSetUpMethodDefDetailAST(
			objBlockDetailAST);

		if (setUpMethodDefDetailAST == null) {
			return;
		}

		List<DetailAST> variableDefDetailASTs = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.VARIABLE_DEF);

		for (DetailAST variableDefDetailAST : variableDefDetailASTs) {
			DetailAST modifiersDetailAST = variableDefDetailAST.findFirstToken(
				TokenTypes.MODIFIERS);

			if (!modifiersDetailAST.branchContains(TokenTypes.LITERAL_STATIC) ||
				!modifiersDetailAST.branchContains(
					TokenTypes.LITERAL_PRIVATE)) {

				continue;
			}

			String variableName = getName(variableDefDetailAST);

			if (!_isAssignedInSetUpMethod(
					setUpMethodDefDetailAST, variableName)) {

				continue;
			}

			log(
				variableDefDetailAST, _MSG_INCORRECT_VARIABLE_ASSIGN,
				variableName);
		}
	}

	private DetailAST _getSetUpMethodDefDetailAST(DetailAST detailAST) {
		List<DetailAST> methodDefDetailASTs = getAllChildTokens(
			detailAST, false, TokenTypes.METHOD_DEF);

		for (DetailAST methodDefDetailAST : methodDefDetailASTs) {
			DetailAST modifiersDetailAST = methodDefDetailAST.findFirstToken(
				TokenTypes.MODIFIERS);

			String methodName = getName(methodDefDetailAST);

			if (!methodName.equals("setUp") ||
				!modifiersDetailAST.branchContains(TokenTypes.LITERAL_PUBLIC)) {

				continue;
			}

			DetailAST typeDetailAST = methodDefDetailAST.findFirstToken(
				TokenTypes.TYPE);

			if (typeDetailAST == null) {
				continue;
			}

			DetailAST firstChildDetailAST = typeDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.LITERAL_VOID) {
				return methodDefDetailAST;
			}
		}

		return null;
	}

	private boolean _isAssignedInSetUpMethod(
		DetailAST detailAST, String variableName) {

		List<DetailAST> identDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.IDENT);

		for (DetailAST identDetailAST : identDetailASTs) {
			if (isMethodNameDetailAST(identDetailAST) ||
				!variableName.equals(identDetailAST.getText())) {

				continue;
			}

			DetailAST parentDetailAST = identDetailAST.getParent();

			if ((parentDetailAST != null) &&
				(parentDetailAST.getType() == TokenTypes.ASSIGN)) {

				return true;
			}
		}

		return false;
	}

	private static final String _MSG_INCORRECT_VARIABLE_ASSIGN =
		"variable.assign.incorrect";

}