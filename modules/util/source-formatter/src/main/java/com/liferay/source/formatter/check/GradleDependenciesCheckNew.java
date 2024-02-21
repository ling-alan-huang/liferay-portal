/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.GradleSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.GradleBuildFile;
import com.liferay.source.formatter.util.GradleDependency;
import com.liferay.source.formatter.util.GradleMethodDependency;

/**
 * @author Alan Huang
 */
public class GradleDependenciesCheckNew extends BaseFileCheck {

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

			content = _formatDependencies(
				content, SourceUtil.getIndent(dependenciesBlock), dependenciesBlock,
				releasePortalAPIVersion);

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

//			if (dependency instanceof ExcludeRuleGradleDependency) {
//				String dependencyString = dependency.toString();
//
//				String[] lines = dependencyString.split("\n");
//
//				for (String line : lines) {
//					sb.append(indent);
//					sb.append("\t");
//					sb.append(line);
//					sb.append("\n");
//				}
//			}
//			else {
				sb.append(indent);
				sb.append("\t");
				sb.append(dependency.toString());
				sb.append("\n");
//			}
		}
		
		return StringUtil.replace(content, dependencies, sb.toString());
	}

	private String _sortDependencyAttributes(String dependency) {
		Matcher matcher = _dependencyPattern.matcher(dependency);

		if (!matcher.find()) {
			return dependency;
		}

		StringBundler sb = new StringBundler();

		sb.append(matcher.group(1));
		sb.append(StringPool.SPACE);

		Map<String, String> attributesMap = new TreeMap<>();

		matcher = _dependencyAttributesPattern.matcher(dependency);

		while (matcher.find()) {
			attributesMap.put(matcher.group(1), matcher.group(2));
		}

		for (Map.Entry<String, String> entry : attributesMap.entrySet()) {
			sb.append(entry.getKey());
			sb.append(": ");
			sb.append(entry.getValue());
			sb.append(", ");
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}


	private static final String _RELEASE_PORTAL_API_VERSION_KEY =
		"releasePortalAPIVersion";

	private static final Pattern _dependencyAttributesPattern = Pattern.compile(
		"(\\w+): ((\"?)[\\w.-]+\\3)");
	private static final Pattern _dependencyPattern = Pattern.compile(
		"^(\\w+) (\\w+: (\"?)[\\w.-]+\\3(, )?)+$");
	private static final Pattern _incorrectGroupNameVersionPattern =
		Pattern.compile(
			"(^[^\\s]+)\\s+\"([^:]+?):([^:]+?):([^\"]+?)\"(.*?)",
			Pattern.DOTALL);
	private static final Pattern _incorrectWhitespacePattern = Pattern.compile(
		"(:|\",)[^ \n]");

	private class GradleDependencyComparator
	implements Comparator<GradleDependency>, Serializable {

	@Override
	public int compare(
		GradleDependency dependency1, GradleDependency dependency2) {

		String dependencyString1 = dependency1.toString();

		String dependencyString2 = dependency2.toString();

//		if (dependency1 instanceof ExcludeRuleGradleDependency) {
//			dependencyString1 = dependency1.toGAVString();
//		}
//
//		if (dependency2 instanceof ExcludeRuleGradleDependency) {
//			dependencyString2 = dependency2.toGAVString();
//		}

		String configuration1 = dependency1.getConfiguration();

		String configuration2 = dependency2.getConfiguration();

		if ((dependency1 instanceof GradleMethodDependency) ||
			(dependency2 instanceof GradleMethodDependency) ||
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