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
import com.liferay.petra.string.StringPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PythonImportsCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		return _formatImports(content);
	}

	private String _formatImports(String content) {
		Matcher matcher = _pythonImportPattern.matcher(content);

		List<String> importNames = new ArrayList<>();

		int endPosition = 0;
		int startPosition = -1;

		while (matcher.find()) {
			importNames.add(matcher.group(1));

			if (startPosition == -1) {
				startPosition = matcher.start() - 1;
			}

			endPosition = matcher.end();
		}

		Collections.sort(importNames);

		StringBundler sb = new StringBundler((importNames.size() * 3) + 1);

		sb.append(StringPool.NEW_LINE);

		for (String importName : importNames) {
			sb.append(StringPool.NEW_LINE);
			sb.append("import ");
			sb.append(importName);
		}

		return content.substring(0, startPosition) + sb.toString() +
			content.substring(endPosition);
	}

	private static final Pattern _pythonImportPattern = Pattern.compile(
		"\n+import (.*)");

}