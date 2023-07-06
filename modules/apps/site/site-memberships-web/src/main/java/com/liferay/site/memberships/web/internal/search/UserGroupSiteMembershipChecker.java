/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;

import javax.portlet.RenderResponse;

/**
 * @author Charles May
 */
public class UserGroupSiteMembershipChecker extends EmptyOnClickRowChecker {

	public UserGroupSiteMembershipChecker(
		RenderResponse renderResponse, Group group) {

		super(renderResponse);

		_group = group;
	}

	@Override
	public boolean isChecked(Object object) {
		UserGroup userGroup = (UserGroup)object;

		try {
			return UserGroupLocalServiceUtil.hasGroupUserGroup(
				_group.getGroupId(), userGroup.getUserGroupId());
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupSiteMembershipChecker.class);

	private final Group _group;

}