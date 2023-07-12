/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.user.groups.admin.item.selector.web.internal.display.context.UserGroupItemSelectorViewDisplayContext;

/**
 * @author Eudaldo Alonso
 */
public class UserGroupSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<UserGroup> {

	public UserGroupSelectorViewDescriptor(
		UserGroupItemSelectorViewDisplayContext
			userGroupItemSelectorViewDisplayContext) {

		_userGroupItemSelectorViewDisplayContext =
			userGroupItemSelectorViewDisplayContext;
	}

	@Override
	public ItemDescriptor getItemDescriptor(UserGroup userGroup) {
		return new UserGroupItemDescriptor(userGroup);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new UUIDItemSelectorReturnType();
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"name"};
	}

	public SearchContainer<UserGroup> getSearchContainer() {
		return _userGroupItemSelectorViewDisplayContext.getSearchContainer();
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	private final UserGroupItemSelectorViewDisplayContext
		_userGroupItemSelectorViewDisplayContext;

}