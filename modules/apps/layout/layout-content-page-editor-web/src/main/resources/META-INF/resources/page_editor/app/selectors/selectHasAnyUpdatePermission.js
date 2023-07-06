/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * @param {{ permissions: import("../../types/ActionKeys").ActionKeysMap, selectedViewportsize: string }} state
 */
export default function selectHasAnyUpdatePermission({permissions}) {
	return (
		permissions.UPDATE ||
		permissions.UPDATE_LAYOUT_BASIC ||
		permissions.UPDATE_LAYOUT_CONTENT ||
		permissions.UPDATE_LAYOUT_LIMITED
	);
}
