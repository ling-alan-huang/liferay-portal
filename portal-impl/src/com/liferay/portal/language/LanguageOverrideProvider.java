/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language;

import java.util.Locale;
import java.util.Set;

/**
 * @author Drew Brokke
 */
public interface LanguageOverrideProvider {

	public String get(String key, Locale locale);

	public Set<String> keySet(Locale locale);

}