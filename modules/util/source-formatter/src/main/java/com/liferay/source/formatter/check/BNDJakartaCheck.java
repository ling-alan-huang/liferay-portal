/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class BNDJakartaCheck extends BaseJakartaTransformerCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		content = _formatImportPackage(content);
		content = _formatIncludeResource(content);
		content = replaceTaglibURIs(content);

		if (absolutePath.contains("/bean-portlet-cdi-extension/") ||
				absolutePath.contains("/portal-bootstrap/") ||
				absolutePath.contains("/portal-remote-cxf-common/") ||
				absolutePath.contains("/portal-osgi-web-http-servlet-impl/")
		) {
			return replace(replacementDashDotMap, content);
		}

		return content;
	}

	private String _formatImportPackage(String content) {
		Matcher matcher = _importPackagePattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		String importPackage = matcher.group();

		String newImportPackage = importPackage;

		newImportPackage = StringUtil.replace(
			newImportPackage,
			new String[] {
				"jakarta.el;resolution:=optional,\\",
				"jakarta.servlet;resolution:=optional,\\",
				"jakarta.servlet.http;resolution:=optional,\\",
				"jakarta.servlet.jsp;resolution:=optional,\\",
				"jakarta.servlet.jsp.el;resolution:=optional,\\",
				"jakarta.servlet.jsp.tagext;resolution:=optional,\\"
			},
			new String[] {
				"jakarta.el,\\\n\t!javax.el,\\",
				"jakarta.servlet.*,\\\n\t!javax.servlet.*,\\", "", "", "", ""
			});

		if (newImportPackage.contains("!net.sf.cglib.proxy.*,\\") &&
			!newImportPackage.contains("!javax.transaction.*,\\")) {

			newImportPackage = StringUtil.replace(
				newImportPackage, "!net.sf.cglib.proxy.*,\\",
				"!net.sf.cglib.proxy.*,\\\n\t!javax.transaction.*,\\");
		}

		if (newImportPackage.contains("\t!org.bouncycastle.*,") &&
			!newImportPackage.contains("!org.apache.abdera.*") &&
			!newImportPackage.contains("!org.apache.cxf.aegis.*") &&
			!newImportPackage.contains("!org.codehaus.jettison.*")) {

			newImportPackage = StringUtil.replaceFirst(
				newImportPackage, "\t!org.bouncycastle.*,",
				"\t!org.apache.abdera.*,\\\n\t!org.apache.cxf.aegis.*,\\" +
					"\n\t!org.bouncycastle.*,\\\n\t!org.codehaus.jettison.*,");
		}

		if (!importPackage.equals(newImportPackage)) {
			return StringUtil.replaceFirst(
				content, importPackage, newImportPackage, matcher.start());
		}

		return content;
	}

	private String _formatIncludeResource(String content) {
		Matcher matcher = _includeResourcePattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		String includeResource = matcher.group();

		String newIncludeResource = includeResource;

		newIncludeResource = StringUtil.replace(
			newIncludeResource,
			new String[] {
				"cxf-rt-rs-extension-providers*", "cxf-rt-rs-json-basic*",
				"cxf-rt-rs-security-jose*", "cxf-rt-rs-security-jose-jaxrs*",
				"cxf-rt-rs-security-oauth2*", "cxf-rt-security*"
			},
			new String[] {
				"org.apache.cxf.rt.rs.extension.providers*",
				"org.apache.cxf.rt.rs.json.basic*",
				"org.apache.cxf.rt.rs.security.jose*",
				"org.apache.cxf.rt.rs.security.jose.jaxrs*",
				"org.apache.cxf.rt.rs.security.oauth2*",
				"org.apache.cxf.rt.security*"
			});
		newIncludeResource = StringUtil.replace(
			newIncludeResource,
			new String[] {
				"javax.servlet.jsp.jstl", "javax/servlet/jsp/resources",
				"javax/ws/rs/core/"
			},
			new String[] {
				"jakarta.servlet.jsp.jstl", "jakarta/servlet/jsp/resources",
				"jakarta/ws/rs/core/"
			});
		newIncludeResource = StringUtil.replace(
			newIncludeResource,
			new String[] {"opensaml-messaging-impl-*", "opensaml-saml-impl-*"},
			new String[] {
				"org.opensaml.messaging.impl-*", "org.opensaml.impl-*"
			});

		if (!includeResource.equals(newIncludeResource)) {
			return StringUtil.replaceFirst(
				content, includeResource, newIncludeResource, matcher.start());
		}

		return content;
	}

	private static final Pattern _importPackagePattern = Pattern.compile(
		"\nImport-Package:(\\\\\n| )(.*?(\n[^\t]|\\Z))",
		Pattern.DOTALL | Pattern.MULTILINE);
	private static final Pattern _includeResourcePattern = Pattern.compile(
		"^-includeresource:[\\s\\S]*?([^\\\\]\n|\\Z)", Pattern.MULTILINE);

}