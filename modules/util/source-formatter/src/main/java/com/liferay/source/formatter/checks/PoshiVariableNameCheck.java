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

import com.google.common.base.Objects;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
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
import java.util.regex.Pattern;

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

		// TODO Start

		poshiElementSyntax = _fixVariableName(
			file, poshiElement, poshiElementSyntax.trim());

		// TODO End

		Document document = SourceUtil.readXML(poshiElementSyntax);

		_parseElements(fileName, StringPool.BLANK, document.getRootElement());

		return poshiElement.toPoshiScript();
	}

	private void _checkVariableName(
		String fileName, String commandName, String executeName,
		String variableName) {

		String message = "";

		if (Validator.isNull(executeName)) {
			message = commandName;
		}
		else {
			message = commandName + "#" + executeName;
		}

		String firstChar = variableName.substring(0, 1);

		if (!firstChar.matches("[a-z]") &&
			!variableName.matches(
				"(" + StringUtil.merge(_ALL_CAPS_STRINGS, StringPool.PIPE) +
					")" + ".*")) {

			addMessage(
				fileName,
				StringBundler.concat(
					"Variable '", variableName, "' in '", message,
					"' should start with a lowercase letter"));

			return;
		}

		String fixedVariableName = variableName;

//		for (String allCapsString : _ALL_CAPS_STRINGS) {
//			fixedVariableName = fixedVariableName.replaceAll(
//				allCapsString, allCapsString.toLowerCase());
//		}

		String[] words = StringUtil.split(
			fixedVariableName, StringPool.UNDERLINE);

		for (String word : words) {
			if (!word.matches(_CAMEL_CASE_PATTERN)) {
				addMessage(
					fileName,
					StringBundler.concat(
						"Variable '", variableName, "' in '", message,
						"' must match camelCase pattern '", _CAMEL_CASE_PATTERN,
						"'"));
			}
		}
	}

	private String _fixVariableName(
			File file, PoshiElement poshiElement, String poshiElementSyntax)
		throws IOException, PoshiScriptParserException {

		Pattern pattern1 = Pattern.compile("(<var name=\")(.+?)(\"[ >])");

		Matcher matcher1 = pattern1.matcher(poshiElementSyntax);

		while (matcher1.find()) {
			String newVar = matcher1.group(2);

			Pattern pattern2 = Pattern.compile("([A-Z])([A-Z]+)([A-Z][a-z]|$)");

			Matcher matcher2 = pattern2.matcher(newVar);

			StringBuffer sb = new StringBuffer();

			while (matcher2.find()) {
				matcher2.appendReplacement(
					sb,
					matcher2.group(1) +
						matcher2.group(
							2
						).toLowerCase() + matcher2.group(3));
			}

			matcher2.appendTail(sb);

			poshiElementSyntax = StringUtil.replaceFirst(
				poshiElementSyntax, matcher1.group(),
				matcher1.group(1) + sb.toString() + matcher1.group(3),
				matcher1.start() - 1);
		}

		FileUtil.write(file, poshiElementSyntax);

		poshiElement = (PoshiElement)PoshiNodeFactory.newPoshiNodeFromFile(
			FileUtil.getURL(file));
		FileUtil.write(file, poshiElement.toPoshiScript());

		return poshiElementSyntax;
	}

	private void _parseElements(
		String fileName, String commandName, Element parentElement) {

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
					String functionName = variableParentElement.attributeValue(
							"function");

					if (Validator.isNotNull(functionName)) {
						executeName = functionName;
					}
					
					String macroName = variableParentElement.attributeValue(
							"macro");

					if (Validator.isNotNull(macroName)) {
						executeName = macroName;
					}
					
					String className = variableParentElement.attributeValue(
							"class");
					String methodName = variableParentElement.attributeValue(
							"method");

					if (Objects.equal(className, methodName)) {
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
					fileName, commandName, executeName,
					element.attributeValue("name"));
			}

			_parseElements(fileName, commandName, element);
		}
	}

	private static final String[] _ALL_CAPS_STRINGS = {
		"PK", "XML", "ID", "URL"
	};

	private static final String _CAMEL_CASE_PATTERN = "([a-z]+\\d*([A-Z])?)+";

}