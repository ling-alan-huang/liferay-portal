/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.reindexer;

import com.liferay.portal.search.spi.reindexer.BulkReindexer;

/**
 * @author André de Oliveira
 */
public interface BulkReindexersRegistry {

	public BulkReindexer getBulkReindexer(String className);

}