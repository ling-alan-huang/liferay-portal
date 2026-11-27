/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.context.helper.definition;

import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.Servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Miguel Pastor
 */
public class ServletDefinitionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAddMultipleURLPatterns() {
		String urlPattern = "/o/module";

		List<String> urlPatterns = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			urlPatterns.add(urlPattern + "/" + i);
		}

		_servletDefinition.setURLPatterns(urlPatterns);

		urlPatterns = _servletDefinition.getURLPatterns();

		for (int i = 0; i < 10; i++) {
			Assert.assertEquals(urlPattern + "/" + i, urlPatterns.get(i));
		}

		Assert.assertEquals(urlPatterns.toString(), 10, urlPatterns.size());
	}

	@Test
	public void testAddSingleURLPattern() {
		String urlPattern = "/o/module";

		_servletDefinition.addURLPattern(urlPattern);

		List<String> urlPatterns = _servletDefinition.getURLPatterns();

		Assert.assertEquals(urlPattern, urlPatterns.get(0));

		Assert.assertEquals(urlPatterns.toString(), 1, urlPatterns.size());
	}

	@Test
	public void testSetMultipleInitParameters() {
		Map<String, String> initParameters = new HashMap<>();

		for (int i = 0; i < 10; i++) {
			initParameters.put("parameter-" + i, String.valueOf(i));
		}

		_servletDefinition.setInitParameters(initParameters);

		initParameters = _servletDefinition.getInitParameters();

		for (int i = 0; i < 10; i++) {
			String expectedValue = String.valueOf(i);
			String value = initParameters.get("parameter-" + i);

			Assert.assertEquals(expectedValue, value);
		}

		Assert.assertEquals(
			initParameters.toString(), 10, initParameters.size());
	}

	@Test
	public void testSetServlet() {
		Servlet servlet = ProxyFactory.newDummyInstance(Servlet.class);

		_servletDefinition.setServlet(servlet);

		Assert.assertSame(servlet, _servletDefinition.getServlet());
	}

	@Test
	public void testSetServletName() {
		String servletName = "Module Servlet";

		_servletDefinition.setName(servletName);

		Assert.assertEquals(servletName, _servletDefinition.getName());
	}

	@Test
	public void testSetSingleInitParameter() {
		String key = "parameter";
		String value = "1";

		_servletDefinition.setInitParameter(key, value);

		Map<String, String> initParameters =
			_servletDefinition.getInitParameters();

		Assert.assertEquals(value, initParameters.get(key));

		Assert.assertEquals(
			initParameters.toString(), 1, initParameters.size());
	}

	private final ServletDefinition _servletDefinition =
		new ServletDefinition();

}