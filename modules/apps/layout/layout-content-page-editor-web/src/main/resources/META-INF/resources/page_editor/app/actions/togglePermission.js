/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TOGGLE_PERMISSION} from './types';

/**
 * @param {import('../../types/ActionKeys').ACTION_KEYS} key
 * @param {boolean} [value]
 */
export default function togglePermission(key, value = undefined) {
	const action = {
		key,
		type: TOGGLE_PERMISSION,
	};

	if (typeof value === 'boolean') {
		action.forceNewValue = value;
	}

	return action;
}
