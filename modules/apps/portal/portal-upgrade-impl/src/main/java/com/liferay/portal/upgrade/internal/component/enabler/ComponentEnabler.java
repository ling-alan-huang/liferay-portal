/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.component.enabler;

import com.liferay.portal.upgrade.internal.jmx.UpgradeManager;
import com.liferay.portal.util.PropsValues;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Luis Ortiz
 */
@Component(service = {})
public class ComponentEnabler {

	@Activate
	protected void activate(ComponentContext componentContext) {
		if (PropsValues.UPGRADE_DATABASE_AUTO_RUN) {
			componentContext.enableComponent(UpgradeManager.class.getName());
		}
	}

}