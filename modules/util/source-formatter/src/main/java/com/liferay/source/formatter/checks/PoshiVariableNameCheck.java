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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.poshi.core.elements.PoshiElement;
import com.liferay.poshi.core.elements.PoshiNodeFactory;
import com.liferay.poshi.core.script.PoshiScriptParserException;
import com.liferay.poshi.core.util.Dom4JUtil;
import com.liferay.poshi.core.util.FileUtil;
import com.liferay.source.formatter.checks.util.SourceUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.regex.Matcher;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * @author Alan Huang
 */
public class PoshiVariableNameCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws DocumentException, IOException, PoshiScriptParserException {

		if (SourceUtil.isXML(content) ||
			(!fileName.endsWith(".macro") && !fileName.endsWith(".testcase"))) {

			return content;
		}

		File file = new File(fileName);

		PoshiElement poshiElement =
			(PoshiElement)PoshiNodeFactory.newPoshiNodeFromFile(
				FileUtil.getURL(file));

		String poshiElementSyntax = Dom4JUtil.format(poshiElement);

		Document document = SourceUtil.readXML(poshiElementSyntax);

		_parseElements(fileName, StringPool.BLANK, document.getRootElement());

		return content;
	}

	private void _checkVariableName(
			String fileName, String commandName, String executeName, String variableName) {

		String message = "";
		if (Validator.isNull(executeName)) {
//			System.out.println(commandName + "#" + variableName);
			message = commandName;
		}
		else {
//			System.out.println(
//				commandName + "#" + executeName + "#" + variableName);
			message = commandName + "#" + executeName;
		}

		String firstChar = variableName.substring(0, 1);
		
		if (!firstChar.matches("[a-z]") &&
				!variableName.matches("("+StringUtil.merge(_ALL_CAPS_STRINGS, StringPool.PIPE)  +")"+ ".*")) {
			addMessage(
					fileName,
					StringBundler.concat(
						"Variable '", variableName,
						"' in '", message, 
						"' should start with a lowercase letter"
					));
			
			return;

		}

//		String trimmedName = StringUtil.replace(variableName,
//				StringPool.UNDERLINE, StringPool.BLANK);

		String fixedVariableName = variableName;
		
		for (String allCapsString : _ALL_CAPS_STRINGS) {
			fixedVariableName = fixedVariableName.replaceAll(allCapsString, allCapsString.toLowerCase());
		}
		
		String[] words = StringUtil.split(fixedVariableName, StringPool.UNDERLINE);
		
		for (String word : words) {
			if (!word.matches(_CAMEL_CASE_PATTERN)) {
				addMessage(
						fileName,
						StringBundler.concat(
								"Variable '", variableName,
								"' in '", message, 
								"' must match camelCase pattern '",
								_CAMEL_CASE_PATTERN, "'")
						);
			}
		}
	}

	private void _parseElements(String fileName, String commandName, Element parentElement) {
		List<Element> elements = parentElement.elements();

		for (Element element : elements) {
			String elementName = element.getName();

			if (elementName.equals("command")) {
				commandName = element.attributeValue("name");
			}
			else if (elementName.equals("var")) {
				Element variableParentElement = element.getParent();

				String variableParentElementName =
					variableParentElement.getName();
				String executeName = "";

				if (variableParentElementName.equals("execute")) {
					String className = variableParentElement.attributeValue(
						"class");
					String methodName = variableParentElement.attributeValue(
						"method");

					if (className.equals(methodName)) {
						executeName = className;
					}
					else {
						executeName =
							variableParentElement.attributeValue("class") +
								"." +
									variableParentElement.attributeValue(
										"method");
					}
				}

				_checkVariableName(
						fileName, commandName, executeName, element.attributeValue("name"));
			}

			_parseElements(fileName, commandName, element);
		}
	}
		private static final String[] _ALL_CAPS_STRINGS = {
				"PK",
				"XML",
				"ID",
				"URL",
			};

	private static final String _CAMEL_CASE_PATTERN =
//			"[a-z]+((_(URL[A-Z]|[a-z]*))?(([A-Z]|URL[A-Z])[a-z]+)*)*\\d*";
//			"(\\d+|(" + StringUtil.merge(_ALL_CAPS_STRINGS, StringPool.PIPE) + "|([a-z]+(\\d*)(([A-Z])?|" + StringUtil.merge(_ALL_CAPS_STRINGS, StringPool.PIPE) + "([A-Z])?)*)+)\\d*)";
			"([a-z]+\\d*([A-Z])?)+";

}