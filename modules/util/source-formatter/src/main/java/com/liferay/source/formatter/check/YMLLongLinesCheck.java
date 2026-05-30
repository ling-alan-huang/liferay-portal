/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.io.IOException;

/**
 * @author Alan Huang
 */
public class YMLLongLinesCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

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
			if (description == null) {
				String trimmedLine = line.trim();

				if (!trimmedLine.startsWith("description:")) {
					sb.append(line);
					sb.append(StringPool.NEW_LINE);

					continue;
				}

				descriptionLeadingSpaces = SourceUtil.getLeadingSpaces(line);

				sb.append(descriptionLeadingSpaces);

				sb.append("description:");
				sb.append(StringPool.NEW_LINE);

				description = StringPool.BLANK;

				continue;
			}

			leadingSpaces = SourceUtil.getLeadingSpaces(line);

			if (leadingSpaces.length() > descriptionLeadingSpaces.length()) {
				description = description + StringPool.SPACE + line.trim();

				continue;
			}

			if (!description.isEmpty()) {
				String indent =
					descriptionLeadingSpaces + StringPool.FOUR_SPACES;

				description = indent + description.trim();

				if (!fileName.endsWith("/rest-openapi.yaml")) {
					description = _splitDescription(
						description, indent, maxLineLength);
				}

				sb.append(description);
				sb.append(StringPool.NEW_LINE);
			}

			sb.append(line);
			sb.append(StringPool.NEW_LINE);

			description = null;
			descriptionLeadingSpaces = StringPool.BLANK;
		}

		if (!Validator.isBlank(description)) {
			String indent = descriptionLeadingSpaces + StringPool.FOUR_SPACES;

			description = indent + description.trim();

			if (!fileName.endsWith("/rest-openapi.yaml")) {
				description = _splitDescription(
					description, indent, maxLineLength);
			}

			sb.append(description);
			sb.append(StringPool.NEW_LINE);
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private String _splitDescription(
		String description, String indent, int maxLineLength) {

		if (description.length() <= maxLineLength) {
			return description;
		}

		int pos = description.indexOf(CharPool.SPACE, indent.length());

		if (pos == -1) {
			return description;
		}

		if (pos > maxLineLength) {
			return StringBundler.concat(
				description.substring(0, pos), StringPool.NEW_LINE,
				_splitDescription(
					indent + description.substring(pos + 1), indent,
					maxLineLength));
		}

		pos = description.lastIndexOf(CharPool.SPACE, maxLineLength);

		return StringBundler.concat(
			description.substring(0, pos), StringPool.NEW_LINE,
			_splitDescription(
				indent + description.substring(pos + 1), indent,
				maxLineLength));
	}

	private static final String _MAX_LINE_LENGTH = "maxLineLength";

	private static final Log _log = LogFactoryUtil.getLog(
		YMLLongLinesCheck.class);

}