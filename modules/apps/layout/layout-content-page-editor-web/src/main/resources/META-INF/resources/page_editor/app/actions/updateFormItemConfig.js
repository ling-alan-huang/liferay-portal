/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {UPDATE_FORM_ITEM_CONFIG} from './types';

export default function updateFormItemConfig({
	addedFragmentEntryLinks = null,
	deletedItems = [],
	isMapping,
	itemId,
	layoutData,
	overridePreviousConfig = false,
	removedFragmentEntryLinkIds = [],
	restoredFragmentEntryLinkIds = [],
}) {
	return {
		addedFragmentEntryLinks,
		deletedItems,
		isMapping,
		itemId,
		layoutData,
		overridePreviousConfig,
		removedFragmentEntryLinkIds,
		restoredFragmentEntryLinkIds,
		type: UPDATE_FORM_ITEM_CONFIG,
	};
}
