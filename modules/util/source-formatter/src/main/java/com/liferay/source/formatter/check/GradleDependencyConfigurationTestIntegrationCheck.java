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

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.GradleSourceUtil;

import java.util.List;

/**
 * @author Alan Huang
 */
public class GradleDependencyConfigurationTestIntegrationCheck
	extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		List<String> blocks = GradleSourceUtil.getDependenciesBlocks(content);

		for (String dependencies : blocks) {
			content = _formatDependencies(content, dependencies);
		}

		return content;
	}

	private String _formatDependencies(String content, String dependencies) {
		int x = dependencies.indexOf("\n");
		int y = dependencies.lastIndexOf("\n");

		if (x == y) {
			return content;
		}

		dependencies = dependencies.substring(x, y + 1);

		for (String oldDependency : StringUtil.splitLines(dependencies)) {
			String configuration = GradleSourceUtil.getConfiguration(
				oldDependency);
			String newDependency = oldDependency;

			if (configuration.equals("testIntegrationCompile")) {
				newDependency = StringUtil.replaceFirst(
					oldDependency, "testIntegrationCompile",
					"testIntegrationImplementation");
			}

			content = StringUtil.replaceFirst(
				content, oldDependency, newDependency);
		}

		return content;
	}

}