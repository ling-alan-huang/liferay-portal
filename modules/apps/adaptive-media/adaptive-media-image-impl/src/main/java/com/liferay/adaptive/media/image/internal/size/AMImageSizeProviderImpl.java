/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.size;

import com.liferay.adaptive.media.image.internal.configuration.AMImageConfiguration;
import com.liferay.adaptive.media.image.size.AMImageSizeProvider;
import com.liferay.document.library.configuration.DLFileEntryConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Roberto Díaz
 */
@Component(
	configurationPid = {
		"com.liferay.adaptive.media.image.internal.configuration.AMImageConfiguration",
		"com.liferay.document.library.configuration.DLFileEntryConfiguration"
	},
	service = AMImageSizeProvider.class
)
public class AMImageSizeProviderImpl implements AMImageSizeProvider {

	@Override
	public long getImageMaxSize() {
		if (_dlFileEntryConfiguration.previewableProcessorMaxSize() >
				_amImageConfiguration.imageMaxSize()) {

			return _amImageConfiguration.imageMaxSize();
		}

		return _dlFileEntryConfiguration.previewableProcessorMaxSize();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_amImageConfiguration = ConfigurableUtil.createConfigurable(
			AMImageConfiguration.class, properties);
		_dlFileEntryConfiguration = ConfigurableUtil.createConfigurable(
			DLFileEntryConfiguration.class, properties);
	}

	private volatile AMImageConfiguration _amImageConfiguration;
	private volatile DLFileEntryConfiguration _dlFileEntryConfiguration;

}