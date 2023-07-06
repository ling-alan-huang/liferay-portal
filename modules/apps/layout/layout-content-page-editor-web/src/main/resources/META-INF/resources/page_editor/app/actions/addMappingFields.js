/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ADD_MAPPING_FIELDS} from './types';

/**
 * @param {object} options
 * @param {string} options.fields
 * @param {string} options.key
 * @return {object}
 */
export default function addMappingFields({fields, key}) {
	return {
		fields,
		key,
		type: ADD_MAPPING_FIELDS,
	};
}
