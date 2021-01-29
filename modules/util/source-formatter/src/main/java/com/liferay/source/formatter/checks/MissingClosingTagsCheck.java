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

package com.liferay.source.formatter.checks;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.tools.ToolsUtil;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class MissingClosingTagsCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		Matcher matcher = _openTagNamePattern.matcher(content);

		while (matcher.find()) {
			if (ToolsUtil.isInsideQuotes(content, matcher.start())) {
				continue;
			}

			String tag = _getTag(content, matcher.start());

			if ((tag != null) &&
				(tag.charAt(tag.length() - 2) == CharPool.SLASH)) {

				continue;
			}

			String tagName = matcher.group(1);

			int level = getLevel(
				content,
				new String[] {
					matcher.group() + StringPool.GREATER_THAN,
					matcher.group() + StringPool.NEW_LINE,
					matcher.group() + StringPool.SPACE,
					matcher.group() + StringPool.TAB
				},
				new String[] {"</" + tagName + ">"});

			if (level > 0) {
				addMessage(
					fileName, "Missing closing tag for '<" + tagName + ">'");
			}
		}

		return content;
	}

	private String _getTag(String s, int fromIndex) {
		int x = fromIndex;

		while (true) {
			x = s.indexOf(">", x + 1);

			if (x == -1) {
				return null;
			}

			String part = s.substring(fromIndex, x + 1);

			if (getLevel(part, "<", ">") == 0) {
				return part;
			}
		}
	}

	private static final Pattern _openTagNamePattern = Pattern.compile(
		"<([\\w:-]+)");

}