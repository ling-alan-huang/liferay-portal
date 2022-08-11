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

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qi Zhang
 */
public class JSPTagVariableStyleCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

//		Matcher matcher = _jspExpressionPattern1.matcher(content);
//		
//		StringBuffer sb = new StringBuffer();
//
//		while (matcher.find()) {
//			String s1 = matcher.group(1);
//			String s2 = matcher.group(2);
//
//			String replacement = matcher.group(1) + "<%=" + matcher.group(2);
//
//			matcher.appendReplacement(sb, replacement);
//
//		}
//
//		if (sb.length() > 0) {
//			matcher.appendTail(sb);
//
//			return sb.toString();
//		}
		content =  content.replaceAll("<%= \"([^\"']+)?\" \\+( [\\w\\d]+ .*%>)", "$1<%=$2");

//		matcher = _jspExpressionPattern2.matcher(content);
//		
//		sb = new StringBuffer();
//
//		while (matcher.find()) {
//			String s1 = matcher.group(1);
//			String s2 = matcher.group(2);
//
//			String replacement = matcher.group(1) + "%><%=" + matcher.group(2);
//
//			matcher.appendReplacement(sb, replacement);
//
//		}
//
//		if (sb.length() > 0) {
//			matcher.appendTail(sb);
//
//			return sb.toString();
//		}
		
		content =  content.replaceAll("(<%= [\\w\\d]+ )\\+( \"([^\"']+?)\".*%>)", "$1%><%=$2");

		content =  content.replaceAll("<%= \"([^\"']+?)\" %>", "$1");

		return content;
	}


	private static final Pattern _jspExpressionPattern1 = Pattern.compile(
			"<%= \"([^\"']+)?\" \\+( [\\w\\d]+ .*%>)");

	private static final Pattern _jspExpressionPattern2 = Pattern.compile(
			"(<%= [\\w\\d]+ )\\+( \"([^\"']+?)\".*%>)");

	
}