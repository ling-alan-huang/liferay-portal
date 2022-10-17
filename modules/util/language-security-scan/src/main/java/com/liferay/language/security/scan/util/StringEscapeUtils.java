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

package com.liferay.language.security.scan.util;

/**
 * @author Seiphon Wang
 */
public class StringEscapeUtils {

	public static String unEscapeSpecialCharactors(String content) {
		content = content.replaceAll("&quot;", "\"");

		content = content.replaceAll("&gt;", ">");
		content = content.replaceAll("&amp;", "&");
		content = content.replaceAll("•", "&#149;");
		content = content.replaceAll("&raquo;", "»");

		return content;
	}

}