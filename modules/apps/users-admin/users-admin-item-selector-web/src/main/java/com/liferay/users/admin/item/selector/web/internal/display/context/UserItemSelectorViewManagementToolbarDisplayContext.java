/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.item.selector.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class UserItemSelectorViewManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public UserItemSelectorViewManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			UserItemSelectorViewDisplayContext
				userItemSelectorViewDisplayContext)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			userItemSelectorViewDisplayContext.getSearchContainer());

		_userItemSelectorViewDisplayContext =
			userItemSelectorViewDisplayContext;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public String getSearchContainerId() {
		return _userItemSelectorViewDisplayContext.getSearchContainerId();
	}

	@Override
	protected String getDisplayStyle() {
		return _userItemSelectorViewDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"descriptive", "icon", "list"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"first-name", "last-name", "screen-name"};
	}

	private final UserItemSelectorViewDisplayContext
		_userItemSelectorViewDisplayContext;

}