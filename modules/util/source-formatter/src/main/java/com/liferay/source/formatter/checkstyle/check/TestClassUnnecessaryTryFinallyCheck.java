/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alan Huang
 */
public class TestClassUnnecessaryTryFinallyCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_TRY};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if ((!absolutePath.contains("/test/") &&
			 !absolutePath.contains("/testIntegration/")) ||
			(detailAST.findFirstToken(TokenTypes.LITERAL_CATCH) != null)) {

			return;
		}

		DetailAST literalFinallyDetailAST = detailAST.findFirstToken(
			TokenTypes.LITERAL_FINALLY);

		if (literalFinallyDetailAST == null) {
			return;
		}

		DetailAST slistDetailAST = literalFinallyDetailAST.findFirstToken(
			TokenTypes.SLIST);

		if (slistDetailAST == null) {
			return;
		}

		String cleanupMethodName = null;

		DetailAST childDetailAST = slistDetailAST.getFirstChild();

		while (childDetailAST != null) {
			int type = childDetailAST.getType();

			if (type == TokenTypes.RCURLY) {
				break;
			}

			if (type == TokenTypes.SEMI) {
				childDetailAST = childDetailAST.getNextSibling();

				continue;
			}

			if (type != TokenTypes.EXPR) {
				return;
			}

			DetailAST firstChildDetailAST = childDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) {
				return;
			}

			String methodName = getMethodName(firstChildDetailAST);

			if ((methodName == null) ||
				!_cleanupMethodNames.contains(methodName)) {

				return;
			}

			if (cleanupMethodName == null) {
				cleanupMethodName = methodName;
			}

			childDetailAST = childDetailAST.getNextSibling();
		}

		if (cleanupMethodName == null) {
			return;
		}

		log(detailAST, _MSG_TRY_FINALLY_UNNECESSARY, cleanupMethodName);
	}

	private static final String _MSG_TRY_FINALLY_UNNECESSARY =
		"try.finally.unnecessary";

	private static final Set<String> _cleanupMethodNames = new HashSet<>(
		Arrays.asList("popServiceContext"));

}