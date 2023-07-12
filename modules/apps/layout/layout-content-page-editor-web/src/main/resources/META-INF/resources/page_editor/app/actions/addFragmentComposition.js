/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ADD_FRAGMENT_COMPOSITION} from './types';

export default function addFragmentComposition({
	fragmentCollectionId,
	fragmentComposition,
}) {
	return {
		fragmentCollectionId,
		fragmentComposition,
		type: ADD_FRAGMENT_COMPOSITION,
	};
}
