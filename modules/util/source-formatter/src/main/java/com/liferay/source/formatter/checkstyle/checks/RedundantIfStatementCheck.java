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

import com.liferay.petra.string.StringPool;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * @author Alan Huangs
 */
public class RedundantIfStatementCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_IF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.LITERAL_ELSE) {
			return;
		}

		DetailAST nextSiblingDetailAST = detailAST.getNextSibling();

		if ((nextSiblingDetailAST.getType() != TokenTypes.LITERAL_IF) ||
			(getClosingCurlyBraceLineNumber(detailAST) == -1) ||
			(getClosingCurlyBraceLineNumber(nextSiblingDetailAST) == -1) ||
			!_isSameExpressions(detailAST, nextSiblingDetailAST)) {

			return;
		}

		log(
			detailAST, _MSG_COMBINE_IF_STATEMENTS, detailAST.getLineNo(),
			nextSiblingDetailAST.getLineNo());
	}

	private boolean _isSameExpressions(
		DetailAST detailAST1, DetailAST detailAST2) {

		DetailAST lastChildDetailAST1 = detailAST1.getLastChild();
		DetailAST lastChildDetailAST2 = detailAST2.getLastChild();

		int endLineNumber1 = getEndLineNumber(lastChildDetailAST1) - 1;
		int startLineNumber1 = getStartLineNumber(lastChildDetailAST1) + 1;

		int endLineNumber2 = getEndLineNumber(lastChildDetailAST2) - 1;
		int startLineNumber2 = getStartLineNumber(lastChildDetailAST2) + 1;

		if ((endLineNumber1 - startLineNumber1) !=
				(endLineNumber2 - startLineNumber2)) {

			return false;
		}

		String line1 = StringPool.BLANK;
		String line2 = StringPool.BLANK;

		for (int i = -1; i < (endLineNumber1 - startLineNumber1); i++) {
			line1 = getLine(startLineNumber1 + i);
			line2 = getLine(startLineNumber2 + i);

			if (!line1.equals(line2)) {
				return false;
			}
		}

		return true;
	}

	private static final String _MSG_COMBINE_IF_STATEMENTS =
		"if.statements.combine";

}