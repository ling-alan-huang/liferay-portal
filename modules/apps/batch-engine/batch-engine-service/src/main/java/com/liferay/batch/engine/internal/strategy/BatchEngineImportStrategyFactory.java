/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.strategy;

import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.strategy.BatchEngineImportStrategy;

/**
 * @author Matija Petanjek
 */
public class BatchEngineImportStrategyFactory {

	public BatchEngineImportStrategy create(
		BatchEngineImportTask batchEngineImportTask) {

		if (batchEngineImportTask.getImportStrategy() ==
				BatchEngineImportTaskConstants.
					IMPORT_STRATEGY_ON_ERROR_CONTINUE) {

			return new OnErrorContinueBatchEngineImportStrategy(
				batchEngineImportTask.getBatchEngineImportTaskId(),
				batchEngineImportTask.getCompanyId(),
				batchEngineImportTask.getUserId());
		}

		return new OnErrorFailBatchEngineImportStrategy(
			batchEngineImportTask.getBatchEngineImportTaskId(),
			batchEngineImportTask.getCompanyId(),
			batchEngineImportTask.getUserId());
	}

}