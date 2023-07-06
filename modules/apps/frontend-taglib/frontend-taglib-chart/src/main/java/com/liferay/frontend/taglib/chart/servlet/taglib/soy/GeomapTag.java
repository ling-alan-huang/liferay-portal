/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.chart.servlet.taglib.soy;

import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.frontend.taglib.chart.internal.js.loader.modules.extender.npm.NPMResolverProvider;
import com.liferay.frontend.taglib.chart.model.geomap.GeomapConfig;
import com.liferay.frontend.taglib.soy.servlet.taglib.TemplateRendererTag;
import com.liferay.petra.string.StringPool;

import java.util.Map;

/**
 * @author Julien Castelain
 */
public class GeomapTag extends TemplateRendererTag {

	public GeomapTag() {
		_moduleBaseName = "Geomap";
	}

	@Override
	public int doStartTag() {
		setTemplateNamespace("ClayGeomap.render");

		return super.doStartTag();
	}

	@Override
	public String getModule() {
		NPMResolver npmResolver = NPMResolverProvider.getNPMResolver();

		if (npmResolver == null) {
			return StringPool.BLANK;
		}

		return npmResolver.resolveModuleName(
			"clay-charts/lib/" + _moduleBaseName);
	}

	public void setConfig(GeomapConfig geomapConfig) {
		for (Map.Entry<String, Object> entry : geomapConfig.entrySet()) {
			putValue(entry.getKey(), entry.getValue());
		}
	}

	public void setId(String id) {
		putValue("id", id);
	}

	private final String _moduleBaseName;

}