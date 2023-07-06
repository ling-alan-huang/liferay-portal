/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.portal.dao.orm.hibernate.event.ResetOriginalValuesLoadEventListener;
import com.liferay.portal.dao.orm.hibernate.event.ResetOriginalValuesPostLoadEventListener;
import com.liferay.portal.db.partition.DBPartitionUtil;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * @author Tina Tian
 */
public class GlobalEventListenerIntegrator implements Integrator {

	public static final GlobalEventListenerIntegrator INSTANCE =
		new GlobalEventListenerIntegrator();

	@Override
	public void disintegrate(
		SessionFactoryImplementor sessionFactoryImplementor,
		SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {
	}

	@Override
	public void integrate(
		Metadata metadata, SessionFactoryImplementor sessionFactoryImplementor,
		SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {

		EventListenerRegistry eventListenerRegistry =
			sessionFactoryServiceRegistry.getService(
				EventListenerRegistry.class);

		DBPartitionUtil.registerEventListeners(eventListenerRegistry);

		eventListenerRegistry.setListeners(
			EventType.LOAD, ResetOriginalValuesLoadEventListener.INSTANCE);
		eventListenerRegistry.setListeners(
			EventType.POST_LOAD,
			ResetOriginalValuesPostLoadEventListener.INSTANCE);
	}

}