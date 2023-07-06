/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {UPDATE_COLLECTION_DISPLAY_COLLECTION} from './types';

export default function updateCollectionDisplayCollection({
	fragmentEntryLinks,
	itemId,
	layoutData,
	pageContents,
}) {
	return {
		fragmentEntryLinks,
		itemId,
		layoutData,
		pageContents,
		type: UPDATE_COLLECTION_DISPLAY_COLLECTION,
	};
}
