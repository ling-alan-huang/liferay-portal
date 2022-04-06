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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class PropertiesEmptyLinesCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (fileName.endsWith("/liferay-plugin-package.properties")) {
			return content;
		}

		return _fixMissingEmptyLines(content);
	}

	private boolean _afterSingleCommentIsEmptyLine(
		String trimmedLine, String nextLine, String content, int lineNumber) {

		int index = trimmedLine.indexOf(StringPool.EQUAL);

		if (Validator.isNull(nextLine) && (index != -1)) {
			nextLine = getLine(content, lineNumber + 2);

			if (Validator.isNotNull(nextLine)) {
				String currentVariableName = trimmedLine.substring(1, index);

				nextLine = StringUtil.trim(nextLine);

				index = nextLine.indexOf(StringPool.EQUAL);

				if (index != -1) {
					for (int i = 0; i < nextLine.length(); i++) {
						char element = nextLine.charAt(i);

						if ((element != '#') && (element != ' ') &&
							(i < index)) {

							String nextVariableName = nextLine.substring(
								i, index);

							if (StringUtil.equals(
									currentVariableName, nextVariableName)) {

								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	private String _appendResult(
		StringBundler sb, String line, boolean currentLineFlg,
		boolean notAddLineFlg) {

		if (notAddLineFlg) {
			sb.append(line);
			sb.append(StringPool.NEW_LINE);

			return StringUtil.trim(line);
		}

		if (currentLineFlg) {
			sb.append(StringPool.NEW_LINE);
			sb.append(line);
			sb.append(StringPool.NEW_LINE);

			return StringUtil.trim(line);
		}

		sb.append(line);
		sb.append(StringPool.NEW_LINE);
		sb.append(StringPool.NEW_LINE);

		return StringPool.BLANK;
	}

	private boolean _checkSingleCommentAfterMultiLine(
		String multiLinePropertyKey, String line) {

		String nextLinePropertyKey = StringUtil.extractFirst(
			line, StringPool.EQUAL);

		if (Validator.isNull(nextLinePropertyKey)) {
			return true;
		}

		for (int i = 0; i < multiLinePropertyKey.length(); i++) {
			char element = multiLinePropertyKey.charAt(i);

			if (element != CharPool.SPACE) {
				String currentPropertyKeyPattern =
					Pattern.quote(multiLinePropertyKey.substring(0, i)) + "#?" +
						Pattern.quote(multiLinePropertyKey.substring(i));

				return nextLinePropertyKey.matches(currentPropertyKeyPattern);
			}
		}

		return false;
	}

	private String _fixMissingEmptyLines(String content) throws IOException {
		int lineNumber = 0;

		StringBundler sb = new StringBundler();

		String previousLine = StringPool.BLANK;

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;
			boolean nextLineUnEmptyFlg = false;
			String multiLinePropertyKey = StringPool.BLANK;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lineNumber++;

				if (Validator.isNull(line)) {
					if (nextLineUnEmptyFlg) {
						nextLineUnEmptyFlg = false;
					}
					else {
						sb.append(StringPool.NEW_LINE);
						previousLine = StringPool.BLANK;
					}

					continue;
				}

				nextLineUnEmptyFlg = false;

				String trimmedLine = StringUtil.trim(line);

				if (StringUtil.equals(trimmedLine, StringPool.BACK_SLASH) ||
					StringUtil.equals(trimmedLine, "#\\")) {

					previousLine = _appendResult(sb, line, true, true);

					continue;
				}

				if (!trimmedLine.startsWith("#")) {
					int index = trimmedLine.indexOf("#");

					if ((index != -1) && (index < (trimmedLine.length() - 1)) &&
						(trimmedLine.charAt(index + 1) == CharPool.SPACE)) {

						trimmedLine = StringUtil.trim(
							trimmedLine.substring(0, index));
					}
				}

				if (trimmedLine.matches("[^#]+=\\\\")) {
					multiLinePropertyKey = line.substring(
						0, line.indexOf(StringPool.EQUAL));
				}

				if (Validator.isNotNull(previousLine) &&
					((!trimmedLine.startsWith("#") &&
					  (StringUtil.equals(previousLine, "##") ||
					   StringUtil.equals(previousLine, "#"))) ||
					 (trimmedLine.startsWith("##") &&
					  !previousLine.startsWith("##")) ||
					 (trimmedLine.matches("[^#]+=\\\\") &&
					  !previousLine.matches("#(?![ #]).+")))) {

					previousLine = _appendResult(sb, line, true, false);
					nextLineUnEmptyFlg = trimmedLine.endsWith(
						StringPool.BACK_SLASH);

					continue;
				}

				String nextLine = getLine(content, lineNumber + 1);

				if (Validator.isNull(nextLine)) {
					nextLine = StringPool.BLANK;
				}

				if (!trimmedLine.startsWith("#") &&
					!trimmedLine.endsWith(StringPool.BACK_SLASH) &&
					previousLine.endsWith(StringPool.BACK_SLASH) &&
					Validator.isNotNull(nextLine)) {

					previousLine = _appendResult(
						sb, line, false,
						_checkSingleCommentAfterMultiLine(
							multiLinePropertyKey, nextLine));
					multiLinePropertyKey = StringPool.BLANK;

					continue;
				}

				nextLine = StringUtil.trim(nextLine);

				if (trimmedLine.matches(_SINGLE_POUND_COMMENT_LINE_REGEX)) {
					if (StringUtil.equals(previousLine, "\\") ||
						StringUtil.equals(previousLine, "#\\") ||
						previousLine.matches("[^#]+=\\\\*")) {

						previousLine = _appendResult(sb, line, true, true);

						continue;
					}

					if (Validator.isNotNull(previousLine) &&
						!previousLine.matches("((# +.+)|#)") &&
						nextLine.matches(_SINGLE_POUND_COMMENT_LINE_REGEX)) {

						previousLine = _appendResult(sb, line, true, false);

						continue;
					}
				}

				if (trimmedLine.matches("#(?![ #]).+")) {
					if (previousLine.matches(
							_SINGLE_POUND_COMMENT_LINE_REGEX)) {

						previousLine = _appendResult(sb, line, true, false);
						nextLineUnEmptyFlg = true;
					}
					else {
						previousLine = _appendResult(sb, line, true, true);
						nextLineUnEmptyFlg = _afterSingleCommentIsEmptyLine(
							trimmedLine, nextLine, content, lineNumber);
					}

					continue;
				}

				sb.append(line);
				sb.append(StringPool.NEW_LINE);
				previousLine = trimmedLine;
			}
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private static final String _SINGLE_POUND_COMMENT_LINE_REGEX =
		"((# (?! ).+)|#)";

}