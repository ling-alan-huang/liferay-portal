/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate.event;

import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;

/**
 * @author Michael Bowerman
 */
public class CompanySynchronizerPreDeleteEventListener
	implements PreDeleteEventListener {

	public static final CompanySynchronizerPreDeleteEventListener INSTANCE =
		new CompanySynchronizerPreDeleteEventListener();

	public boolean onPreDelete(PreDeleteEvent event) {
		Object entity = event.getEntity();

		if (entity instanceof ShardedModel) {
			ShardedModel shardedModel = (ShardedModel)entity;

			CompanyThreadLocal.pushCompanyId(shardedModel.getCompanyId());
		}

		return false;
	}

}