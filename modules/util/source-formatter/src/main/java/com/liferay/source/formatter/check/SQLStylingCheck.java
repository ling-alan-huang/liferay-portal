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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.util.SQLFormatterUtil;

/**
 * @author Hugo Huijser
 */
public class SQLStylingCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		for (String line : StringUtil.splitLines(content)) {
			String strippedQuotesLine = stripQuotes(line, CharPool.APOSTROPHE);

			if (strippedQuotesLine.contains(StringPool.QUOTE)) {
				String newLine = StringUtil.replace(
					line, CharPool.QUOTE, CharPool.APOSTROPHE);

				return StringUtil.replace(content, line, newLine);
			}
		}

		return _formatSQL(content);
	}

	private String _formatSQL(String content) {

		String[] lines = StringUtil.splitLines(content);

		StringBundler sb = new StringBundler();
		int sqlStartPos = 0;

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			if (line.startsWith("#")) {
				continue;
			}

			if ((sb.index() == 0) && Validator.isNotNull(line)) {
				sqlStartPos = getLineStartPos(content, i + 1);
			}

			String newSql = null;

			if (Validator.isNull(line) && (sb.index() > 0)) {
				sb.setIndex(sb.index() - 1);

				newSql = _callSQLFormatUtil(sb.toString());
			}
			else if (Validator.isNotNull(line)) {
				if (line.endsWith(StringPool.SEMICOLON)) {
					sb.append(line);

					newSql = _callSQLFormatUtil(sb.toString());
				}
				else {
					sb.append(line);
					sb.append(StringPool.NEW_LINE);
				}
			}

			if (Validator.isNotNull(newSql)) {
				if (!StringUtil.equals(sb.toString(), newSql)) {
					return StringUtil.replaceFirst(
							content, StringUtil.trim(sb.toString()), newSql,
							sqlStartPos);
				}

				sqlStartPos = 0;
				sb = new StringBundler();
			}

			if ((i == (lines.length - 1)) && (sb.index() > 0)) {
				sb.setIndex(sb.index() - 1);

				newSql = _callSQLFormatUtil(sb.toString());

				if (!StringUtil.equals(sb.toString(), newSql)) {
					return StringUtil.replaceFirst(
							content, StringUtil.trim(sb.toString()), newSql,
							sqlStartPos);
				}
			}
		}

		return content;
	}

	private String _callSQLFormatUtil(String content) {
		content = content.replaceAll("\n\t*", StringPool.SPACE);
		content = content.replaceAll("\\( ", "(");
		content = content.replaceAll(" +", " ");

		return SQLFormatterUtil.format(content);
	}

}