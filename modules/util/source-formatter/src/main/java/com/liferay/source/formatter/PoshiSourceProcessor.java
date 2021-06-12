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
import com.liferay.poshi.core.util.FileUtil;
import com.liferay.source.formatter.checks.util.SourceUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;

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

		testPoshiScriptFunctionToXML(absolutePath);

		return super.format(file, fileName, absolutePath, content);
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

	private static final String[] _INCLUDES = {
		"**/*.function", "**/*.macro", "**/*.testcase"
	};

}