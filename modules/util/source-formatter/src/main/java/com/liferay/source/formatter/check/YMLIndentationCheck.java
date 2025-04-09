/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
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

//		content = _checkIndentation(content);
		return content;
	}

	private String _checkIndentation(String content) {
		List<String> definitions = _splitDefinitions(content, StringPool.BLANK);



		return content;
	}

	private List<String> _splitDefinitions(String content, String indent) {

		List<String> definitions = new ArrayList<>();

		String leadingSpaces = null;
		int leadingSpacesLength = 0;

		String[] lines = content.split("\n");

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
				sb.setIndex(sb.index() - 1);
			}

			definitions.add(sb.toString());

			sb.setIndex(0);
		}

		return null;
	}

//	private String _checkIndentation(String content, String indent) {
//		List<String> definitions = YMLSourceUtil.getDefinitions(
//				content, indent);
//
//	}

}