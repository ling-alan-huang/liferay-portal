/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.registry;

/**
 * @author Eduardo García
 */
@Component(service = UpgradeStepRegistrator.class)
public class JournalServiceUpgradeStepRegistrator
		implements UpgradeStepRegistrator {

	@Reference
	private Portal _b;

	// See LPS-82746

	@Reference
	private PortalCapabilityLocator _c;

	private Release _a;

}