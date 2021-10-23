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

package com.liferay.source.formatter;

import com.liferay.poshi.core.elements.PoshiElement;
import com.liferay.poshi.core.elements.PoshiNodeFactory;
import com.liferay.poshi.core.util.Dom4JUtil;
import com.liferay.poshi.core.util.FileUtil;
import com.liferay.source.formatter.checks.util.SourceUtil;
import com.liferay.source.formatter.util.DebugUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.util.NodeComparator;

/**
 * @author Hugo Huijser
 */
public class PoshiSourceProcessor extends BaseSourceProcessor {

	@Override
	protected List<String> doGetFileNames() throws IOException {
		return getFileNames(new String[0], getIncludes());
	}

	@Override
	protected String[] doGetIncludes() {
		return _INCLUDES;
	}

	@Override
	protected File format(
			File file, String fileName, String absolutePath, String content)
		throws Exception {

		if (SourceUtil.isXML(content)) {
			return file;
		}

		return super.format(file, fileName, absolutePath, content);
	}

	@Override
	protected String parse(
			File file, String fileName, String content,
			Set<String> modifiedMessages)
		throws Exception {

		PoshiElement poshiElement = _getPoshiElement(fileName);

		String newContent = poshiElement.toPoshiScript();

		Element xmlElement = _getDom4JElement(fileName);

		_assertEqualElements(
			poshiElement, xmlElement,
			"Poshi script syntax does not translate to Poshi XML");

		if (!content.equals(newContent)) {
			modifiedMessages.add(file.toString() + " (PoshiParser)");

			SourceFormatterArgs sourceFormatterArgs = getSourceFormatterArgs();

			if (sourceFormatterArgs.isShowDebugInformation()) {
				DebugUtil.printContentModifications(
					"PoshiParser", fileName, content, newContent);
			}
		}

		return newContent;
	}

	private void _assertEqualElements(
			Element actualElement, Element xmlElement, String errorMessage)
		throws Exception {

		NodeComparator nodeComparator = new NodeComparator();

		int compare = nodeComparator.compare(actualElement, xmlElement);

		if (compare != 0) {
			String actual = Dom4JUtil.format(actualElement);
			String expected = Dom4JUtil.format(xmlElement);

			errorMessage = _getErrorMessage(actual, expected, errorMessage);

			throw new Exception(errorMessage);
		}
	}

	private Element _getDom4JElement(String fileName) throws Exception {
		String fileContent = FileUtil.read(fileName);

		Document document = Dom4JUtil.parse(fileContent);

		Element rootElement = document.getRootElement();

		Dom4JUtil.removeWhiteSpaceTextNodes(rootElement);

		return rootElement;
	}

	private String _getErrorMessage(
			String actual, String expected, String errorMessage)
		throws Exception {

		StringBuilder sb = new StringBuilder();

		sb.append(errorMessage);
		sb.append("\n\nExpected:\n");
		sb.append(expected);
		sb.append("\n\nActual:\n");
		sb.append(actual);

		return sb.toString();
	}

	private File _getFile(String absolutePath) {
		return new File(absolutePath);
	}

	private PoshiElement _getPoshiElement(String absolutePath)
		throws Exception {

		return (PoshiElement)PoshiNodeFactory.newPoshiNodeFromFile(
			FileUtil.getURL(_getFile(absolutePath)));
	}

	private static final String[] _INCLUDES = {
		"**/*.function", "**/*.macro", "**/*.testcase"
	};

}