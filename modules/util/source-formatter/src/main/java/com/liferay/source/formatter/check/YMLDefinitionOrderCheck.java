/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.check.util.YMLSourceUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 * @author Alan Huang
 */
public class YMLDefinitionOrderCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (fileName.endsWith(".travis.yml")) {
			return content;
		}

		String trimmedContent = content.trim();

		if (trimmedContent.startsWith("---") ||
			trimmedContent.endsWith("---")) {

			return content;
		}

		List<String> documents = YMLSourceUtil.splitDocuments(content);

		StringBundler sb = new StringBundler(documents.size() * 2);

		for (String document : documents) {
			sb.append(_sortDefinitions(document));
			sb.append("\n---\n");
		}

		sb.setIndex(sb.index() - 1);

		content = sb.toString();

		content = _sortFeatureFlags(content);

		if (fileName.endsWith("docker-compose.yaml")) {
			content = _sortPorts(content);
		}

		return _sortPathParameters(content);
	}

	private List<String> _combineComments(List<String> definitions) {
		List<String> definitionsList = new ArrayList<>();

		StringBundler sb = new StringBundler();

		String previousDefinition = StringPool.BLANK;

		for (String definition : definitions) {
			if (definition.matches(" *#")) {
				sb.append(definition);
				sb.append("\n");
			}
			else if (previousDefinition.matches(" *#")) {
				sb.append(definition);

				definitionsList.add(sb.toString());

				sb.setIndex(0);
			}
			else {
				definitionsList.add(definition);
			}

			previousDefinition = definition;
		}

		if (sb.index() > 0) {
			definitionsList.add(StringUtil.trimTrailing(sb.toString()));
		}

		return definitionsList;
	}

	private String _getParameterType(String definition) {
		return definition.replaceAll("(?s).*in: (\\S*).*", "$1");
	}

	private int _getParameterTypeWeight(String definitionKey) {
		if (_parameterTypesWeightMap.containsKey(definitionKey)) {
			return _parameterTypesWeightMap.get(definitionKey);
		}

		return -1;
	}

	private String _removeComments(String definition) {
		int y = definition.indexOf("\n");

		if (y == -1) {
			return definition;
		}

		int x = 0;

		String line = definition.substring(x, y);

		while (line.matches(" *#.*")) {
			x = y + 1;

			y = definition.indexOf("\n", x);

			if (y == -1) {
				return definition;
			}

			line = definition.substring(x, y);
		}

		return definition.substring(x);
	}

	private String _sortDefinitions(String content) {
		List<String> definitions = _splitDefinitions(content);

		definitions = _combineComments(definitions);

		Collections.sort(definitions, new DefinitionComparator());

		StringBundler sb1 = new StringBundler(definitions.size() * 2);

		for (String definition : definitions) {
			String[] lines = definition.split("\n");

			if (lines.length == 1) {
				sb1.append(definition);
				sb1.append("\n");

				continue;
			}

			String firstLine = lines[0];

			if (firstLine.endsWith("|")) {
				sb1.append(definition);
				sb1.append("\n");

				continue;
			}

			String secondLine = lines[1];

			if (firstLine.matches(" +-") && secondLine.endsWith("|")) {
				sb1.append(definition);
				sb1.append("\n");

				continue;
			}

			if (firstLine.endsWith(":") && !secondLine.contains(":")) {
				sb1.append(definition);
				sb1.append("\n");

				continue;
			}

			StringBundler sb2 = new StringBundler(lines.length * 2);

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];

				if (i == 0) {
					sb1.append(line);
					sb1.append("\n");

					continue;
				}

				sb2.append(line);
				sb2.append("\n");
			}

			if (sb2.index() > 0) {
				sb2.setIndex(sb2.index() - 1);
			}

			sb1.append(_sortDefinitions(sb2.toString()));
			sb1.append("\n");
		}

		if (sb1.index() > 0) {
			sb1.setIndex(sb1.index() - 1);
		}

		return sb1.toString();
	}

	private String _sortFeatureFlags(String content) {
		int x = -1;

		while (true) {
			x = content.indexOf("featureFlags: ", x + 1);

			if (x == -1) {
				return content;
			}

			String featureFlags = content.substring(x + 14);

			int y = featureFlags.indexOf("\n");

			if (y != -1) {
				featureFlags = featureFlags.substring(0, y);
			}

			String[] array = featureFlags.split(",");

			if (array.length < 2) {
				return content;
			}

			Arrays.sort(array, new NaturalOrderStringComparator());

			String newFeatureFlags = StringUtil.merge(array);

			if (!featureFlags.equals(newFeatureFlags)) {
				return StringUtil.replaceFirst(
					content, featureFlags, newFeatureFlags, x);
			}
		}
	}

	private String _sortPathParameters(String content) {
		Matcher matcher1 = _pathPattern1.matcher(content);

		while (matcher1.find()) {
			String path = matcher1.group();

			String[] lines = path.split("\n", 2);

			Matcher matcher2 = _pathPattern2.matcher(lines[0]);

			Map<String, String> inPathsMap = new LinkedHashMap<>();

			while (matcher2.find()) {
				inPathsMap.put(matcher2.group(1), "");
			}

			int inPathCount = inPathsMap.size();

			Pattern pattern = Pattern.compile(
				"( *-\n( +)in: path(\n\\2.+)*\n){" + inPathCount + "}");

			matcher2 = pattern.matcher(lines[1]);

			while (matcher2.find()) {
				String inPaths = matcher2.group();

				Matcher matcher3 = _pathPattern3.matcher(inPaths);

				while (matcher3.find()) {
					String inPath = matcher3.group();

					inPathsMap.replace(
						inPath.replaceAll("(?s).*name: (\\S+).*", "$1"),
						inPath);
				}

				StringBundler sb = new StringBundler(inPathCount);

				for (Map.Entry<String, String> entry : inPathsMap.entrySet()) {
					sb.append(entry.getValue());
				}

				content = StringUtil.replaceFirst(
					content, inPaths, sb.toString());
			}
		}

		return content;
	}

	private String _sortPorts(String content) {
		Matcher matcher = _portsPattern.matcher(content);

		while (matcher.find()) {
			String indent = matcher.group(1) + StringPool.FOUR_SPACES;

			String ports = matcher.group(2);

			String trimmedPorts = StringUtil.trimLeading(ports);

			trimmedPorts = trimmedPorts.replaceAll(" *-\n +", "");

			String[] portsArray = StringUtil.splitLines(trimmedPorts);

			Arrays.sort(portsArray);

			StringBundler sb = new StringBundler(portsArray.length * 8);

			for (String port : portsArray) {
				sb.append(StringPool.NEW_LINE);
				sb.append(indent);
				sb.append(StringPool.DASH);
				sb.append(StringPool.NEW_LINE);
				sb.append(indent);
				sb.append(StringPool.FOUR_SPACES);
				sb.append(port);
			}

			String newPorts = sb.toString();

			if (!ports.equals(newPorts)) {
				return StringUtil.replaceFirst(
					content, ports, newPorts, matcher.start(2));
			}
		}

		return content;
	}

	private int _sortSpecificDefinitions(
		String definition1, String definition2, String key) {

		String parameter1Type = _getParameterType(definition1);
		String parameter2Type = _getParameterType(definition2);

		Pattern pattern = Pattern.compile(
			"^ *" + key + ": *(\\S*)(\n|\\Z)", Pattern.MULTILINE);

		String value1 = StringPool.BLANK;

		Matcher matcher = pattern.matcher(definition1);

		if (matcher.find()) {
			value1 = matcher.group(1);
		}

		String value2 = StringPool.BLANK;

		matcher = pattern.matcher(definition2);

		if (matcher.find()) {
			value2 = matcher.group(1);
		}

		if (parameter1Type.equals(parameter2Type)) {
			return value1.compareTo(value2);
		}

		return _getParameterTypeWeight(parameter1Type) -
			_getParameterTypeWeight(parameter2Type);
	}

	private List<String> _splitDefinitions(String content) {
		List<String> definitions = new ArrayList<>();

		String[] lines = content.split("\n");

		StringBundler sb = new StringBundler();

		String leadingSpaces = StringPool.BLANK;
		int leadingSpacesLength = 0;

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			if (i == 0) {
				leadingSpaces = SourceUtil.getLeadingSpaces(line);

				leadingSpacesLength = leadingSpaces.length();

				sb.append(line);
				sb.append("\n");

				continue;
			}

			if ((line.length() == 0) || line.matches(" +")) {
				sb.append(line);
				sb.append("\n");

				continue;
			}

			if (line.charAt(leadingSpacesLength) != ' ') {
				if (sb.index() > 0) {
					sb.setIndex(sb.index() - 1);
				}

				definitions.add(sb.toString());

				sb.setIndex(0);
			}

			sb.append(line);
			sb.append("\n");
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);

			definitions.add(sb.toString());
		}

		return definitions;
	}

	private static final Map<String, Integer> _parameterTypesWeightMap =
		HashMapBuilder.put(
			"cookie", 4
		).put(
			"header", 3
		).put(
			"path", 1
		).put(
			"query", 2
		).build();
	private static final Pattern _pathPattern1 = Pattern.compile(
		"(?<=\n)( *)\"([^{}\"]*\\{[^}]+\\}[^{}\"]*){2,}\":(\n\\1 .*)*");
	private static final Pattern _pathPattern2 = Pattern.compile(
		"\\{([^{}]+)\\}");
	private static final Pattern _pathPattern3 = Pattern.compile(
		" *-\n( +)in: path(\n\\1.+)*\n");
	private static final Pattern _portsPattern = Pattern.compile(
		"\n( +)ports:((\n +-\\s+\\d{4}:\\d{4}){2,})");

	private class DefinitionComparator implements Comparator<String> {

		@Override
		public int compare(String definition1, String definition2) {
			String trimmedDefinition1 = StringUtil.trimLeading(definition1);
			String trimmedDefinition2 = StringUtil.trimLeading(definition2);

			if (trimmedDefinition1.matches("( *#.*\n)* *\\{\\{.*") ||
				trimmedDefinition2.matches("( *#.*\n)* *\\{\\{.*") ||
				Validator.isNull(trimmedDefinition1) ||
				Validator.isNull(trimmedDefinition2)) {

				return 0;
			}

			String[] definition1Lines = StringUtil.splitLines(
				_removeComments(definition1));
			String[] definition2Lines = StringUtil.splitLines(
				_removeComments(definition2));

			String trimmedDefinition1Line = definition1Lines[0];
			String trimmedDefinition2Line = definition2Lines[0];

			if (trimmedDefinition1Line.equals(StringPool.DASH) ||
				trimmedDefinition2Line.equals(StringPool.DASH)) {

				if (definition1Lines[1].contains("in: ") &&
					definition2Lines[1].contains("in: ")) {

					return _sortSpecificDefinitions(
						definition1, definition2, "name");
				}

				return 0;
			}

			if (trimmedDefinition1Line.startsWith("in:") ||
				trimmedDefinition2Line.startsWith("in:")) {

				if (trimmedDefinition1Line.startsWith("in:")) {
					return -1;
				}

				return 1;
			}

			String definition1Key = definition1.replaceAll(
				"( *#.*(\\Z|\n))*(.*)", "$3");
			String definition2Key = definition2.replaceAll(
				"( *#.*(\\Z|\n))*(.*)", "$3");

			if (Validator.isNull(definition1Key) ||
				Validator.isNull(definition2Key)) {

				return 0;
			}

			definition1Key = definition1Key.replaceAll("(?s):\n.*", "");
			definition2Key = definition2Key.replaceAll("(?s):\n.*", "");

			return definition1Key.compareTo(definition2Key);
		}

	}

}