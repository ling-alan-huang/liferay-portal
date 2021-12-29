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
import com.liferay.portal.kernel.util.Validator;
import com.liferay.poshi.core.elements.PoshiElement;
import com.liferay.poshi.core.elements.PoshiNodeFactory;
import com.liferay.poshi.core.script.PoshiScriptParserException;
import com.liferay.poshi.core.util.Dom4JUtil;
import com.liferay.poshi.core.util.FileUtil;
import com.liferay.source.formatter.checks.util.SourceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

/**
 * @author Alan Huang
 */
public class PoshiVariableNameCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException, PoshiScriptParserException, DocumentException {

		if (SourceUtil.isXML(content) || (!fileName.endsWith(".macro") && !fileName.endsWith(".macro"))) {
			return content;
		}

		File file = new File(fileName);

		PoshiElement poshiElement =
				(PoshiElement)PoshiNodeFactory.newPoshiNodeFromFile(
					FileUtil.getURL(file));

		String poshiElementSyntax = Dom4JUtil.format(poshiElement);

		Document document = SourceUtil.readXML(poshiElementSyntax);

		_parseDocument(fileName, document.getRootElement());

		return content;
	}

	private void _parseDocument(String fileName, Element rootElement) {
		if (rootElement == null) {
			return;
		}


		for (Element commandElement :
			(List<Element>)rootElement.elements("command")) {

			String commandName = commandElement.attributeValue("name");

			List<Map<String, String>> finderColumns = new ArrayList<>();

			for (Element varElement :
					(List<Element>)commandElement.elements("var")) {

				_checkVariableName(commandName, varElement.attributeValue("name"));
//				String variableName = varElement.attributeValue(
//					"name");
//				System.out.println(variableName);
			}
			
			for (Element forElement :
				(List<Element>)commandElement.elements("for")) {

				_checkVariableName(commandName, forElement.attributeValue("param"));

//				String paramName = forElement.attributeValue("param");
//				System.out.println(paramName);
				
				for (Element varElement :
					(List<Element>)forElement.elements("var")) {

					_checkVariableName(commandName, varElement.attributeValue("name"));
				}

			}
			
			for (Element executeElement :
				(List<Element>)commandElement.elements("execute")) {

				for (Element varElement :
					(List<Element>)executeElement.elements("var")) {

					_checkVariableName(commandName, varElement.attributeValue("name"));
				}

			}


//			_checkFinderName(
//				fileName, entityName, finderName, finderColumns);
		}
	}
	
	private void _checkVariableName(String parentName, String variableName) {
		System.out.println(parentName + "#" + variableName);

	}

}