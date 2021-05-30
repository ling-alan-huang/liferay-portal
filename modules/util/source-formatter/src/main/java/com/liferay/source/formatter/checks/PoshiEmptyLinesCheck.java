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

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.checks.util.SourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;

import com.liferay.poshi.core.PoshiContext;
import com.liferay.poshi.core.elements.PoshiElement;
import com.liferay.poshi.core.elements.PoshiNodeFactory;
import com.liferay.poshi.core.util.Dom4JUtil;
import com.liferay.poshi.core.util.FileUtil;

import java.io.File;
/**
 * @author Alan Huang
 */
public class PoshiEmptyLinesCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) throws Exception {

		content = content.replaceAll("(\\{\n)\n+(?!\t*/[/\\*])", "$1");
		content = content.replaceAll("(\n\t+[^/\\t].*)\n(\n\t+\\})", "$1$2");
		content = content.replaceAll(
			"(?<!\n)(\n\t(?!else|if)\\w+ \\{)", "\n$1");

		content = content.replaceFirst("(definition \\{\n)(?!\n)", "$1\n");
		content = content.replaceFirst("(?<!\n)(\n\\})$", "\n$1");
		content = content.replaceFirst(
			"(\n\t*[^@\\s].*\n)((\t@.+\n)*\t(function|macro|test) )", "$1\n$2");

		content = _fixMissingEmptyLinesAroundComments(content);

		testPoshiScriptFunctionToXML(absolutePath);
		return content;
	}

	private void testPoshiScriptFunctionToXML(String absolutePath) throws Exception {
		PoshiElement actualElement = _getPoshiElement(absolutePath);

		int a = 0;
	}

	private PoshiElement _getPoshiElement(String absolutePath) throws Exception {
		return (PoshiElement)PoshiNodeFactory.newPoshiNodeFromFile(
			FileUtil.getURL(_getFile(absolutePath)));
	}

	private File _getFile(String absolutePath) {
		return new File(absolutePath);
	}

	private String _fixMissingEmptyLinesAroundComments(String content) {
		int[] multiLineCommentsPositions = SourceUtil.getMultiLinePositions(
			content, _multiLineCommentsPattern);
		int[] multiLineStringPositions = SourceUtil.getMultiLinePositions(
			content, _multiLineStringPattern);

		Matcher matcher = _missingEmptyLineAfterCommentPattern.matcher(content);

		int lineNumber;

		if (matcher.find()) {
			lineNumber = getLineNumber(content, matcher.start(1));

			if (!SourceUtil.isInsideMultiLines(
					lineNumber, multiLineCommentsPositions) &&
				!SourceUtil.isInsideMultiLines(
					lineNumber, multiLineStringPositions)) {

				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.start() + 1);
			}
		}

		matcher = _missingEmptyLineBeforeCommentPattern.matcher(content);

		if (matcher.find()) {
			lineNumber = getLineNumber(content, matcher.start(1));

			if (!SourceUtil.isInsideMultiLines(
					lineNumber, multiLineCommentsPositions) &&
				!SourceUtil.isInsideMultiLines(
					lineNumber, multiLineStringPositions)) {

				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.start() + 1);
			}
		}

		return content;
	}

	private static final Pattern _missingEmptyLineAfterCommentPattern =
		Pattern.compile("\n\t*(//).*\n[\t ]*(?!//)\\S");
	private static final Pattern _missingEmptyLineBeforeCommentPattern =
		Pattern.compile("\n[\t ]*(?!//)\\S.*\n\t*(//)");
	private static final Pattern _multiLineCommentsPattern = Pattern.compile(
		"[ \t]/\\*.*?\\*/", Pattern.DOTALL);
	private static final Pattern _multiLineStringPattern = Pattern.compile(
		"'''.*?'''", Pattern.DOTALL);

}