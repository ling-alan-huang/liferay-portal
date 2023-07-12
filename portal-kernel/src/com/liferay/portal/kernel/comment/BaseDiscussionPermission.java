/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.comment;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;

/**
 * @author Adolfo Pérez
 * @author Sergio González
 */
public abstract class BaseDiscussionPermission implements DiscussionPermission {

	@Override
	public void checkAddPermission(
			long companyId, long groupId, String className, long classPK)
		throws PortalException {

		if (!hasAddPermission(companyId, groupId, className, classPK)) {
			throw new PrincipalException.MustHavePermission(
				0, className, classPK, ActionKeys.ADD_DISCUSSION);
		}
	}

	@Override
	public void checkDeletePermission(long commentId) throws PortalException {
		if (!hasDeletePermission(commentId)) {
			throw new PrincipalException.MustHavePermission(
				0, ActionKeys.DELETE_DISCUSSION);
		}
	}

	@Override
	public void checkSubscribePermission(
			long companyId, long groupId, String className, long classPK)
		throws PortalException {

		if (!hasSubscribePermission(companyId, groupId, className, classPK)) {
			throw new PrincipalException();
		}
	}

	@Override
	public void checkUpdatePermission(long commentId) throws PortalException {
		if (!hasUpdatePermission(commentId)) {
			throw new PrincipalException.MustHavePermission(
				0, ActionKeys.UPDATE_DISCUSSION);
		}
	}

	@Override
	public void checkViewPermission(
			long companyId, long groupId, String className, long classPK)
		throws PortalException {

		if (!hasViewPermission(companyId, groupId, className, classPK)) {
			throw new PrincipalException.MustHavePermission(
				0, className, classPK, ActionKeys.VIEW);
		}
	}

	@Override
	public boolean hasDeletePermission(long commentId) throws PortalException {
		return hasPermission(commentId, ActionKeys.DELETE_DISCUSSION);
	}

	@Override
	public boolean hasUpdatePermission(long commentId) throws PortalException {
		return hasPermission(commentId, ActionKeys.UPDATE_DISCUSSION);
	}

}