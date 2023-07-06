/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {localStorage} from 'frontend-js-web';

export function getSettingValue(defaultValue, sessionClicksValue, key) {
	if (themeDisplay.isSignedIn() && !isNullOrUndefined(sessionClicksValue)) {
		return sessionClicksValue;
	}
	else {
		const localStorageValue = localStorage.getItem(
			key,
			localStorage.TYPES.FUNCTIONAL
		);

		if (!isNullOrUndefined(localStorageValue)) {
			return localStorageValue === 'true' ? true : false;
		}
	}

	return defaultValue;
}

export function isNullOrUndefined(value) {
	return value === null || value === undefined;
}

export function toggleClassName(className, value) {
	document.querySelector('body').classList.toggle(className, value);
}
