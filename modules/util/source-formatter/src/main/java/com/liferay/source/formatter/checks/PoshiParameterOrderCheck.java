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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.checks.util.SourceUtil;

import java.io.IOException;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PoshiParameterOrderCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		return _sortPoshiParameter(content);
	}

	private String _sortPoshiParameter(String content) {
		Matcher matcher1 = _methodCallPattern.matcher(content);

		while (matcher1.find()) {
			String indent = SourceUtil.getIndent(matcher1.group());

			String parameters = matcher1.group(1);

			Matcher matcher2 = _parametersPattern.matcher(parameters);

			Map<String, String> parametersMap = new TreeMap<>(
				new NaturalOrderStringComparator());

			while (matcher2.find()) {
				String parameter = StringUtil.trim(matcher2.group(1));

				int equalPos = parameter.indexOf(" = ");

				if (equalPos > 0) {
					String parameterName;
					String parameterValue;

					parameterName = parameter.substring(0, equalPos);

					if (parameter.endsWith(StringPool.COMMA)) {
						parameterValue = parameter.substring(
							equalPos + 3, parameter.length() - 1);
					}
					else {
						parameterValue = parameter.substring(equalPos + 3);
					}

					parametersMap.put(parameterName, parameterValue);
				}
			}

			if (parametersMap.isEmpty()) {
				continue;
			}

			StringBundler sb = new StringBundler();

			for (Map.Entry<String, String> entry : parametersMap.entrySet()) {
				if (parametersMap.size() == 1) {
					sb.append(entry.getKey());
					sb.append(" = ");
					sb.append(entry.getValue());

					break;
				}

				sb.append(CharPool.NEW_LINE);
				sb.append(indent);
				sb.append(CharPool.TAB);
				sb.append(entry.getKey());
				sb.append(" = ");
				sb.append(entry.getValue());
				sb.append(CharPool.COMMA);
			}

			if (parametersMap.size() > 1) {
				sb.setIndex(sb.index() - 1);
			}

			content = StringUtil.replaceFirst(
				content, parameters, sb.toString());
		}

		return content;
	}

	private static final Pattern _methodCallPattern = Pattern.compile(
		"[ \t]*\\w+(?:\\.\\w+)?\\((.*?)\\);\n", Pattern.DOTALL);
	private static final Pattern _parametersPattern = Pattern.compile(
		"(\\s*\\w+ = ['\"].*?['\"],?)(\\s|\\Z)");

}