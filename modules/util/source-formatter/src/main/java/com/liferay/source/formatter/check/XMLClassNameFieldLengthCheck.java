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

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class XMLClassNameFieldLengthCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws DocumentException, IOException {

		if (fileName.endsWith("/portlet-model-hints.xml")) {
			_checkClassNamFieldLength(fileName, content);
		}

		return content;
	}

	private void _checkClassNamFieldLength(String fileName, String content)
		throws DocumentException {

		Document document = SourceUtil.readXML(content);

		Element rootElement = document.getRootElement();

		if (rootElement == null) {
			return;
		}

		List<Element> childModelElements = rootElement.elements("model");

		for (Element modelElement : childModelElements) {
			String modelClassName = modelElement.attributeValue("name");

			List<Element> childFieldElements = modelElement.elements("field");

			for (Element fieldElements : childFieldElements) {
				String fieldName = fieldElements.attributeValue("name");

				if (!StringUtil.equals(fieldName, "className")) {
					continue;
				}

				List<Element> fieldChildElements = fieldElements.elements();

				for (Element fieldChildElement : fieldChildElements) {
					if (!StringUtil.equals(
							fieldChildElement.getName(), "hint")) {

						continue;
					}

					String value = fieldChildElement.attributeValue("name");

					if (StringUtil.equals(value, "max-length")) {
						value = fieldChildElement.getStringValue();

						if (Validator.isNull(value)) {
							continue;
						}

						if (GetterUtil.getInteger(value) > 75) {
							_checkExistInServiceXML(fileName, modelClassName);
						}
					}
				}
			}
		}
	}

	private void _checkExistInServiceXML(String fileName, String fullClassName)
		throws DocumentException {

		int pos = fileName.lastIndexOf("/src/main");

		if (pos == -1) {
			return;
		}

		String filePath = fileName.substring(0, pos) + "/service.xml";

		File file = new File(filePath);

		if (!file.exists()) {
			return;
		}

		Document document = SourceUtil.readXML(file);

		Element rootElement = document.getRootElement();

		if (rootElement == null) {
			return;
		}

		List<Element> childEntityElements = rootElement.elements("entity");

		for (Element entityElement : childEntityElements) {
			String className = entityElement.attributeValue("name");

			if (!fullClassName.endsWith("." + className)) {
				continue;
			}

			List<Element> childColumnElements = entityElement.elements(
				"column");

			for (Element columnElement : childColumnElements) {
				String value = columnElement.attributeValue("name");

				if (StringUtil.equals(value, "className")) {
					addMessage(
						fileName,
						"Filed 'className' max length may be not longer than " +
							"75 in model " + fullClassName);
				}
			}
		}
	}

}