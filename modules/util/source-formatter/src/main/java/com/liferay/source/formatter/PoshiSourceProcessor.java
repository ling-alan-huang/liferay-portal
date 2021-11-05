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
import com.liferay.poshi.core.script.PoshiScriptParserException;
import com.liferay.poshi.core.util.Dom4JUtil;
import com.liferay.poshi.core.util.FileUtil;
import com.liferay.source.formatter.checks.util.SourceUtil;
import com.liferay.source.formatter.util.DebugUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
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

		if (fileName.endsWith("JSONWebcontentUtil.macro") || fileName.endsWith("Kaleodesigner.testcase")) {
			return content;
		}
		String orignalPoshiSyntax = _generatePoshiXMLFile(fileName);
		
		if (orignalPoshiSyntax.length() == 0) {
			return content;
		}
		
		
		Element expectedElement = _getDom4JElement(orignalPoshiSyntax);
		
		PoshiElement actualElement = _getPoshiElement(fileName);

			_assertEqualElements(
					fileName, actualElement, expectedElement,
					"Poshi script syntax does not translate to Poshi XML");

		String newContent = actualElement.toPoshiScript();

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
			String fileName, Element actualElement, Element expectedElement, String errorMessage)
		throws Exception {

		NodeComparator nodeComparator = new NodeComparator();

		int compare = nodeComparator.compare(actualElement, expectedElement);

		if (compare != 0) {
			String actual = Dom4JUtil.format(actualElement);
			String expected = Dom4JUtil.format(expectedElement);

//			throw new Exception(errorMessage);
			System.out.println("====" + fileName);
		}
	}

	private Element _getDom4JElement(String orignalPoshiSyntax) throws Exception {

		Document document = Dom4JUtil.parse(orignalPoshiSyntax);

		Element rootElement = document.getRootElement();

		Dom4JUtil.removeWhiteSpaceTextNodes(rootElement);

		return rootElement;
	}

	private String _generatePoshiXMLFile(String filePath)
			throws PoshiScriptParserException {

			try {
				URL url = FileUtil.getURL(new File(filePath));

				PoshiElement poshiElement =
					(PoshiElement)PoshiNodeFactory.newPoshiNodeFromFile(url);

				
					return Dom4JUtil.format(
						poshiElement
					);
			}
			catch (IOException ioException) {
				ioException.printStackTrace();
			}
			return "";
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