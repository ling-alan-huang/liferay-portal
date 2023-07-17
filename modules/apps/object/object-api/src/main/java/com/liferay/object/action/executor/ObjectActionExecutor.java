/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.action.executor;

import com.liferay.object.exception.ObjectActionExecutorKeyException;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.UnicodeProperties;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public interface ObjectActionExecutor {

	public void execute(
			long companyId, UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception;

	public String getKey();

	public default boolean isAllowedCompany(long companyId) {
		return true;
	}

	public default boolean isAllowedObjectDefinition(
		String objectDefinitionName) {

		return true;
	}

	public default void validate(long companyId, String objectDefinitionName)
		throws PortalException {

		if (!isAllowedCompany(companyId)) {
			throw new ObjectActionExecutorKeyException(
				StringBundler.concat(
					"The object action executor key ", getKey(),
					" is not allowed for company ", companyId));
		}

		if (!isAllowedObjectDefinition(objectDefinitionName)) {
			throw new ObjectActionExecutorKeyException(
				StringBundler.concat(
					"The object action executor key ", getKey(),
					" is not allowed for object definition ",
					objectDefinitionName));
		}
	}

}