/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;

import java.util.List;

/**
 * @author Alan Huang
 */
public class IllegalHTMLTagsCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		List<String> avoidHtmlHeaderNames = getAttributeValues(
			_AVOID_HTML_HEADER_NAMES_KEY, absolutePath);

		for (String avoidHtmlHeaderName : avoidHtmlHeaderNames) {
			int x = -1;

			while (true) {
				x = StringUtil.indexOfAny(
					content,
					new String[] {
						"<" + avoidHtmlHeaderName + " ",
						"<" + avoidHtmlHeaderName + ">"
					},
					x + 1);

				if (x == -1) {
					break;
				}

				if (ToolsUtil.isInsideQuotes(content, x)) {
					continue;
				}

				addMessage(
					fileName,
					StringBundler.concat(
						"Do not use '", avoidHtmlHeaderName,
						"', use 'div' instead"),
					getLineNumber(content, x));
			}
		}

		return content;
	}

	private static final String _AVOID_HTML_HEADER_NAMES_KEY =
		"avoidHtmlHeaderNames";

}