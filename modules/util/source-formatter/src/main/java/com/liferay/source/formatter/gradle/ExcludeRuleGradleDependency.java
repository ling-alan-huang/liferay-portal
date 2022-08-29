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

import com.liferay.petra.string.StringBundler;
import com.liferay.source.formatter.upgrade.GradleDependency;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Seiphon Wang
 */
public class ExcludeRuleGradleDependency extends GradleDependency {

	public ExcludeRuleGradleDependency(
		String configuration, String group, String name, String version,
		int lineNumber, int lastLineNumber,
		List<GradleDependency> excludedDependencies) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);

		_excludedDependencies = excludedDependencies;
	}

	public ExcludeRuleGradleDependency(
		String configuration, String group, String name, String version,
		List<GradleDependency> excludedDependencies) {

		super(configuration, group, name, version);

		_excludedDependencies = excludedDependencies;
	}

	public void addExcludeDependency(GradleDependency dependency) {
		_excludedDependencies.add(dependency);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof ExcludeRuleGradleDependency) {
			ExcludeRuleGradleDependency excludeRuleDependency =
				(ExcludeRuleGradleDependency)object;

			if (!Objects.equals(
					getConfiguration(),
					excludeRuleDependency.getConfiguration()) ||
				!Objects.equals(getGroup(), excludeRuleDependency.getGroup()) ||
				!Objects.equals(getName(), excludeRuleDependency.getName())) {

				return false;
			}

			return _excludedDependencies.equals(
				excludeRuleDependency.getExcludedDependencies());
		}

		return super.equals(object);
	}

	public List<GradleDependency> getExcludedDependencies() {
		return _excludedDependencies;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			getConfiguration(), getGroup(), getName(), _excludedDependencies);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append(getConfiguration());
		sb.append("(group: \"");
		sb.append(getGroup());
		sb.append("\", name: \"");
		sb.append(getName());
		sb.append("\"");

		if (getVersion() != null) {
			sb.append(", version: \"");
			sb.append(getVersion());
			sb.append("\"");
		}

		sb.append(") {\n");

		for (GradleDependency excludeDependency : _excludedDependencies) {
			sb.append("\t");
			sb.append(
				MessageFormat.format(
					"{0} group: \"{1}\", module: \"{2}\"",
					excludeDependency.getConfiguration(),
					excludeDependency.getGroup(), excludeDependency.getName()));

			sb.append("\n");
		}

		sb.append("}");

		return sb.toString();
	}

	private List<GradleDependency> _excludedDependencies = new ArrayList<>();

}