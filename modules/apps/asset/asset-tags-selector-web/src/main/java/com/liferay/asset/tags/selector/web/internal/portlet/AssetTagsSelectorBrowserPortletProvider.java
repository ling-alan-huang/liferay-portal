/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.selector.web.internal.portlet;

import com.liferay.asset.tags.selector.web.internal.constants.AssetTagsSelectorPortletKeys;
import com.liferay.portal.kernel.portlet.BasePortletProvider;
import com.liferay.portal.kernel.portlet.PortletProvider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "model.class.name=com.liferay.asset.kernel.model.AssetTag",
	service = PortletProvider.class
)
public class AssetTagsSelectorBrowserPortletProvider
	extends BasePortletProvider {

	@Override
	public String getPortletName() {
		return AssetTagsSelectorPortletKeys.ASSET_TAGS_SELECTOR;
	}

	@Override
	public Action[] getSupportedActions() {
		return _supportedActions;
	}

	private final Action[] _supportedActions = {Action.BROWSE};

}