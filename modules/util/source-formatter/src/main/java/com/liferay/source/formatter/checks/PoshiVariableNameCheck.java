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

import com.liferay.petra.string.StringBundler;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PoshiVariableNameCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith(".macro")) {
			return content;
		}

		Matcher matcher = _variableDefinitionPattern.matcher(content);

		while (matcher.find()) {
			String variableName = matcher.group(2);

			if (!variableName.matches(_CAMEL_CASE_PATTERN)) {
				addMessage(
					fileName,
					StringBundler.concat(
						"Variable '", variableName,
						"' must match camelCase pattern  '",
						_CAMEL_CASE_PATTERN, "'"),
					getLineNumber(content, matcher.start()));
			}
		}

		return content;
	}

	private static final String _CAMEL_CASE_PATTERN = "[a-z]+(_?[A-Z][a-z]+)*";

	private static final Pattern _variableDefinitionPattern = Pattern.compile(
		"\\bvar( .+)? (.+)(?= [:=])");

}