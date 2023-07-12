/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ADD_FRAGMENT_ENTRY_LINKS} from './types';

/**
 * @param {object} data
 * @param {object[]} data.fragmentEntryLinks
 * @param {object} data.layoutData
 */
export default function addFragmentEntryLinks({
	addedItemId,
	fragmentEntryLinks,
	layoutData,
}) {
	return {
		fragmentEntryLinks,
		itemId: addedItemId,
		layoutData,
		type: ADD_FRAGMENT_ENTRY_LINKS,
	};
}
