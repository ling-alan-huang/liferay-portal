/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;

/**
 * @author Alan Huang
 */
public class GradleJakartaCheck extends BaseJakartaTransformerCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		content = _formatDeployDependencies(content);

		content = replace(replacementDashDotMap, content);

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
				content = StringUtil.replaceFirst(
					content, "cxf-*", "org.apache.cxf.*", x);
				content = StringUtil.replaceFirst(
					content, "jakarta.ws.rs-api-*", "jakarta.ws.rs-*", x);
				content = StringUtil.replaceFirst(
					content, "jakarta.mvc-api", "jakarta.mvc-api", x);
				content = StringUtil.replaceFirst(
					content, "javax\\.mvc-api", "jakarta\\.mvc-api", x);
				content = StringUtil.replaceFirst(
					content, "jaxb-osgi-*", "com.sun.xml.bind.jaxb.osgi-*", x);
				content = StringUtil.replaceFirst(
					content, "jackson-jaxrs-base-*",
					"jackson-jakarta-rs-base-*", x);
				content = StringUtil.replaceFirst(
					content, "jackson-jaxrs-json-provider-*",
					"jackson-jakarta-rs-json-provider-*", x);
				content = StringUtil.replaceFirst(
					content, "jackson-module-jaxb-annotations-*",
					"jackson-module-jakarta-xmlbind-annotations-*", x);

				break;
			}
		}

		return content;
	}

}