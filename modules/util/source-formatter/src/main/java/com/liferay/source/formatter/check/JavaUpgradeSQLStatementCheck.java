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

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.io.IOException;

import java.util.List;

/**
 * @author Peter Shin
 */
public class JavaUpgradeSQLStatementCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith("UpgradeProcess.java")) {
			return content;
		}

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = StringPool.BLANK;
			boolean makeSQlStatementFlg = false;

			StringBundler runSQLStatementSB = new StringBundler();

			while ((line = unsyncBufferedReader.readLine()) != null) {
				String trimmedLine = StringUtil.trim(line);

				if (!trimmedLine.startsWith("runSQL(") &&
					!makeSQlStatementFlg) {

					sb.append(line);
					sb.append(StringPool.NEW_LINE);

					continue;
				}

				if (trimmedLine.startsWith("runSQL(")) {
					makeSQlStatementFlg = true;
				}

				runSQLStatementSB.append(line);
				runSQLStatementSB.append(StringPool.NEW_LINE);

				if (trimmedLine.endsWith(StringPool.SEMICOLON)) {
					makeSQlStatementFlg = false;
				}

				if (!makeSQlStatementFlg) {
					if (runSQLStatementSB.index() > 0) {
						runSQLStatementSB.setIndex(
							runSQLStatementSB.index() - 1);
					}

					String runSQLStatement = runSQLStatementSB.toString();

					List<String> parameterList =
						JavaSourceUtil.getParameterList(runSQLStatement);

					runSQLStatement = _replaceErrorKeyWords(
						parameterList, runSQLStatement);

					sb.append(runSQLStatement);

					sb.append(StringPool.NEW_LINE);

					runSQLStatementSB = new StringBundler();
				}
			}
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private String _replaceErrorKeyWords(
		List<String> parameterList, String runSQLStatement) {

		for (String parameter : parameterList) {
			int start = -1;

			for (int i = 0; i < parameter.length(); i++) {
				if (parameter.charAt(i) == CharPool.QUOTE) {
					if (start == -1) {
						start = i + 1;
					}
					else {
						String insideQuotesContent = parameter.substring(
							start, i);

						StringBundler sb = new StringBundler();

						for (String element :
								insideQuotesContent.split(StringPool.SPACE)) {

							if (ArrayUtil.contains(
									_OLD_SQL_KEY_WORDS, element)) {

								sb.append(
									StringUtil.replace(
										element, _OLD_SQL_KEY_WORDS,
										_NEW_SQL_KEY_WORDS));
							}
							else {
								sb.append(element);
							}

							sb.append(StringPool.SPACE);
						}

						char lastElement = insideQuotesContent.charAt(
							insideQuotesContent.length() - 1);

						if ((sb.index() > 0) &&
							(lastElement != CharPool.SPACE)) {

							sb.setIndex(sb.index() - 1);
						}

						String newParameter = StringUtil.replaceFirst(
							parameter, insideQuotesContent, sb.toString());

						runSQLStatement = StringUtil.replaceFirst(
							runSQLStatement, parameter, newParameter);

						start = -1;
					}
				}
			}
		}

		return runSQLStatement;
	}

	private static final String[] _NEW_SQL_KEY_WORDS = {
		"alert", "and", "asc", "avg", "count", "create", "delete", "desc",
		"drop", "from", "insert", "into", "join", "like", "max", "on", "or",
		"order by", "select", "set", "sum", "table", "update", "values", "where"
	};

	private static final String[] _OLD_SQL_KEY_WORDS = {
		"ALERT", "AND", "ASC", "AVG", "COUNT", "CREATE", "DELETE", "DESC",
		"DROP", "FROM", "INSERT", "INTO", "JOIN", "LIKE", "MAX", "ON", "OR",
		"ORDER BY", "SELECT", "SET", "SUM", "TABLE", "UPDATE", "VALUES", "WHERE"
	};

}