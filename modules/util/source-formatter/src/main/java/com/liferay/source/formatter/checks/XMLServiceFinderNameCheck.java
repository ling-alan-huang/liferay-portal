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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.DocumentException;

/**
 * @author Alan Huang
 */
public class XMLServiceFinderNameCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws DocumentException {

		if (!fileName.endsWith("/service.xml")) {
			return content;
		}

		Matcher matcher1 = _finderPattern.matcher(content);

		String finder = StringPool.BLANK;
		String finderName = StringPool.BLANK;

		while (matcher1.find()) {
			finder = matcher1.group();

			finderName = finder.replaceFirst(
				"(?s).*?<finder .*?name=\"(.+?)\".*</finder>", "$1");

			List<Map<String, String>> finderColumns = new ArrayList<>();

			for (String line : StringUtil.splitLines(finder)) {
				line = line.trim();

				if (!line.startsWith("<finder-column")) {
					continue;
				}

				Matcher matcher2 = _finderColumnAttributesPattern.matcher(line);

				Map<String, String> attributesMap = new LinkedHashMap<>();

				while (matcher2.find()) {
					attributesMap.put(matcher2.group(1), matcher2.group(2));
				}

				finderColumns.add(attributesMap);
			}

			if (finderColumns.isEmpty()) {
				continue;
			}

			String newFinderName = _generateFinderName(finderColumns);

			if (Validator.isNotNull(newFinderName) &&
				!finderName.equals(newFinderName)) {

				return StringUtil.replaceFirst(
					content, "name=\"" + finderName + "\"",
					"name=\"" + newFinderName + "\"", matcher1.start());
			}
		}

		return content;
	}

	private String _generateFinderName(
		List<Map<String, String>> finderColumns) {

		String newFinderName = StringPool.BLANK;

		for (Map<String, String> finderColumn : finderColumns) {
			if ((finderColumns.size() == 1) &&
				!finderColumn.containsKey("comparator")) {

				return TextFormatter.format(
					finderColumn.get("name"), TextFormatter.G);
			}

			if (!finderColumn.containsKey("name")) {
				continue;
			}

			String finderColumnName = finderColumn.get("name");

			finderColumnName = TextFormatter.format(
				finderColumnName, TextFormatter.G);

			finderColumnName = finderColumnName.substring(0, 1);

			if (finderColumn.containsKey("comparator")) {
				newFinderName += _comparatorNamesMap.get(
					finderColumn.get("comparator"));
			}

			newFinderName =
				newFinderName + finderColumnName + StringPool.UNDERLINE;
		}

		return newFinderName.substring(0, newFinderName.length() - 1);
	}

	private static final Map<String, String> _comparatorNamesMap =
		HashMapBuilder.put(
			"!=", "Not"
		).put(
			"&gt;", "Gt"
		).put(
			"&lt;", "Lt"
		).put(
			"is", "Is"
		).put(
			"LIKE", "Like"
		).build();
	private static final Pattern _finderColumnAttributesPattern =
		Pattern.compile("([\\w-]+)=\"(.*?)\"");
	private static final Pattern _finderPattern = Pattern.compile(
		"\n\t+<finder .+?>.+?</finder>", Pattern.DOTALL);

}