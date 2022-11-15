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

package com.liferay.language.security.scanner.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Seiphon Wang
 */
public class StringEscapeUtil {

	public static String escapeTag(String content) {
		Matcher matcher = _tagPattern.matcher(content);

		StringBuffer stringBuffer = new StringBuffer();

		while (matcher.find()) {
			String replacement = matcher.group();

			replacement = replacement.replaceAll(">", "&gt;");
			replacement = replacement.replaceAll("<", "&lt;");

			matcher.appendReplacement(stringBuffer, replacement);
		}

		matcher.appendTail(stringBuffer);

		return stringBuffer.toString();
	}

	public static String unEscape(String content) {
		content = content.replaceAll("&#39;", "'");
		content = content.replaceAll("&#039;", "'");
		content = content.replaceAll("&#149;", "•");
		content = content.replaceAll("&amp;", "&");
		content = content.replaceAll("&hellip;", "…");
		content = content.replaceAll("&laquo;", "«");
		content = content.replaceAll("&raquo;", "»");
		content = content.replaceAll("&quot;", "\"");
		content = content.replaceAll("&gt;", ">");
		content = content.replaceAll("&lt;", "<");
		content = content.replaceAll("&trade;", "™");
		content = content.replaceAll("&reg;", "®");

		return content;
	}

	public static String unEscapeQuot(String content) {
		return content.replaceAll("&quot;", "\"");
	}

	public static String unEscapeTag(String content) {
		content = content.replaceAll("&gt;", ">");
		content = content.replaceAll("&lt;", "<");

		return content;
	}

	private static final Pattern _tagPattern = Pattern.compile("<.+?>");

}