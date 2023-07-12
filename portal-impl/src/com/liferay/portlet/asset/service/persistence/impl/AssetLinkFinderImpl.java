/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.impl;

import com.liferay.asset.kernel.model.AssetLink;
import com.liferay.asset.kernel.service.persistence.AssetLinkFinder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.asset.model.impl.AssetLinkImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.Date;
import java.util.List;

/**
 * @author Zoltan Csaszi
 */
public class AssetLinkFinderImpl
	extends AssetLinkFinderBaseImpl implements AssetLinkFinder {

	public static final String FIND_BY_G_C =
		AssetLinkFinder.class.getName() + ".findByG_C";

	public static final String FIND_BY_C_C =
		AssetLinkFinder.class.getName() + ".findByC_C";

	@Override
	public List<AssetLink> findByAssetEntryGroupId(
		long groupId, int start, int end) {

		return findByG_C(groupId, null, null, start, end);
	}

	@Override
	public List<AssetLink> findByG_C(
		long groupId, Date startDate, Date endDate, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_G_C);

			sql = StringUtil.replace(
				sql, "[$CREATE_DATE_COMPARATOR$]",
				_getCreateDateComparator(startDate, endDate));

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("AssetLink", AssetLinkImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(groupId);

			if (startDate != null) {
				queryPos.add(startDate);
			}

			if (endDate != null) {
				queryPos.add(endDate);
			}

			return (List<AssetLink>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<AssetLink> findByC_C(long classNameId, long classPK) {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_C_C);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("AssetLink", AssetLinkImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			return sqlQuery.list();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private String _getCreateDateComparator(Date startDate, Date endDate) {
		if ((startDate == null) && (endDate == null)) {
			return StringPool.BLANK;
		}

		String createDateComparator = StringPool.BLANK;

		if (startDate != null) {
			createDateComparator = " AND (AssetLink.createDate > ?)";
		}

		if (endDate != null) {
			createDateComparator =
				createDateComparator + " AND (AssetLink.createDate < ?)";
		}

		return createDateComparator;
	}

}