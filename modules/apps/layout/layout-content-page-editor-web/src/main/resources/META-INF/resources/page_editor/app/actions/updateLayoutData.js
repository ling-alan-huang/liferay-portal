/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {UPDATE_LAYOUT_DATA} from './types';

export default function updateLayoutData({
	addedFragmentEntryLinks = [],
	deletedFragmentEntryLinkIds = [],
	layoutData,
}) {
	return {
		addedFragmentEntryLinks,
		deletedFragmentEntryLinkIds,
		layoutData,
		type: UPDATE_LAYOUT_DATA,
	};
}
