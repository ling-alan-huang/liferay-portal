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

/**
 * @author Alan Huang
 */
public class YMLIndentationCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		content = _checkIndentation(content);
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
			String[] s = definition.split("\n");

			String firstLine = s[0];
			leadingSpaces = SourceUtil.getLeadingSpaces(firstLine);
			leadingSpacesLength = leadingSpaces.length();

			String subdefinition = definition.substring(firstLine.length() + 1);
			subdefinition = _checkIndentation(subdefinition);


			s = subdefinition.split("\n");

			StringBundler sb2 = new StringBundler();

			sb2.append(firstLine.trim());
			sb2.append("\n");

			for (int i = 0; i < s.length; i++) {
				sb2.append("    " + s[i]);
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