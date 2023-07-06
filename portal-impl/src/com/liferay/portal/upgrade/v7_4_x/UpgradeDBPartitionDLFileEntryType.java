/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.db.partition.DBPartitionUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;

import java.sql.PreparedStatement;

/**
 * @author Alberto Chaparro
 */
public class UpgradeDBPartitionDLFileEntryType extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		long companyId = CompanyThreadLocal.getCompanyId();

		if (companyId != _getDefaultCompanyId()) {
			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"update DLFileEntryType set companyId = ? where " +
							"fileEntryTypeKey = ?")) {

				preparedStatement.setLong(1, companyId);
				preparedStatement.setString(2, "BASIC-DOCUMENT");

				preparedStatement.executeUpdate();
			}
		}
	}

	@Override
	protected boolean isSkipUpgradeProcess() {
		return !DBPartitionUtil.isPartitionEnabled();
	}

	private long _getDefaultCompanyId() {
		if (_defaultCompanyId == CompanyConstants.SYSTEM) {
			_defaultCompanyId = PortalUtil.getDefaultCompanyId();
		}

		return _defaultCompanyId;
	}

	private static long _defaultCompanyId;

}