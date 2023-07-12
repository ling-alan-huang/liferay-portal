/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.strategy;

import com.liferay.batch.engine.internal.util.ItemIndexThreadLocal;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Collection;

/**
 * @author Matija Petanjek
 */
public class OnErrorContinueBatchEngineImportStrategy
	extends BaseBatchEngineImportStrategy {

	public OnErrorContinueBatchEngineImportStrategy(
		long batchEngineImportTaskId, long companyId, long userId) {

		_batchEngineImportTaskId = batchEngineImportTaskId;
		_companyId = companyId;
		_userId = userId;
	}

	@Override
	public <T> void apply(
			Collection<T> collection,
			UnsafeConsumer<T, Exception> unsafeConsumer)
		throws Exception {

		for (T item : collection) {
			try {
				unsafeConsumer.accept(item);
			}
			catch (Exception exception) {
				_log.error(exception);

				addBatchEngineImportTaskError(
					_companyId, _userId, _batchEngineImportTaskId,
					item.toString(), ItemIndexThreadLocal.get(item),
					exception.toString());
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OnErrorContinueBatchEngineImportStrategy.class);

	private final long _batchEngineImportTaskId;
	private final long _companyId;
	private final long _userId;

}