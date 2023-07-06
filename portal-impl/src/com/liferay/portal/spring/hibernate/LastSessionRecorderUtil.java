/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.transaction.NewTransactionLifecycleListener;
import com.liferay.portal.kernel.transaction.TransactionAttribute;
import com.liferay.portal.kernel.transaction.TransactionLifecycleListener;
import com.liferay.portal.kernel.transaction.TransactionStatus;

import org.hibernate.Session;

/**
 * @author Shuyang Zhou
 */
public class LastSessionRecorderUtil {

	public static final TransactionLifecycleListener
		TRANSACTION_LIFECYCLE_LISTENER = new NewTransactionLifecycleListener() {

			@Override
			protected void doCreated(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus) {

				syncLastSessionState();
			}

		};

	public static void syncLastSessionState() {
		Session session = _lastSessionThreadLocal.get();

		if ((session != null) && session.isOpen()) {
			try {
				session.flush();

				session.clear();
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}
		}
	}

	protected static void setLastSession(Session session) {
		_lastSessionThreadLocal.set(session);
	}

	private static final ThreadLocal<Session> _lastSessionThreadLocal =
		new CentralizedThreadLocal<>(
			LastSessionRecorderUtil.class.getName() +
				"._lastSessionThreadLocal");

}