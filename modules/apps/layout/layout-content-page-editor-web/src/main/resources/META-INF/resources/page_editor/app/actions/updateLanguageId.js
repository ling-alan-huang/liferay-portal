/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {UPDATE_LANGUAGE_ID} from './types';

/**
 * @param {string} languageId
 * @return {object}
 */
export default function updateLanguageId({languageId}) {
	return {
		languageId,
		type: UPDATE_LANGUAGE_ID,
	};
}
