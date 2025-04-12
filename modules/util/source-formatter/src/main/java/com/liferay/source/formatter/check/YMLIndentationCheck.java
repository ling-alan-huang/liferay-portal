/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.check.util.YMLSourceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class YMLIndentationCheck extends BaseFileCheck {
	private static final Pattern _sequencesAndMappingsPattern1 =
			Pattern.compile("^( *)[^ -].+:(\n\\1-(\n\\1 .+)*)+", Pattern.MULTILINE);
	private static final Pattern _sequencesAndMappingsPattern2 =
			Pattern.compile("(^( *)-)(?: )(.+(\n|\\Z))", Pattern.MULTILINE);

	private String _fixIncorrectIndentation(String content) {
		Matcher matcher = _sequencesAndMappingsPattern1.matcher(content);

		while (matcher.find()) {
			String s = matcher.group();

			String[] lines = s.split("\n");

			StringBundler sb = new StringBundler();

			for (int i = 1; i < lines.length; i++) {
				sb.append(StringPool.NEW_LINE);
				sb.append(StringPool.DOUBLE_SPACE);
				sb.append(lines[i]);
			}

			content = StringUtil.replaceFirst(
					content, matcher.group(),
					lines[0] + _fixIncorrectIndentation(sb.toString()));
		}

		return content;
	}


	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		content = content.replaceAll("\\n +\\n", "\n\n");

		Matcher matcher = _sequencesAndMappingsPattern2.matcher(content);

		while (matcher.find()) {
			content = StringUtil.replaceFirst(
					content, matcher.group(),
					StringBundler.concat(
							matcher.group(1), "\n", matcher.group(2), "  ",
							matcher.group(3)));
		}

		content = _fixIncorrectIndentation(content);

		content = _checkIndentation(content);


		content = content.replaceAll("(?m)^( *-)\n +(.*)", "$1   $2");
		return content;
	}

//	private String _checkIndentation1(String content) {
//
//		String[] lines = content.split("\n");
//		StringBundler sb = new StringBundler();
//
//		String leadingSpaces = null;
//		int leadingSpacesLength = 0;
//
//		for (int i = 0; i < lines.length; i++) {
//			if (i == 0) {
//				leadingSpaces = SourceUtil.getLeadingSpaces(lines[i]);
//				leadingSpacesLength = leadingSpaces.length();
//
//				continue;
//			}
//
//			if (lines[i].contains("\n")) {
//
//			}
//			sb.append(lines[i].substring(leadingSpacesLength));
//			sb.append("\n");
//
//
//
//		}
//
//		return content;
//	}

//	private String _checkIndentation(String content) {
//		List<String> definitions = _splitDefinitions(content, StringPool.BLANK);
//
//
//
//		return content;
//	}

	private String _checkIndentation(String content) {

		List<String> definitions = new ArrayList<>();

		String leadingSpaces = null;
		int leadingSpacesLength = 0;

		String[] lines = content.split("\n");

		if (lines.length == 1) {
			return StringUtil.trimLeading(lines[0]);
		}

		StringBundler sb = new StringBundler();

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			if (i == 0) {
				leadingSpaces = SourceUtil.getLeadingSpaces(lines[i]);
				leadingSpacesLength = leadingSpaces.length();

				sb.append(line);
				sb.append("\n");

				continue;
			}

			if (line.length() == 0 || line.matches(" +")) {
				sb.append(line);
				sb.append("\n");

				continue;

			}
			if (line.charAt(leadingSpacesLength) != ' ') {

				if (sb.index() > 0) {
					sb.setIndex(sb.index() - 1);

				}

				definitions.add(sb.toString());

				sb.setIndex(0);


			}

			sb.append(line);
			sb.append("\n");

		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
			definitions.add(sb.toString());

		}

		sb.setIndex(0);

		for (String definition : definitions) {
			lines = definition.split("\n");

			if (lines.length == 1) {
				sb.append(StringUtil.trimLeading(definition));
				sb.append("\n");

				continue;
			}

			String firstLine = lines[0];

			if (firstLine.endsWith("|")) {
				sb.append(StringUtil.trimLeading(firstLine));
				sb.append("\n");

				if (lines.length == 1) {
					continue;
				}

				for (int i = 1; i < lines.length; i++) {
					if (i == 1) {
						leadingSpaces = SourceUtil.getLeadingSpaces(lines[1]);
						leadingSpacesLength = leadingSpaces.length();
					}

					if (lines[i].length() == 0 || lines[i].matches(" +")) {
						sb.append("\n");

						continue;
					}

					sb.append("    " + lines[i].substring(leadingSpacesLength));
					sb.append("\n");

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

			for (int i = 0; i < lines.length; i++) {
				String indent = "    ";
				if (firstLine.matches(" +-.*") && lines[i].startsWith("-")) {
					indent = "        ";
				}
				sb2.append(indent + lines[i]);
				sb2.append("\n");

			}

			if (sb2.index() > 0) {
				sb2.setIndex(sb2.index() - 1);

			}

			sb.append(sb2.toString());
			sb.append("\n");

		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);

		}

		return sb.toString();
	}

//	private String _checkIndentation(String content, String indent) {
//		List<String> definitions = YMLSourceUtil.getDefinitions(
//				content, indent);
//
//	}

}