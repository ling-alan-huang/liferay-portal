/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.instance.internal;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterRegistry;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.KeyValuePair;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "ddm.data.provider.instance.id=ddm-storage-types",
	service = DDMDataProvider.class
)
public class DDMStorageTypesDataProvider implements DDMDataProvider {

	@Override
	public DDMDataProviderResponse getData(
		DDMDataProviderRequest ddmDataProviderRequest) {

		HttpServletRequest httpServletRequest =
			ddmDataProviderRequest.getParameter(
				"httpServletRequest", HttpServletRequest.class);

		List<KeyValuePair> keyValuePairs = TransformUtil.transform(
			ddmStorageAdapterRegistry.getDDMStorageAdapterTypes(),
			ddmStorageAdapterType -> {
				if (ddmStorageAdapterType.equals(StorageType.JSON.getValue())) {
					return null;
				}

				if (httpServletRequest == null) {
					return new KeyValuePair(
						ddmStorageAdapterType, ddmStorageAdapterType);
				}

				return new KeyValuePair(
					ddmStorageAdapterType,
					_language.get(
						httpServletRequest,
						ddmStorageAdapterType + "[stands-for]",
						_language.get(
							httpServletRequest, ddmStorageAdapterType)));
			});

		DDMDataProviderResponse.Builder builder =
			DDMDataProviderResponse.Builder.newBuilder();

		builder.withOutput("Default-Output", keyValuePairs);

		return builder.build();
	}

	@Override
	public Class<?> getSettings() {
		throw new UnsupportedOperationException();
	}

	@Reference
	protected DDMStorageAdapterRegistry ddmStorageAdapterRegistry;

	@Reference
	private Language _language;

}