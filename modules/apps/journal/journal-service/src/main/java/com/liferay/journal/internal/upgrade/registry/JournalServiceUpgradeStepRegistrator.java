/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.registry;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.capabilities.PortalCapabilityLocator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(service = UpgradeStepRegistrator.class)
public class JournalServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	private static final Log _log = LogFactoryUtil.getLog(
		JournalServiceUpgradeStepRegistrator.class);

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Portal _portal;

	// See LPS-82746

	@Reference
	private PortalCapabilityLocator _portalCapabilityLocator;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.layout.service)(&(release.schema.version>=1.0.0)))"
	)
	private Release _arelease;

}