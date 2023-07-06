/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service.persistence;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AssetLinkFinderUtil {

	public static java.util.List<com.liferay.asset.kernel.model.AssetLink>
		findByAssetEntryGroupId(long groupId, int start, int end) {

		return getFinder().findByAssetEntryGroupId(groupId, start, end);
	}

	public static java.util.List<com.liferay.asset.kernel.model.AssetLink>
		findByG_C(
			long groupId, java.util.Date startDate, java.util.Date endDate,
			int start, int end) {

		return getFinder().findByG_C(groupId, startDate, endDate, start, end);
	}

	public static java.util.List<com.liferay.asset.kernel.model.AssetLink>
		findByC_C(long classNameId, long classPK) {

		return getFinder().findByC_C(classNameId, classPK);
	}

	public static AssetLinkFinder getFinder() {
		if (_finder == null) {
			_finder = (AssetLinkFinder)PortalBeanLocatorUtil.locate(
				AssetLinkFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(AssetLinkFinder finder) {
		_finder = finder;
	}

	private static AssetLinkFinder _finder;

}