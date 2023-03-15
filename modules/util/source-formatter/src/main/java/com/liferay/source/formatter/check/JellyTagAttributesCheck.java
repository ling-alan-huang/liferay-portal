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

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Qi Zhang
 */
public class JellyTagAttributesCheck extends BaseTagAttributesCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		return _formatSingleLineTagAttributes(absolutePath, content);
	}

	private String _formatSingleLineTagAttributes(
			String absolutePath, String content)
		throws Exception {

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				String trimmedLine = StringUtil.trimLeading(line);

				if (trimmedLine.matches("<\\w+ .*>.*")) {
					String htmlTag = getTag(trimmedLine, 0);

					if (htmlTag != null) {
						String newHTMLTag = formatTagAttributes(
							absolutePath, htmlTag, false, true);

						line = StringUtil.replace(line, htmlTag, newHTMLTag);
					}
				}

				for (String jspTag : getJSPTags(line)) {
					boolean forceSingleLine = false;

					if (!line.equals(jspTag)) {
						forceSingleLine = true;
					}

					String newJSPTag = formatTagAttributes(
						absolutePath, jspTag, false, forceSingleLine);

					line = StringUtil.replace(line, jspTag, newJSPTag);
				}

				sb.append(line);
				sb.append("\n");
			}
		}

		content = sb.toString();

		if (content.endsWith("\n")) {
			content = content.substring(0, content.length() - 1);
		}

		return content;
	}

}