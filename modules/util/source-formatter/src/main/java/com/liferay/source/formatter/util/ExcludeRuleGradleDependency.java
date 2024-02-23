/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import com.liferay.petra.string.StringBundler;

import java.io.Serializable;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Alan Huang
 */
public class ExcludeRuleGradleDependency extends GradleDependency {

	public ExcludeRuleGradleDependency(
		String configuration, String group, String name, String version,
		int lineNumber, int lastLineNumber,
		Set<GradleDependency> excludedDependencies) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);

		_excludedDependencies = excludedDependencies;
	}

	public ExcludeRuleGradleDependency(
		String configuration, String group, String name, String version,
		Set<GradleDependency> excludedDependencies) {

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

	public Set<GradleDependency> getExcludedDependencies() {
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
		sb.append("(group: ");
		sb.append(getGroup());
		sb.append(", name: ");
		sb.append(getName());

		if (getVersion() != null) {
			sb.append(", version: ");
			sb.append(getVersion());
		}

		sb.append(") {\n");

		for (GradleDependency excludeDependency : _excludedDependencies) {
			sb.append("\t");
			sb.append(excludeDependency.getConfiguration());

			sb.append(" group: ");
			sb.append(excludeDependency.getGroup());

			if (excludeDependency.getName() != null) {
				sb.append(", module: ");
				sb.append(excludeDependency.getName());
			}

			sb.append("\n");
		}

		sb.append("}");

		return sb.toString();
	}

	private Set<GradleDependency> _excludedDependencies = new TreeSet<>(
		new ExcludRuleDependencyComparator());

	@SuppressWarnings("serial")
	private class ExcludRuleDependencyComparator
		implements Comparator<GradleDependency>, Serializable {

		@Override
		public int compare(
			GradleDependency dependency1, GradleDependency dependency2) {

			String configuration1 = dependency1.getConfiguration();

			String configuration2 = dependency2.getConfiguration();

			String dependencyString1 = dependency1.toString();

			String dependencyString2 = dependency2.toString();

			if (!configuration1.equals(configuration2)) {
				return configuration1.compareTo(configuration2);
			}

			String group1 = dependency1.getGroup();
			String group2 = dependency2.getGroup();

			if ((group1 != null) && (group2 != null)) {
				if (!group1.equals(group2)) {
					return group1.compareTo(group2);
				}

				String name1 = dependency1.getName();
				String name2 = dependency2.getName();

				if ((name1 != null) && (name2 != null)) {
					if (!name1.equals(name2)) {
						return name1.compareTo(name2);
					}

					int length1 = dependencyString1.length();
					int length2 = dependencyString2.length();

					if (length1 == length2) {
						return 0;
					}
				}
			}

			return dependencyString1.compareTo(dependencyString2);
		}

	}

}