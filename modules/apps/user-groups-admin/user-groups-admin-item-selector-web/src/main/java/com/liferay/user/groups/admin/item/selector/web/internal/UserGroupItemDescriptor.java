/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.util.HtmlUtil;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class UserGroupItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public UserGroupItemDescriptor(UserGroup userGroup) {
		_userGroup = userGroup;
	}

	@Override
	public String getIcon() {
		return "users";
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public String getPayload() {
		return JSONUtil.put(
			"name", HtmlUtil.escape(_userGroup.getName())
		).put(
			"userGroupId", _userGroup.getUserGroupId()
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return HtmlUtil.escape(_userGroup.getDescription());
	}

	@Override
	public String getTitle(Locale locale) {
		return HtmlUtil.escape(_userGroup.getName());
	}

	private final UserGroup _userGroup;

}