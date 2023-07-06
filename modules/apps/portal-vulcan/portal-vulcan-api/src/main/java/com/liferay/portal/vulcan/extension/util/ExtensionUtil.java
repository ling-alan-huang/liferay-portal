/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.extension.util;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.extension.EntityExtensionHandler;
import com.liferay.portal.vulcan.extension.ExtensionProvider;
import com.liferay.portal.vulcan.extension.ExtensionProviderRegistry;

import java.util.List;

/**
 * @author Carlos Correa
 */
public class ExtensionUtil {

	public static EntityExtensionHandler getEntityExtensionHandler(
		String className, long companyId,
		ExtensionProviderRegistry extensionProviderRegistry) {

		List<ExtensionProvider> extensionProviders =
			extensionProviderRegistry.getExtensionProviders(
				companyId, className);

		if (ListUtil.isEmpty(extensionProviders)) {
			return null;
		}

		return new EntityExtensionHandler(className, extensionProviders);
	}

}