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

package com.liferay.source.formatter.gradle;

import java.util.List;

import com.liferay.source.formatter.upgrade.GradleDependency;

/**
 * @author Seiphon Wang
 */
public class ExcludeRuleGradleDependency extends GradleDependency {

	public ExcludeRuleGradleDependency(
		String configuration, String group, String name, String version,
		List<GradleDependency> excludedDependencies) {

		super(configuration, group, name, version);

		_excludedDependencies = excludedDependencies;
	}

	public ExcludeRuleGradleDependency(
		String configuration, String group, String name, String version,
		int lineNumber, int lastLineNumber,
		List<GradleDependency> excludedDependencies) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);

		_excludedDependencies = excludedDependencies;
	}

	public List<GradleDependency> getExcludedDependencies() {
		return _excludedDependencies;
	}

	private List<GradleDependency> _excludedDependencies ;
}
