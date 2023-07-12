/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.jaxrs.exception.mapper;

import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * @author Luis Miguel Barcos
 */
public class ObjectValidationRuleEngineExceptionMapper
	extends BaseExceptionMapper<ObjectValidationRuleEngineException> {

	public ObjectValidationRuleEngineExceptionMapper(Language language) {
		_language = language;
	}

	@Override
	protected Problem getProblem(
		ObjectValidationRuleEngineException
			objectValidationRuleEngineException) {

		String messageKey = objectValidationRuleEngineException.getMessageKey();

		if (messageKey == null) {
			messageKey = objectValidationRuleEngineException.getMessage();
		}

		return new Problem(
			Response.Status.BAD_REQUEST,
			_language.get(_acceptLanguage.getPreferredLocale(), messageKey));
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	private final Language _language;

}