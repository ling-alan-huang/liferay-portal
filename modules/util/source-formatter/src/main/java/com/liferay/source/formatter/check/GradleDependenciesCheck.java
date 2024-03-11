/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.GradleSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.GradleBuildFile;
import com.liferay.source.formatter.util.GradleDependency;
import com.liferay.source.formatter.util.MethodGradleDependency;

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

			String dependencies = dependenciesBlock.substring(x, y + 1);

			if (isAttributeValue(
					_CHECK_TEST_INTEGRATION_IMPLEMENTATION_DEPENDENCIES_KEY,
					absolutePath, true)) {

				content = _formatTestIntegrationImplementationDependencies(
					content, dependencies, _petraPattern);
				content = _formatTestIntegrationImplementationDependencies(
					content, dependencies, _portalKernelPattern);
			}

			content = _formatDependencies(
				content, SourceUtil.getIndent(dependenciesBlock),
				dependenciesBlock, releasePortalAPIVersion);
		}

		return content;
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

			String dependencyString = dependency.toString();

			String[] lines = dependencyString.split("\n");

			for (String line : lines) {
				sb.append(indent);
				sb.append("\t");
				sb.append(line);
				sb.append("\n");
			}
		}

		return StringUtil.replace(content, dependencies, sb.toString());
	}

	private String _formatTestIntegrationImplementationDependencies(
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
		_CHECK_TEST_INTEGRATION_IMPLEMENTATION_DEPENDENCIES_KEY =
			"checkTestIntegrationImplementationDependencies";

	private static final String _RELEASE_PORTAL_API_VERSION_KEY =
		"releasePortalAPIVersion";

	private static final Pattern _petraPattern = Pattern.compile(
		"testIntegrationImplementation project\\(\":core:petra:.*");
	private static final Pattern _portalKernelPattern = Pattern.compile(
		"testIntegrationImplementation.* name: \"com\\.liferay\\.portal\\." +
			"kernel\".*");

	private class GradleDependencyComparator
		implements Comparator<GradleDependency> {

		@Override
		public int compare(
			GradleDependency dependency1, GradleDependency dependency2) {

			String configuration1 = dependency1.getConfiguration();
			String configuration2 = dependency2.getConfiguration();

			if (!configuration1.equals(configuration2)) {
				return configuration1.compareTo(configuration2);
			}
			
			if (dependency1 instanceof GradleDependency && dependency2 instanceof MethodGradleDependency) {
				return -1;
			}
			
			if (dependency1 instanceof MethodGradleDependency && dependency2 instanceof GradleDependency) {
				return 1;
			}

			if (dependency1 instanceof MethodGradleDependency && dependency2 instanceof MethodGradleDependency) {
				MethodGradleDependency methodGradleDependency1 = (MethodGradleDependency)dependency1;
				
				String methodName1 = methodGradleDependency1.getMethodName();
				
				MethodGradleDependency methodGradleDependency2 = (MethodGradleDependency)dependency2;
				
				String methodName2 = methodGradleDependency2.getMethodName();
				
				if (!methodName1.equals(methodName2)) {
					return methodName1.compareTo(methodName2);
				}
				
				// TODO
				
				String s1 = dependency1.toString();
				String s2 = dependency2.toString();
				
				return s1.compareTo(s2);
			}

			Map<String, String> argumentsMap1 = dependency1.getArgumentsMap();
			Map<String, String> argumentsMap2 = dependency2.getArgumentsMap();
			
			Set<String> argumentsKeysSet1 =  argumentsMap1.keySet();
			Set<String> argumentsKeysSet2 =  argumentsMap2.keySet();
			
			Object[] keys1 = argumentsKeysSet1.toArray();
			Object[] keys2 = argumentsKeysSet2.toArray();

			int minSetSize = Math.min(keys1.length, keys2.length);
			
			for (int i = 0; i < minSetSize; i++) {
				String argumentName1 = argumentsMap1.get(keys1[i]);
				String argumentName2 = argumentsMap2.get(keys2[i]);

				if (!argumentName1.equals(argumentName2)) {
					return argumentName1.compareTo(argumentName2);
				}
				
			}
			
			if (minSetSize == keys1.length) {
				return -1;
			}
			else if (minSetSize == keys2.length) {
				return 1;
			}
			
			return 0;
		}

	}

}