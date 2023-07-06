/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {VIEWPORT_SIZES} from '../config/constants/viewportSizes';

/**
 * @param {Array<Array<string>>} panels
 */
export default function selectAvailablePanels(panels) {

	/**
	 * @param {{ permissions: import("../../types/ActionKeys").ActionKeysMap, selectedViewportSize: string }} state
	 */
	return function ({permissions, selectedViewportSize}) {
		const availablePanels = ['browser', 'comments', 'page_content'];

		if (
			permissions.LOCKED_SEGMENTS_EXPERIMENT ||
			(!permissions.UPDATE &&
				!permissions.UPDATE_LAYOUT_LIMITED &&
				!permissions.UPDATE_LAYOUT_BASIC)
		) {
			return panels
				.map((group) =>
					group.filter((panelId) => availablePanels.includes(panelId))
				)
				.filter((group) => group.length);
		}
		else if (selectedViewportSize !== VIEWPORT_SIZES.desktop) {
			return panels
				.map((group) =>
					group.filter((panelId) => availablePanels.includes(panelId))
				)
				.filter((group) => group.length);
		}

		return panels;
	};
}
