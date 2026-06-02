/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.check.util.YMLSourceUtil;

import java.io.IOException;

/**
 * @author Alan Huang
 */
public class YMLStylingCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		content = content.trim();

		if (content.endsWith("\n---")) {
			content = content.substring(0, content.length() - 4);
		}

		if (content.startsWith("---\n")) {
			content = content.substring(4);
		}

		content = content.replaceAll(
			"(\\A|\n)( *#)@? ?(review)(\\Z|\n)", "$1$2 @$3$4");
		content = _formatDescription(fileName, absolutePath, content);

		return _formatQuotes(content);
	}

	private boolean _containsLogicalOperator(String s) {
		if (s.contains("!") || s.contains("&&") || s.contains("||")) {
			return true;
		}

		return false;
	}

	private String _fixBooleanValue(String s) {
		if (_isBooleanFalse(s)) {
			return "false";
		}

		if (_isBooleanTrue(s)) {
			return "true";
		}

		return s;
	}

	private String _fixNullValue(String s) {
		if (_isNullValue(s)) {
			return "null";
		}

		return s;
	}

	private String _fixQuotes(String s) {
		if (Validator.isNull(s) || (s.length() == 1)) {
			return s;
		}

		if ((s.charAt(0) == CharPool.APOSTROPHE) &&
			(s.charAt(s.length() - 1) == CharPool.APOSTROPHE)) {

			if (s.length() == 2) {
				return StringPool.QUOTE + StringPool.QUOTE;
			}

			String unquotedValue = s.substring(1, s.length() - 1);

			unquotedValue = StringUtil.replace(unquotedValue, "''", "'");
			unquotedValue = StringUtil.replace(unquotedValue, "\"", "\\\"");

			s = CharPool.QUOTE + unquotedValue + CharPool.QUOTE;
		}

		if ((s.charAt(0) == CharPool.QUOTE) &&
			(s.charAt(s.length() - 1) == CharPool.QUOTE)) {

			if (s.length() == 2) {
				return s;
			}

			String unquotedValue = s.substring(1, s.length() - 1);

			if (unquotedValue.contains(": ") || unquotedValue.contains("\\") ||
				unquotedValue.endsWith(":") ||
				unquotedValue.matches("-?\\d+(\\.\\d*)?") ||
				unquotedValue.startsWith("#") ||
				unquotedValue.startsWith("%") ||
				unquotedValue.startsWith("&") ||
				unquotedValue.startsWith("*") ||
				unquotedValue.startsWith("[") ||
				unquotedValue.startsWith("{") ||
				_containsLogicalOperator(unquotedValue) ||
				_isBooleanValue(unquotedValue) || _isNullValue(unquotedValue)) {

				return s;
			}

			return s.substring(1, s.length() - 1);
		}

		return s;
	}

	private String _formatDescription(
		String fileName, String absolutePath, String content) {

		content = content.replaceAll(
			"(\\A|\n)( *)(description:) +(?![|>])(.+)(\\Z|\n)",
			"$1$2$3\n    $2$4$5");
		content = content.replaceAll("(\\A|\n) *description:\n +\"\"", "");

		int maxLineLength = 0;

		try {
			maxLineLength = Integer.parseInt(
				getAttributeValue(_MAX_LINE_LENGTH, absolutePath));
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isDebugEnabled()) {
				_log.debug(numberFormatException);
			}

			return content;
		}

		StringBundler sb = new StringBundler();

		String description = null;
		String descriptionLeadingSpaces = StringPool.BLANK;
		String leadingSpaces = StringPool.BLANK;

		for (String line : content.split("\n")) {
			String trimmedLine = line.trim();

			if (trimmedLine.matches(".+?:.*")) {
				if (!Validator.isBlank(description)) {
					sb.append(
						_formatDescription(
							description,
							descriptionLeadingSpaces + StringPool.FOUR_SPACES,
							fileName, maxLineLength));
					sb.append(StringPool.NEW_LINE);
				}

				if (!trimmedLine.startsWith("description:")) {
					sb.append(line);
					sb.append(StringPool.NEW_LINE);

					description = null;

					continue;
				}

				descriptionLeadingSpaces = SourceUtil.getLeadingSpaces(line);

				sb.append(descriptionLeadingSpaces);

				sb.append("description:");
				sb.append(StringPool.NEW_LINE);

				description = StringPool.BLANK;

				continue;
			}

			if (description == null) {
				sb.append(line);
				sb.append(StringPool.NEW_LINE);

				continue;
			}

			leadingSpaces = SourceUtil.getLeadingSpaces(line);

			if (leadingSpaces.length() > descriptionLeadingSpaces.length()) {
				description =
					description.trim() + StringPool.SPACE + trimmedLine;
			}
		}

		if (!Validator.isBlank(description)) {
			sb.append(
				_formatDescription(
					description,
					descriptionLeadingSpaces + StringPool.FOUR_SPACES, fileName,
					maxLineLength));
			sb.append(StringPool.NEW_LINE);
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private String _formatDescription(
		String description, String indent, String fileName, int maxLineLength) {

		description = description.trim();

		if (!fileName.endsWith("/rest-openapi.yaml")) {
			return _splitDescription(description, indent, maxLineLength);
		}

		return indent + description;
	}

	private String _formatQuotes(String content) throws IOException {
		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String blockStyleLeadingSpaces = null;
			boolean insideBlockStyle = false;
			String leadingSpaces = null;
			String line = null;
			int lineNumber = 0;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lineNumber++;

				if (insideBlockStyle) {
					leadingSpaces = SourceUtil.getLeadingSpaces(line);

					if (leadingSpaces.length() >
							blockStyleLeadingSpaces.length()) {

						continue;
					}

					insideBlockStyle = false;
				}

				if (YMLSourceUtil.isBlockStyle(line)) {
					blockStyleLeadingSpaces = SourceUtil.getLeadingSpaces(line);
					insideBlockStyle = true;
				}

				String trimmedLine = StringUtil.trimLeading(line);

				int x = trimmedLine.indexOf(": ");

				if (x == -1) {
					continue;
				}

				String key = trimmedLine.substring(0, x);

				String newKey = _fixQuotes(key);

				if (!key.equals(newKey)) {
					return StringUtil.replaceFirst(
						content, key, newKey,
						getLineStartPos(content, lineNumber));
				}

				String value = trimmedLine.substring(x + 2);

				String newValue = _fixQuotes(value);

				newValue = _fixBooleanValue(newValue);
				newValue = _fixNullValue(newValue);

				if (value.equals(newValue)) {
					continue;
				}

				return StringUtil.replaceFirst(
					content, ": " + value, ": " + newValue,
					getLineStartPos(content, lineNumber));
			}
		}

		return content;
	}

	private boolean _isBooleanFalse(String s) {
		if (StringUtil.equalsIgnoreCase(s, "false") ||
			StringUtil.equalsIgnoreCase(s, "no") ||
			StringUtil.equalsIgnoreCase(s, "off")) {

			return true;
		}

		return false;
	}

	private boolean _isBooleanTrue(String s) {
		if (StringUtil.equalsIgnoreCase(s, "on") ||
			StringUtil.equalsIgnoreCase(s, "true") ||
			StringUtil.equalsIgnoreCase(s, "yes")) {

			return true;
		}

		return false;
	}

	private boolean _isBooleanValue(String s) {
		if (_isBooleanFalse(s) || _isBooleanTrue(s)) {
			return true;
		}

		return false;
	}

	private boolean _isNullValue(String s) {
		if (s.equals("NULL") || s.equals("Null") || s.equals("null") ||
			s.equals("~")) {

			return true;
		}

		return false;
	}

	private String _splitDescription(
		String description, String indent, int maxLineLength) {

		if (Validator.isNull(description)) {
			return StringPool.BLANK;
		}

		if ((indent.length() + description.length()) <= maxLineLength) {
			return indent + description;
		}

		description = indent + description;

		int x = description.indexOf(CharPool.SPACE, indent.length());

		if (x == -1) {
			return description;
		}

		if (x > maxLineLength) {
			String s = description.substring(x + 1);

			return description.substring(0, x) + "\n" +
				_splitDescription(s, indent, maxLineLength);
		}

		x = description.lastIndexOf(CharPool.SPACE, maxLineLength);

		String s = description.substring(x + 1);

		return description.substring(0, x) + "\n" +
			_splitDescription(s, indent, maxLineLength);
	}

	private static final String _MAX_LINE_LENGTH = "maxLineLength";

	private static final Log _log = LogFactoryUtil.getLog(
		YMLStylingCheck.class);

}