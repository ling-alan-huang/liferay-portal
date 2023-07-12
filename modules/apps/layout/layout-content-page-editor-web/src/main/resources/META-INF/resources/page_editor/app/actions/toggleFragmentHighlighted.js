/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TOGGLE_FRAGMENT_HIGHLIGHTED} from './types';

export default function toggleFragmentHighlighted({
	fragmentEntryKey,
	groupId,
	highlighted,
	highlightedFragments,
	initiallyHighlighted,
}) {
	return {
		fragmentEntryKey,
		groupId,
		highlighted,
		highlightedFragments,
		initiallyHighlighted,
		type: TOGGLE_FRAGMENT_HIGHLIGHTED,
	};
}
