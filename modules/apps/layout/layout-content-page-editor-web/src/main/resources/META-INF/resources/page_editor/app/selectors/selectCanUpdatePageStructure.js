/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {VIEWPORT_SIZES} from '../config/constants/viewportSizes';

/**
 * @param {{ permissions: import("../../types/ActionKeys").ActionKeysMap, selectedViewportsize: string }} state
 */
export default function selectCanUpdatePageStructure({
	permissions,
	selectedViewportSize,
}) {
	return (
		!permissions.LOCKED_SEGMENTS_EXPERIMENT &&
		(permissions.UPDATE ||
			permissions.UPDATE_LAYOUT_BASIC ||
			permissions.UPDATE_LAYOUT_LIMITED) &&
		selectedViewportSize === VIEWPORT_SIZES.desktop
	);
}
