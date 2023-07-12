/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.item.selector.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.usersadmin.search.UserSearch;
import com.liferay.portlet.usersadmin.search.UserSearchTerms;
import com.liferay.users.admin.item.selector.web.internal.search.UserItemSelectorChecker;
import com.liferay.users.admin.kernel.util.UsersAdmin;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class UserItemSelectorViewDisplayContext {

	public UserItemSelectorViewDisplayContext(
		UserLocalService userLocalService, UsersAdmin usersAdmin,
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		String itemSelectedEventName) {

		_userLocalService = userLocalService;
		_usersAdmin = usersAdmin;
		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_itemSelectedEventName = itemSelectedEventName;

		_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);
		_renderResponse = (RenderResponse)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE);
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle", "list");

		return _displayStyle;
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public String getOrderByCol() {
		return ParamUtil.getString(
			_portletRequest, SearchContainer.DEFAULT_ORDER_BY_COL_PARAM,
			"first-name");
	}

	public String getOrderByType() {
		return ParamUtil.getString(
			_portletRequest, SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM,
			"asc");
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public RowChecker getRowChecker() throws PortalException {
		SearchContainer<User> searchContainer = getSearchContainer();

		return searchContainer.getRowChecker();
	}

	public SearchContainer<User> getSearchContainer() throws PortalException {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new UserSearch(_portletRequest, getPortletURL());

		_searchContainer.setEmptyResultsMessage("no-users-were-found");
		_searchContainer.setOrderByCol(getOrderByCol());
		_searchContainer.setOrderByComparator(
			_usersAdmin.getUserOrderByComparator(
				getOrderByCol(), getOrderByType()));
		_searchContainer.setOrderByType(getOrderByType());

		UserSearchTerms userSearchTerms =
			(UserSearchTerms)_searchContainer.getSearchTerms();

		long companyId = CompanyThreadLocal.getCompanyId();
		String keywords = userSearchTerms.getKeywords();
		int status = userSearchTerms.getStatus();

		_searchContainer.setResultsAndTotal(
			() -> _userLocalService.search(
				companyId, keywords, status, null, _searchContainer.getStart(),
				_searchContainer.getEnd(),
				_searchContainer.getOrderByComparator()),
			_userLocalService.searchCount(companyId, keywords, status, null));

		_searchContainer.setRowChecker(
			new UserItemSelectorChecker(
				_renderResponse, _getCheckedUserIds(),
				_isCheckedUseIdsEnable()));

		return _searchContainer;
	}

	public String getSearchContainerId() {
		return "users";
	}

	private long[] _getCheckedUserIds() {
		return ParamUtil.getLongValues(_portletRequest, "checkedUserIds");
	}

	private boolean _isCheckedUseIdsEnable() {
		return ParamUtil.getBoolean(_portletRequest, "checkedUserIdsEnabled");
	}

	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;
	private final PortletRequest _portletRequest;
	private final PortletURL _portletURL;
	private final RenderResponse _renderResponse;
	private SearchContainer<User> _searchContainer;
	private final UserLocalService _userLocalService;
	private final UsersAdmin _usersAdmin;

}