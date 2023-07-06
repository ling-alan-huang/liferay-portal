/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.comment;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Adolfo Pérez
 * @author Sergio González
 */
public interface DiscussionPermission {

	public void checkAddPermission(
			long companyId, long groupId, String className, long classPK)
		throws PortalException;

	public void checkDeletePermission(long commentId) throws PortalException;

	public void checkSubscribePermission(
			long companyId, long groupId, String className, long classPK)
		throws PortalException;

	public void checkUpdatePermission(long commentId) throws PortalException;

	public void checkViewPermission(
			long companyId, long groupId, String className, long classPK)
		throws PortalException;

	public boolean hasAddPermission(
			long companyId, long groupId, String className, long classPK)
		throws PortalException;

	public boolean hasDeletePermission(long commentId) throws PortalException;

	public default boolean hasPermission(Comment comment, String actionId)
		throws PortalException {

		return hasPermission(comment.getCommentId(), actionId);
	}

	public boolean hasPermission(long commentId, String actionId)
		throws PortalException;

	public boolean hasSubscribePermission(
			long companyId, long groupId, String className, long classPK)
		throws PortalException;

	public boolean hasUpdatePermission(long commentId) throws PortalException;

	public boolean hasViewPermission(
			long companyId, long groupId, String className, long classPK)
		throws PortalException;

}