/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {config} from '../config/index';
import isNullOrUndefined from './isNullOrUndefined';

export function getEditableLocalizedValue(
	editableValue,
	languageId = null,
	defaultValue = ''
) {
	let content;

	if (languageId && !isNullOrUndefined(editableValue?.[languageId])) {
		content = editableValue[languageId];
	}
	else if (!isNullOrUndefined(editableValue?.[config.defaultLanguageId])) {
		content = editableValue[config.defaultLanguageId];
	}
	else if (!isNullOrUndefined(editableValue?.defaultValue)) {
		content = editableValue.defaultValue;
	}
	else if (typeof editableValue === typeof defaultValue) {
		content = editableValue;
	}
	else {
		content = defaultValue;
	}

	return content;
}
