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

		if (SourceUtil.isXML(content) && !fileName.endsWith(".path")) {

			return content;
		}

		// Check *.path
		if (fileName.endsWith(".path")) {

			// TODO Start
			// Auto Fix
			// return _fixVariableName2(content);
			// TODO End
			
			// Check
			Pattern pattern1 = Pattern.compile("(\\$\\{)([a-zA-Z0-9_]+?)(\\})");

			Matcher matcher1 = pattern1.matcher(content);

			while (matcher1.find()) {
				_checkVariableName(fileName, "", "" , matcher1.group(2));
			}
			
			return content;

		}

		File file = new File(fileName);

		PoshiElement poshiElement =
			(PoshiElement)PoshiNodeFactory.newPoshiNodeFromFile(
				FileUtil.getURL(file));

		String poshiElementSyntax = Dom4JUtil.format(poshiElement);

		// TODO Start
		// Auto Fix

		poshiElementSyntax = _fixVariableName(
			file, poshiElement, poshiElementSyntax.trim());

		poshiElementSyntax = _fixVariableName1(
			file, poshiElement, poshiElementSyntax.trim());

		// TODO End

		Document document = SourceUtil.readXML(poshiElementSyntax);

		_parseElements(fileName, StringPool.BLANK, document.getRootElement());

		return poshiElement.toPoshiScript();
	}

	private void _checkVariableName(
		String fileName, String commandName, String executeName,
		String variableName) {

		String message = commandName;

		
		if (Validator.isNotNull(executeName)) {
			message = message + "#" + executeName;
		}

		String firstChar = variableName.substring(0, 1);

		if (!firstChar.matches("[a-z]")) {

			addMessage(
				fileName,
				StringBundler.concat(
					"Variable '", variableName, "' in '", message,
					"' should start with a lowercase letter"));

			return;
		}

		String[] words = StringUtil.split(
				variableName, StringPool.UNDERLINE);

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

		Pattern pattern1 = Pattern.compile("((?:<var name|<isset var|for param)=\")(.+?)(\"([ >])|/>)");

		Matcher matcher1 = pattern1.matcher(poshiElementSyntax);

		StringBuffer sb1 = new StringBuffer();
		
		while (matcher1.find()) {
			String newVar = matcher1.group(2);

			if (newVar.startsWith("OSGi")) {
				newVar = newVar.replace("OSGi", "osgi");
			}
			
			if (newVar.matches("[A-Z]+")) {
				newVar = newVar.toLowerCase();
			}
			

			Pattern pattern2 = Pattern.compile("([A-Z])([A-Z]+)([A-Z][a-z]|$)");

			Matcher matcher2 = pattern2.matcher(newVar);

			StringBuffer sb2 = new StringBuffer();

			while (matcher2.find()) {
				matcher2.appendReplacement(
					sb2,
					matcher2.group(1) +
						matcher2.group(
							2
						).toLowerCase() + matcher2.group(3));
			}

			matcher2.appendTail(sb2);
			
			newVar =  sb2.toString();

			Pattern pattern3 = Pattern.compile("(_)([A-Z])");

			Matcher matcher3 = pattern3.matcher(newVar);
			
			while (matcher3.find()) {
				newVar = newVar.replaceFirst(matcher3.group(), matcher3.group(1) + matcher3.group(2).toLowerCase());
			}

			Pattern pattern4 = Pattern.compile("^([A-Z])([a-z])(.*)$");

			Matcher matcher4 = pattern4.matcher(newVar);
			
			if (matcher4.find()) {
				newVar = newVar.replaceFirst(matcher4.group(), matcher4.group(1).toLowerCase() + matcher4.group(2) + matcher4.group(3));
			}

			Pattern pattern5 = Pattern.compile("([A-Z])([A-Z]+)(\\d)");

			Matcher matcher5 = pattern5.matcher(newVar);
			
			if (matcher5.find()) {
				newVar = newVar.replaceFirst(matcher5.group(), matcher5.group(1) + matcher5.group(2).toLowerCase() + matcher5.group(3));
			}

			matcher1.appendReplacement(
					sb1,
					matcher1.group(1) + newVar.toString() + matcher1.group(3));

		}
		matcher1.appendTail(sb1);

		FileUtil.write(file, sb1.toString());

		poshiElement = (PoshiElement)PoshiNodeFactory.newPoshiNodeFromFile(
			FileUtil.getURL(file));
		FileUtil.write(file, poshiElement.toPoshiScript());

		return sb1.toString();
	}

	private String _fixVariableName1(
			File file, PoshiElement poshiElement, String poshiElementSyntax)
		throws IOException, PoshiScriptParserException {

		Pattern pattern1 = Pattern.compile("(\\$\\{)([a-zA-Z0-9_]+?)(\\})");

		Matcher matcher1 = pattern1.matcher(poshiElementSyntax);

		StringBuffer sb1 = new StringBuffer();
		
		while (matcher1.find()) {
			String newVar = matcher1.group(2);

			if (newVar.startsWith("OSGi")) {
				newVar = newVar.replace("OSGi", "osgi");
			}
			
			if (newVar.matches("[A-Z]+")) {
				newVar = newVar.toLowerCase();
			}
			

			Pattern pattern2 = Pattern.compile("([A-Z])([A-Z]+)([A-Z][a-z]|$)");

			Matcher matcher2 = pattern2.matcher(newVar);

			StringBuffer sb2 = new StringBuffer();

			while (matcher2.find()) {
				matcher2.appendReplacement(
					sb2,
					matcher2.group(1) +
						matcher2.group(
							2
						).toLowerCase() + matcher2.group(3));
			}

			matcher2.appendTail(sb2);
			
			newVar =  sb2.toString();

			Pattern pattern3 = Pattern.compile("(_)([A-Z])");

			Matcher matcher3 = pattern3.matcher(newVar);
			
			while (matcher3.find()) {
				newVar = newVar.replaceFirst(matcher3.group(), matcher3.group(1) + matcher3.group(2).toLowerCase());
			}

			Pattern pattern4 = Pattern.compile("^([A-Z])([a-z])(.*)$");

			Matcher matcher4 = pattern4.matcher(newVar);
			
			if (matcher4.find()) {
				newVar = newVar.replaceFirst(matcher4.group(), matcher4.group(1).toLowerCase() + matcher4.group(2) + matcher4.group(3));
			}

			Pattern pattern5 = Pattern.compile("([A-Z])([A-Z]+)(\\d)");

			Matcher matcher5 = pattern5.matcher(newVar);
			
			if (matcher5.find()) {
				newVar = newVar.replaceFirst(matcher5.group(), matcher5.group(1) + matcher5.group(2).toLowerCase() + matcher5.group(3));
			}

			matcher1.appendReplacement(
					sb1,
//					matcher1.group(1) + newVar.toString() + matcher1.group(3));
			"\\$\\{" + newVar.toString() + "\\}");

		}
		matcher1.appendTail(sb1);

		FileUtil.write(file, sb1.toString());

		poshiElement = (PoshiElement)PoshiNodeFactory.newPoshiNodeFromFile(
			FileUtil.getURL(file));
		FileUtil.write(file, poshiElement.toPoshiScript());

		return sb1.toString();
	}

	private String _fixVariableName2(String pathContent)
		{

		Pattern pattern1 = Pattern.compile("(\\$\\{)([a-zA-Z0-9_]+?)(\\})");

		Matcher matcher1 = pattern1.matcher(pathContent);

		StringBuffer sb1 = new StringBuffer();
		
		while (matcher1.find()) {
			String newVar = matcher1.group(2);

			if (newVar.startsWith("OSGi")) {
				newVar = newVar.replace("OSGi", "osgi");
			}
			
			if (newVar.matches("[A-Z]+")) {
				newVar = newVar.toLowerCase();
			}
			

			Pattern pattern2 = Pattern.compile("([A-Z])([A-Z]+)([A-Z][a-z]|$)");

			Matcher matcher2 = pattern2.matcher(newVar);

			StringBuffer sb2 = new StringBuffer();

			while (matcher2.find()) {
				matcher2.appendReplacement(
					sb2,
					matcher2.group(1) +
						matcher2.group(
							2
						).toLowerCase() + matcher2.group(3));
			}

			matcher2.appendTail(sb2);
			
			newVar =  sb2.toString();

			Pattern pattern3 = Pattern.compile("(_)([A-Z])");

			Matcher matcher3 = pattern3.matcher(newVar);
			
			while (matcher3.find()) {
				newVar = newVar.replaceFirst(matcher3.group(), matcher3.group(1) + matcher3.group(2).toLowerCase());
			}

			Pattern pattern4 = Pattern.compile("^([A-Z])([a-z])(.*)$");

			Matcher matcher4 = pattern4.matcher(newVar);
			
			if (matcher4.find()) {
				newVar = newVar.replaceFirst(matcher4.group(), matcher4.group(1).toLowerCase() + matcher4.group(2) + matcher4.group(3));
			}

			Pattern pattern5 = Pattern.compile("([A-Z])([A-Z]+)(\\d)");

			Matcher matcher5 = pattern5.matcher(newVar);
			
			if (matcher5.find()) {
				newVar = newVar.replaceFirst(matcher5.group(), matcher5.group(1) + matcher5.group(2).toLowerCase() + matcher5.group(3));
			}

			matcher1.appendReplacement(
					sb1,
//					matcher1.group(1) + newVar.toString() + matcher1.group(3));
			"\\$\\{" + newVar.toString() + "\\}");

		}
		matcher1.appendTail(sb1);

		return sb1.toString();
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
//		"PK", "XML", "ID", "URL"
			""
	};

	private static final String _CAMEL_CASE_PATTERN = "([a-z0-9]+([A-Z])?)+";

}