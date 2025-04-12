/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class YMLIndentationCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		content = _postFixIndentation(content);

		content = _checkIndentation(content);

		content = _preFixIndentation(content);

		return content;
	}

	private String _checkIndentation(String content) {
		String[] lines = content.split("\n");

		if (lines.length == 1) {
			return StringUtil.trimLeading(lines[0]);
		}

		List<String> definitions = new ArrayList<>();

		StringBundler sb1 = new StringBundler();

		String leadingSpaces = StringPool.BLANK;
		int leadingSpacesLength = 0;

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			if (i == 0) {
				leadingSpaces = SourceUtil.getLeadingSpaces(line);

				leadingSpacesLength = leadingSpaces.length();

				sb1.append(line);
				sb1.append("\n");

				continue;
			}

			if ((line.length() == 0) || line.matches(" +")) {
				sb1.append(line);
				sb1.append("\n");

				continue;
			}

			if (line.charAt(leadingSpacesLength) != ' ') {
				if (sb1.index() > 0) {
					sb1.setIndex(sb1.index() - 1);
				}

				definitions.add(sb1.toString());

				sb1.setIndex(0);
			}

			sb1.append(line);
			sb1.append("\n");
		}

		if (sb1.index() > 0) {
			sb1.setIndex(sb1.index() - 1);

			definitions.add(sb1.toString());
		}

		sb1.setIndex(0);

		for (String definition : definitions) {
			lines = definition.split("\n");

			if (lines.length == 1) {
				sb1.append(StringUtil.trimLeading(definition));
				sb1.append("\n");

				continue;
			}

			String firstLine = lines[0];

			if (firstLine.endsWith("|")) {
				sb1.append(StringUtil.trimLeading(firstLine));
				sb1.append("\n");

				if (lines.length == 1) {
					continue;
				}

				for (int i = 1; i < lines.length; i++) {
					String line = lines[i];

					if (i == 1) {
						leadingSpaces = SourceUtil.getLeadingSpaces(line);

						leadingSpacesLength = leadingSpaces.length();
					}

					if ((line.length() == 0) || line.matches(" +")) {
						sb1.append("\n");

						continue;
					}

					sb1.append(StringPool.FOUR_SPACES);
					sb1.append(line.substring(leadingSpacesLength));
					sb1.append("\n");
				}

				continue;
			}

			leadingSpaces = SourceUtil.getLeadingSpaces(firstLine);

			leadingSpacesLength = leadingSpaces.length();

			String subdefinition = definition.substring(firstLine.length() + 1);

			subdefinition = _checkIndentation(subdefinition);

			lines = subdefinition.split("\n");

			StringBundler sb2 = new StringBundler();

			sb2.append(firstLine.trim());
			sb2.append("\n");

			for (String line : lines) {
				sb2.append(StringPool.FOUR_SPACES + line);
				sb2.append("\n");
			}

			if (sb2.index() > 0) {
				sb2.setIndex(sb2.index() - 1);
			}

			sb1.append(sb2.toString());
			sb1.append("\n");
		}

		if (sb1.index() > 0) {
			sb1.setIndex(sb1.index() - 1);
		}

		return sb1.toString();
	}

	private String _postFixIndentation(String content) {
		content = content.replaceAll("\\n +\\n", "\n\n");

		StringBundler sb = new StringBundler();

		String[] lines = content.split("\n");

		for (String line : lines) {
			String trimmedLine = line.trim();

			if (Validator.isBlank(trimmedLine)) {
				sb.append("\n");

				continue;
			}

			Matcher matcher = _dashPattern.matcher(line);

			if (matcher.matches()) {
				String indent = matcher.group(1);

				sb.append(StringUtil.trimTrailing(indent));

				sb.append("\n");
				sb.append(indent.replaceFirst("-", " "));
				sb.append(matcher.group(2));
				sb.append("\n");

				continue;
			}

			sb.append(line);
			sb.append("\n");
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private String _preFixIndentation(String content) {
		content = content.replaceAll("(?m)^( *-)\n +(.*)", "$1   $2");

		return content.replaceAll("\\n +\\n", "\n\n");
	}

	private static final Pattern _dashPattern = Pattern.compile("( +- +)(.+)");

}