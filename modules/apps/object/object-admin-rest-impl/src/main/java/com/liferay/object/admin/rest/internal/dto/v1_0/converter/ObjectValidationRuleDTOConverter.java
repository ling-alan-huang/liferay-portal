/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.dto.v1_0.converter;

import com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRule;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	property = "dto.class.name=com.liferay.object.model.ObjectValidationRule",
	service = DTOConverter.class
)
public class ObjectValidationRuleDTOConverter
	implements DTOConverter
		<com.liferay.object.model.ObjectValidationRule, ObjectValidationRule> {

	@Override
	public String getContentType() {
		return ObjectValidationRule.class.getSimpleName();
	}

	@Override
	public ObjectValidationRule toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.object.model.ObjectValidationRule
				serviceBuilderObjectValidationRule)
		throws PortalException {

		if (serviceBuilderObjectValidationRule == null) {
			return null;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				serviceBuilderObjectValidationRule.getObjectDefinitionId());

		return new ObjectValidationRule() {
			{
				actions = dtoConverterContext.getActions();
				active = serviceBuilderObjectValidationRule.isActive();
				dateCreated =
					serviceBuilderObjectValidationRule.getCreateDate();
				dateModified =
					serviceBuilderObjectValidationRule.getModifiedDate();
				engine = serviceBuilderObjectValidationRule.getEngine();
				engineLabel = _language.get(
					dtoConverterContext.getLocale(),
					serviceBuilderObjectValidationRule.getEngine());
				errorLabel = LocalizedMapUtil.getLanguageIdMap(
					serviceBuilderObjectValidationRule.getErrorLabelMap());
				id =
					serviceBuilderObjectValidationRule.
						getObjectValidationRuleId();
				name = LocalizedMapUtil.getLanguageIdMap(
					serviceBuilderObjectValidationRule.getNameMap());
				objectDefinitionExternalReferenceCode =
					objectDefinition.getExternalReferenceCode();
				objectDefinitionId =
					serviceBuilderObjectValidationRule.getObjectDefinitionId();
				script = serviceBuilderObjectValidationRule.getScript();
			}
		};
	}

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}