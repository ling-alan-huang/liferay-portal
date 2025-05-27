/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Alan Huang
 */
public abstract class BaseJakartaTransformerCheck extends BaseFileCheck {

	public static final Map<String, String> replacementDashDotMap =
		new LinkedHashMap<>();
	public static final Map<String, String> replacementSlashMap =
		new LinkedHashMap<>();
	public static final Map<String, String> reverseReplacementDashDotMap =
		new HashMap<>();

	public static String replace(
		Map<String, String> replacementMap, String value) {

		for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
			value = StringUtil.replace(value, entry.getKey(), entry.getValue());
		}

		return value;
	}

	protected static String replaceTaglibURIs(String content) {
		return StringUtil.replace(
			content,
			new String[] {
				"http://java.sun.com/jsp/jstl/core",
				"http://java.sun.com/jsp/jstl/fmt",
				"http://java.sun.com/jsp/jstl/functions",
				"http://java.sun.com/jsp/jstl/sql",
				"http://java.sun.com/jsp/jstl/xml"
			},
			new String[] {
				"jakarta.tags.core", "jakarta.tags.fmt",
				"jakarta.tags.functions", "jakarta.tags.sql", "jakarta.tags.xml"
			});
	}

	private static final Set<String> _fixupSubpackageNames = new HashSet<>(
		Arrays.asList("annotation.processing", "transaction.xa"));
	private static final Set<String> _subpackageNames = new HashSet<>(
		Arrays.asList(
			"activation", "annotation", "batch", "decorator", "ejb", "el",
			"enterprise", "faces", "inject", "interceptor", "jms", "json",
			"jws", "mail", "mvc", "persistence", "portlet", "resource",
			"security.auth.message", "security.enterprise", "security.jacc",
			"servlet", "transaction", "validation", "websocket", "ws.rs",
			"xml.bind", "xml.soap", "xml.ws"));

	static {
		_subpackageNames.forEach(
			subpackageName -> {
				String javaxPackage = "javax." + subpackageName;
				String jakartaPackage = "jakarta." + subpackageName;

				replacementDashDotMap.put(
					StringUtil.replace(javaxPackage, '.', '-'),
					StringUtil.replace(jakartaPackage, '.', '-'));
				replacementDashDotMap.put(javaxPackage, jakartaPackage);
				replacementSlashMap.put(
					StringUtil.replace(javaxPackage, '.', '/'),
					StringUtil.replace(jakartaPackage, '.', '/'));
				reverseReplacementDashDotMap.put(
					StringUtil.replace(jakartaPackage, '.', '-'),
					StringUtil.replace(javaxPackage, '.', '-'));
				reverseReplacementDashDotMap.put(jakartaPackage, javaxPackage);
			});

		// Order matters, fixups need to be put into replacement map later

		_fixupSubpackageNames.forEach(
			fixupSubpackageName -> {
				String fixupJavaxPackage = "javax." + fixupSubpackageName;
				String fixupJakartaPackage = "jakarta." + fixupSubpackageName;

				replacementDashDotMap.put(
					StringUtil.replace(fixupJakartaPackage, '.', '-'),
					StringUtil.replace(fixupJavaxPackage, '.', '-'));
				replacementDashDotMap.put(
					fixupJakartaPackage, fixupJavaxPackage);
				replacementSlashMap.put(
					StringUtil.replace(fixupJakartaPackage, '.', '/'),
					StringUtil.replace(fixupJavaxPackage, '.', '/'));
			});

		replacementDashDotMap.put(
			"X-JAVAX-PORTLET-NAMESPACED-RESPONSE",
			"X-JAKARTA-PORTLET-NAMESPACED-RESPONSE");
	}

}