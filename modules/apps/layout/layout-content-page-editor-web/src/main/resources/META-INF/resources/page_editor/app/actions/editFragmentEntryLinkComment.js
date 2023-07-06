/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EDIT_FRAGMENT_ENTRY_LINK_COMMENT} from './types';

export default function editFragmentEntryLinkComment({
	fragmentEntryLinkComment,
	fragmentEntryLinkId,
	parentCommentId = 0,
}) {
	return {
		fragmentEntryLinkComment,
		fragmentEntryLinkId,
		parentCommentId,
		type: EDIT_FRAGMENT_ENTRY_LINK_COMMENT,
	};
}
