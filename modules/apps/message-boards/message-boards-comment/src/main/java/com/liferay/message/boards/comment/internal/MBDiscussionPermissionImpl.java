/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.comment.internal;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.permission.MBDiscussionPermission;
import com.liferay.portal.kernel.comment.BaseDiscussionPermission;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 * @author Adolfo Pérez
 * @author Sergio González
 */
public class MBDiscussionPermissionImpl extends BaseDiscussionPermission {

	public MBDiscussionPermissionImpl(PermissionChecker permissionChecker) {
		_permissionChecker = permissionChecker;
	}

	@Override
	public boolean hasAddPermission(
		long companyId, long groupId, String className, long classPK) {

		return MBDiscussionPermission.contains(
			_permissionChecker, companyId, groupId, className, classPK,
			ActionKeys.ADD_DISCUSSION);
	}

	@Override
	public boolean hasPermission(Comment comment, String actionId)
		throws PortalException {

		if (comment instanceof MBCommentImpl) {
			MBCommentImpl mbCommentImpl = (MBCommentImpl)comment;

			MBMessage mbMessage = mbCommentImpl.getMessage();

			return MBDiscussionPermission.contains(
				_permissionChecker, mbMessage, actionId);
		}

		return MBDiscussionPermission.contains(
			_permissionChecker, comment.getCommentId(), actionId);
	}

	@Override
	public boolean hasPermission(long commentId, String actionId)
		throws PortalException {

		return MBDiscussionPermission.contains(
			_permissionChecker, commentId, actionId);
	}

	@Override
	public boolean hasSubscribePermission(
			long companyId, long groupId, String className, long classPK)
		throws PortalException {

		return hasViewPermission(companyId, groupId, className, classPK);
	}

	@Override
	public boolean hasViewPermission(
		long companyId, long groupId, String className, long classPK) {

		return MBDiscussionPermission.contains(
			_permissionChecker, companyId, groupId, className, classPK,
			ActionKeys.VIEW);
	}

	private final PermissionChecker _permissionChecker;

}