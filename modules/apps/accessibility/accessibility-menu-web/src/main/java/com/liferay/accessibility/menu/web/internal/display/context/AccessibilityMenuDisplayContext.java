/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.accessibility.menu.web.internal.display.context;

import com.liferay.accessibility.menu.web.internal.util.AccessibilitySettingsUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Evan Thibodeau
 */
public class AccessibilityMenuDisplayContext {

	public AccessibilityMenuDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public JSONArray getAccessibilitySettingsJSONArray() throws Exception {
		return JSONUtil.toJSONArray(
			AccessibilitySettingsUtil.getAccessibilitySettings(
				_httpServletRequest),
			accessibilitySetting -> JSONUtil.put(
				"className", accessibilitySetting.getCssClass()
			).put(
				"defaultValue", accessibilitySetting.getDefaultValue()
			).put(
				"description", accessibilitySetting.getDescription()
			).put(
				"key", accessibilitySetting.getKey()
			).put(
				"label", accessibilitySetting.getLabel()
			).put(
				"sessionClicksValue",
				accessibilitySetting.getSessionClicksValue()
			));
	}

	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, PanelAppShowFilter.class);

		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, PanelApp.class, "(panel.category.key=*)",
			new PropertyServiceReferenceMapper<>("panel.category.key"),
			new ServiceTrackerCustomizer<PanelApp, PanelApp>() {

				@Override
				public PanelApp addingService(
					ServiceReference<PanelApp> serviceReference) {

					PanelApp panelApp = bundleContext.getService(
						serviceReference);

					return panelApp;
				}

				@Override
				public void modifiedService(
					ServiceReference<PanelApp> serviceReference,
					PanelApp panelApp) {
				}

				@Override
				public void removedService(
					ServiceReference<PanelApp> serviceReference,
					PanelApp panelApp) {

					bundleContext.ungetService(serviceReference);
				}

			},
			Collections.reverseOrder(
				new PanelEntryServiceReferenceComparator<PanelApp>(
					bundleContext, _log, "panel.app.order")));
	}

	private final HttpServletRequest _httpServletRequest;

}