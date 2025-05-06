/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.BNDSourceUtil;

/**
 * @author Alan Huang
 */
public class BNDJakartaCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		String moduleName = BNDSourceUtil.getModuleName(absolutePath);

		if (moduleName.equals("oauth2-provider-rest") ||
			moduleName.equals("oauth2-provider-test")) {

			content = StringUtil.replace(
				content,
				new String[] {
					"cxf-rt-rs-extension-providers*", "cxf-rt-rs-json-basic*",
					"cxf-rt-rs-security-jose*",
					"cxf-rt-rs-security-jose-jaxrs*",
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

			if (moduleName.equals("oauth2-provider-test")) {
				content = StringUtil.replace(
					content, "\t!org.bouncycastle.*,\\",
					"\t!org.apache.abdera.*,\\\n\t!org.apache.cxf.aegis.*,\\" +
						"\n\t\\\n\t!org.bouncycastle.*,\\\n\t\\\n\t" +
							"!org.codehaus.jettison.*,\\\n\t\\");
			}
		}
		else if (moduleName.equals("portal-jsp-engine")) {
			content = StringUtil.replace(
				content, "javax/servlet/jsp/resources",
				"jakarta/servlet/jsp/resources");
		}
		else if (moduleName.equals("portal-osgi-web-servlet-jsp-compiler")) {
			content = StringUtil.replace(
				content, "javax.servlet.jsp.jstl", "jakarta.servlet.jsp.jstl");
		}
		else if (moduleName.equals("portal-reports-engine-console-service")) {
			content = StringUtil.replace(
				content, "!net.sf.cglib.proxy.*,\\",
				"!net.sf.cglib.proxy.*,\\\n\t!javax.transaction.*\\");
		}
		else if (moduleName.equals("document-library-google-docs") ||
				 moduleName.equals("portal-template-freemarker")) {

			content = StringUtil.replace(
				content,
				new String[] {
					"jakarta.el;resolution:=optional,\\",
					"jakarta.el,\\\n\t!javax.el,\\",
					"jakarta.servlet.http;resolution:=optional,\\",
					"jakarta.servlet.jsp;resolution:=optional,\\",
					"jakarta.servlet.jsp.el;resolution:=optional,\\",
					"jakarta.servlet.jsp.tagext;resolution:=optional,\\",
					"javax.servlet.jsp.jstl"
				},
				new String[] {
					"jakarta.servlet;resolution:=optional,\\",
					"jakarta.servlet.*,\\\n\t!javax.servlet.*,\\", "", "", "",
					"", "jakarta.servlet.jsp.jstl"
				});
		}
		else if (moduleName.equals("portal-tools-rest-builder")) {
			content = StringUtil.replace(
				content, "javax/ws/rs/core/", "jakarta/ws/rs/core/");
		}
		else if (moduleName.equals("saml-opensaml-integration")) {
			content = StringUtil.replace(
				content,
				new String[] {
					"opensaml-messaging-impl-*", "opensaml-saml-impl-*"
				},
				new String[] {
					"org.opensaml.messaging.impl-*", "org.opensaml.impl-*"
				});
		}

		content = _replaceTaglibURIs(content);

		if (moduleName.equals("bean-portlet-cdi-extension") ||
			moduleName.equals("portal-bootstrap") ||
			moduleName.equals("portal-remote-cxf-common") ||
			moduleName.equals("portal-osgi-web-http-servlet-impl")) {

			//			newContent = TextReplacerBiFunction.INSTANCE.apply(
			//					"BndSource", newContent);
		}

		return content;
	}

	private static String _replaceTaglibURIs(String content) {
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

}