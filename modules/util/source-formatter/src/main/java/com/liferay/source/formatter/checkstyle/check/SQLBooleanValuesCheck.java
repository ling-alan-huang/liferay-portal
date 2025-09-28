/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class SQLBooleanValuesCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (!absolutePath.contains("/upgrade/") ||
			absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/")) {

			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		List<DetailAST> methodCallDetailASTList = getMethodCalls(
			detailAST, "AutoBatchPreparedStatementUtil",
			new String[] {"autoBatch", "concurrentAutoBatch"});

		_checkBooleanValues(methodCallDetailASTList, true);

		methodCallDetailASTList = getMethodCalls(
			detailAST, null, new String[] {"prepareStatement"});

		_checkBooleanValues(methodCallDetailASTList, true);

		methodCallDetailASTList = getMethodCalls(
			detailAST, null, new String[] {"runSQL"});

		_checkBooleanValues(methodCallDetailASTList, false);
	}

	private void _checkBooleanValues(
		List<DetailAST> methodCallDetailASTList,
		boolean requiresTransformCall) {

		for (DetailAST methodCallDetailAST : methodCallDetailASTList) {
			List<DetailAST> literalStringDetailASTList = getAllChildTokens(
				methodCallDetailAST, true, TokenTypes.STRING_LITERAL);

			for (DetailAST literalStringDetailAST :
					literalStringDetailASTList) {

				String s = literalStringDetailAST.getText();

				s = s.substring(1, s.length() - 1);

				Matcher matcher = _falseTruePattern.matcher(s);

				if (matcher.find()) {
					String booleanValue = matcher.group();

					log(
						literalStringDetailAST, _MSG_INCORRECT_BOOLEAN_VALUE,
						StringUtil.toUpperCase(booleanValue),
						StringUtil.toLowerCase(booleanValue));
				}

				if (requiresTransformCall &&
					(s.contains("[FALSE]") || s.contains("[$TRUE]"))) {

					_checkMissingTransformCall(methodCallDetailAST);
				}
			}
		}
	}

	private void _checkMissingTransformCall(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		while (true) {
			if (parentDetailAST.getLineNo() < detailAST.getColumnNo()) {
				log(detailAST, _MSG_UNWRAPPED_SQL);

				return;
			}

			if (parentDetailAST.getType() != TokenTypes.METHOD_CALL) {
				parentDetailAST = parentDetailAST.getParent();

				continue;
			}

			DetailAST dotDetailAST = parentDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST == null) {
				parentDetailAST = parentDetailAST.getParent();

				continue;
			}

			List<String> names = getNames(dotDetailAST, false);

			if (names.size() != 2) {
				parentDetailAST = parentDetailAST.getParent();

				continue;
			}

			if (StringUtil.equals(names.get(0), "SQLTransformer") &&
				StringUtil.equals(names.get(1), "transform")) {

				return;
			}

			parentDetailAST = parentDetailAST.getParent();
		}
	}

	private static final String _MSG_INCORRECT_BOOLEAN_VALUE =
		"boolean.value.incorrect";

	private static final String _MSG_UNWRAPPED_SQL = "sql.unwrapped";

	private static final Pattern _falseTruePattern = Pattern.compile(
		"\\b(false|true)\\b");

}