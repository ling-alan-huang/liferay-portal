/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CHANGE_MASTER_LAYOUT} from './types';

export default function changeMasterLayout({
	fragmentEntryLinks = [],
	masterLayoutData = null,
	masterLayoutPlid = 0,
}) {
	return {
		fragmentEntryLinks,
		masterLayoutData,
		masterLayoutPlid,
		type: CHANGE_MASTER_LAYOUT,
	};
}
