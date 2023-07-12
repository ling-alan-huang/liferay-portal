/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.info.action.provider;

import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemActionException;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.exception.PortalException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author David Arques
 */
public interface AnalyticsReportsContentDashboardItemActionProvider {

	public default ContentDashboardItemAction getContentDashboardItemAction(
			HttpServletRequest httpServletRequest,
			InfoItemReference infoItemReference)
		throws ContentDashboardItemActionException {

		return getContentDashboardItemAction(
			infoItemReference.getClassName(), infoItemReference.getClassPK(),
			httpServletRequest);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getContentDashboardItemAction(
	 *             HttpServletRequest, InfoItemReference)}
	 */
	@Deprecated
	public ContentDashboardItemAction getContentDashboardItemAction(
			String className, long classPK,
			HttpServletRequest httpServletRequest)
		throws ContentDashboardItemActionException;

	public default boolean isShowContentDashboardItemAction(
			HttpServletRequest httpServletRequest,
			InfoItemReference infoItemReference)
		throws PortalException {

		return isShowContentDashboardItemAction(
			infoItemReference.getClassName(), infoItemReference.getClassPK(),
			httpServletRequest);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #isShowContentDashboardItemAction(
	 *             HttpServletRequest, InfoItemReference)}
	 */
	@Deprecated
	public boolean isShowContentDashboardItemAction(
			String className, long classPK,
			HttpServletRequest httpServletRequest)
		throws PortalException;

}