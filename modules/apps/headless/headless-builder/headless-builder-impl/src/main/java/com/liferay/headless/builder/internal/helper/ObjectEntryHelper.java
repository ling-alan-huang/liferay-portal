/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.helper;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Miguel Barcos
 * @authot Carlos Correa
 * @author Alejandro Tardín
 */
@Component(service = ObjectEntryHelper.class)
public class ObjectEntryHelper {

	public List<ObjectEntry> getObjectEntries(
			long companyId, String filterString, List<String> nestedFields,
			String objectDefinitionExternalReferenceCode)
		throws Exception {

		NestedFieldsContext nestedFieldsContext = new NestedFieldsContext(
			1, nestedFields);

		NestedFieldsContext oldNestedFieldsContext =
			NestedFieldsContextThreadLocal.getAndSetNestedFieldsContext(
				nestedFieldsContext);

		try {
			return getObjectEntries(
				companyId, filterString, objectDefinitionExternalReferenceCode);
		}
		finally {
			NestedFieldsContextThreadLocal.setNestedFieldsContext(
				oldNestedFieldsContext);
		}
	}

	public List<ObjectEntry> getObjectEntries(
			long companyId, String filterString,
			String objectDefinitionExternalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode, companyId);

		if (objectDefinition == null) {
			return null;
		}

		PermissionThreadLocal.setPermissionChecker(
			_permissionCheckerFactory.create(
				_userLocalService.getUser(objectDefinition.getUserId())));

		Page<ObjectEntry> page = _objectEntryManager.getObjectEntries(
			companyId, objectDefinition, null, null,
			new DefaultDTOConverterContext(
				false, null, null, null, null, LocaleUtil.getSiteDefault(),
				null, _userLocalService.getUser(objectDefinition.getUserId())),
			filterString, Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			null, null);

		return new ArrayList<>(page.getItems());
	}

	public ObjectEntry getObjectEntry(
			long companyId, String filterString,
			String objectDefinitionExternalReferenceCode)
		throws Exception {

		List<ObjectEntry> objectEntries = getObjectEntries(
			companyId, filterString, objectDefinitionExternalReferenceCode);

		if (ListUtil.isEmpty(objectEntries)) {
			return null;
		}

		return objectEntries.get(0);
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private UserLocalService _userLocalService;

}