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

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.GradleSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.gradle.ExcludeRuleGradleDependency;
import com.liferay.source.formatter.gradle.MethodGradleDependency;
import com.liferay.source.formatter.upgrade.GradleBuildFile;
import com.liferay.source.formatter.upgrade.GradleDependency;

import java.io.Serializable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 * @author Peter Shin
 */
public class GradleDependenciesCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		List<String> dependenciesBlocks =
			GradleSourceUtil.getDependenciesBlocks(content);

		if (dependenciesBlocks.isEmpty()) {
			return content;
		}

		String releasePortalAPIVersion = getAttributeValue(
			_RELEASE_PORTAL_API_VERSION_KEY, absolutePath);

		for (String dependenciesBlock : dependenciesBlocks) {
			int x = dependenciesBlock.indexOf("\n");
			int y = dependenciesBlock.lastIndexOf("\n");

			if (x == y) {
				continue;
			}

			if (isAttributeValue(
					_CHECK_TEST_INTEGRATION_COMPILE_DEPENDENCIES_KEY,
					absolutePath)) {

				content = _formatTestIntegrationCompileDependencies(
					content, dependenciesBlock.substring(x, y + 1),
					_petraPattern);
				content = _formatTestIntegrationCompileDependencies(
					content, dependenciesBlock.substring(x, y + 1),
					_portalKernelPattern);
			}

			content = _formatDependencies(
				content, SourceUtil.getIndent(dependenciesBlock),
				dependenciesBlock, releasePortalAPIVersion);

			if (isAttributeValue(_CHECK_PETRA_DEPENDENCIES_KEY, absolutePath) &&
				absolutePath.contains("/modules/core/petra/")) {

				_checkPetraDependencies(
					fileName, content, dependenciesBlock.substring(x, y + 1));
			}

			_checkCommerceDependencies(
				fileName, absolutePath, content,
				dependenciesBlock.substring(x, y + 1),
				getAttributeValues(
					_ALLOWED_COMMERCE_DEPENDENCIES_MODULE_PATH_NAMES,
					absolutePath));

			if (isAttributeValue(
					_CHECK_REST_CLIENT_DEPENDENCIES_KEY, absolutePath)) {

				_checkRestClientDependencies(
					fileName, content, dependenciesBlock.substring(x, y + 1));
			}
		}

		return content;
	}

	private void _checkCommerceDependencies(
		String fileName, String absolutePath, String content,
		String dependencies,
		List<String> allowedCommerceDependenciesModulePathNames) {

		if (!isModulesFile(absolutePath) ||
			absolutePath.contains("/commerce/")) {

			return;
		}

		for (String line : StringUtil.splitLines(dependencies)) {
			if (Validator.isNull(line) ||
				!line.matches(
					"\\s*compileOnly project\\(\".*?:apps:commerce.+?\"\\)")) {

				continue;
			}

			for (String allowedCommerceDependenciesModulePathName :
					allowedCommerceDependenciesModulePathNames) {

				if (absolutePath.contains(
						allowedCommerceDependenciesModulePathName)) {

					return;
				}
			}

			addMessage(
				fileName,
				"Modules that are outside of Commerce are not allowed to " +
					"depend on Commerce modules",
				SourceUtil.getLineNumber(content, content.indexOf(line)));
		}
	}

	private void _checkPetraDependencies(
		String fileName, String content, String dependencies) {

		for (String line : StringUtil.splitLines(dependencies)) {
			if (Validator.isNotNull(line) && !line.contains("petra")) {
				addMessage(
					fileName,
					"Only modules/core/petra dependencies are allowed",
					SourceUtil.getLineNumber(content, content.indexOf(line)));
			}
		}
	}

	private void _checkRestClientDependencies(
		String fileName, String content, String dependencies) {

		Matcher matcher = _restClientPattern.matcher(dependencies);

		while (matcher.find()) {
			addMessage(
				fileName,
				"Project dependencies '.*-rest-client' can only be used for " +
					"'testIntegrationCompile'",
				SourceUtil.getLineNumber(
					content, content.indexOf(matcher.group())));
		}
	}

	private String _formatDependencies(
		String content, String indent, String dependenciesBlock,
		String releasePortalAPIVersion) {

		int x = dependenciesBlock.indexOf("\n");
		int y = dependenciesBlock.lastIndexOf("\n");

		if (x == y) {
			return content;
		}

		String dependencies = dependenciesBlock.substring(x, y + 1);

		GradleBuildFile gradleBuildFile = new GradleBuildFile(
			dependenciesBlock);

		if (gradleBuildFile.isDependenciesWithIfElse()) {
			return content;
		}

		List<GradleDependency> gradleDependencies =
			gradleBuildFile.getGradleDependencies();

		Set<GradleDependency> uniqueDependencies = new TreeSet<>(
			new GradleDependencyComparator());

		for (GradleDependency dependency : gradleDependencies) {
			if (Objects.equals(dependency.getConfiguration(), "compileOnly") &&
				Validator.isNotNull(releasePortalAPIVersion)) {

				dependency.setGroup("com.liferay.portal");
				dependency.setName("release.portal.api");
				dependency.setVersion(releasePortalAPIVersion);

				uniqueDependencies.add(dependency);

				continue;
			}

			uniqueDependencies.add(dependency);
		}

		StringBundler sb = new StringBundler();

		String previousConfiguration = null;

		for (GradleDependency dependency : uniqueDependencies) {
			String configuration = dependency.getConfiguration();

			if ((previousConfiguration == null) ||
				!previousConfiguration.equals(configuration)) {

				previousConfiguration = configuration;
				sb.append("\n");
			}

			if (dependency instanceof ExcludeRuleGradleDependency) {
				String dependencyString = dependency.toString();

				String[] lines = dependencyString.split("\n");

				for (String line : lines) {
					sb.append(indent);
					sb.append("\t");
					sb.append(line);
					sb.append("\n");
				}
			}
			else {
				sb.append(indent);
				sb.append("\t");
				sb.append(dependency.toString());
				sb.append("\n");
			}
		}

		return StringUtil.replace(content, dependencies, sb.toString());
	}

	private String _formatTestIntegrationCompileDependencies(
		String content, String dependencies, Pattern pattern) {

		Matcher matcher = pattern.matcher(dependencies);

		if (matcher.find()) {
			return StringUtil.replace(
				content, dependencies,
				StringUtil.removeSubstring(dependencies, matcher.group()));
		}

		return content;
	}

	private static final String
		_ALLOWED_COMMERCE_DEPENDENCIES_MODULE_PATH_NAMES =
			"allowedCommerceDependenciesModulePathNames";

	private static final String _CHECK_PETRA_DEPENDENCIES_KEY =
		"checkPetraDependencies";

	private static final String _CHECK_REST_CLIENT_DEPENDENCIES_KEY =
		"checkRestClientDependencies";

	private static final String
		_CHECK_TEST_INTEGRATION_COMPILE_DEPENDENCIES_KEY =
			"checkTestIntegrationCompileDependencies";

	private static final String _RELEASE_PORTAL_API_VERSION_KEY =
		"releasePortalAPIVersion";

	private static final Pattern _petraPattern = Pattern.compile(
		"testIntegrationCompile project\\(\":core:petra:.*");
	private static final Pattern _portalKernelPattern = Pattern.compile(
		"testIntegrationCompile.* name: \"com\\.liferay\\.portal\\.kernel\".*");
	private static final Pattern _restClientPattern = Pattern.compile(
		"(?<!testIntegrationCompile) project\\(\".*-rest-client\"\\)");

	@SuppressWarnings("serial")
	private class GradleDependencyComparator
		implements Comparator<GradleDependency>, Serializable {

		@Override
		public int compare(
			GradleDependency dependency1, GradleDependency dependency2) {

			String dependencyString1 = dependency1.toString();

			String dependencyString2 = dependency2.toString();

			if (dependency1 instanceof ExcludeRuleGradleDependency) {
				dependencyString1 = dependency1.toGAVString();
			}

			if (dependency2 instanceof ExcludeRuleGradleDependency) {
				dependencyString2 = dependency2.toGAVString();
			}

			String configuration1 = dependency1.getConfiguration();

			String configuration2 = dependency2.getConfiguration();

			if ((dependency1 instanceof MethodGradleDependency) ||
				(dependency2 instanceof MethodGradleDependency) ||
				!configuration1.equals(configuration2)) {

				return dependencyString1.compareTo(dependencyString2);
			}

			String group1 = dependency1.getGroup();
			String group2 = dependency2.getGroup();

			if ((group1 != null) && group1.equals(group2)) {
				String name1 = dependency1.getName();
				String name2 = dependency2.getName();

				if ((name1 != null) && name1.equals(name2)) {
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