/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.GradleSourceUtil;

import java.util.List;

/**
 * @author Alan Huang
 */
public class GradleJakartaCheck extends BaseJakartaTransformerCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		List<String> dependenciesBlocks =
			GradleSourceUtil.getDependenciesBlocks(content);

		for (String dependencies : dependenciesBlocks) {
			content = _fixClassifier(content, dependencies);
		}

		content = _formatDeployDependencies(content);

		content = replace(replacementDashDotMap, content);

		return content;
	}

	private String _fixClassifier(String content, String dependencies) {
		int x = dependencies.indexOf("\n");
		int y = dependencies.lastIndexOf("\n");

		if (x == y) {
			return content;
		}

		dependencies = dependencies.substring(x, y + 1);

		for (String dependency : StringUtil.splitLines(dependencies)) {
			if (dependency.contains(
					"group: \"org.ehcache\", name: \"ehcache\", version: \"3.10.8\"") &&
				!dependency.contains("classifier: \"jakarta\"")) {

				String newDependency = dependency + ", classifier: \"jakarta\"";

				return StringUtil.replaceFirst(
					content, dependency, newDependency);
			}
		}

		return content;
	}

	private String _formatDeployDependencies(String content) {
		int x = content.indexOf("deployDependencies {");

		if (x == -1) {
			return content;
		}

		String deployDependencies = null;

		int y = x + 19;

		while (true) {
			y = content.indexOf(CharPool.CLOSE_CURLY_BRACE, y + 1);

			if (y == -1) {
				return content;
			}

			deployDependencies = content.substring(x, y + 1);

			if (ToolsUtil.getLevel(deployDependencies, "{", "}") == 0) {
				break;
			}
		}

		String newDeployDependencies = deployDependencies;

		newDeployDependencies = StringUtil.replace(
			newDeployDependencies,
			new String[] {
				"cxf-*", "jackson-jaxrs-base-*",
				"jackson-jaxrs-json-provider-*",
				"jackson-module-jaxb-annotations-*", "jakarta.mvc-api",
				"jakarta.ws.rs-api-*", "javax\\.mvc-api", "jaxb-osgi-*"
			},
			new String[] {
				"org.apache.cxf.*", "jackson-jakarta-rs-base-*",
				"jackson-jakarta-rs-json-provider-*",
				"jackson-module-jakarta-xmlbind-annotations-*",
				"jakarta.mvc-api", "jakarta.ws.rs-*", "jakarta\\.mvc-api",
				"com.sun.xml.bind.jaxb.osgi-*"
			});

		if (!deployDependencies.equals(newDeployDependencies)) {
			return content;
		}

		return StringUtil.replaceFirst(
			content, deployDependencies, newDeployDependencies, x);
	}

}