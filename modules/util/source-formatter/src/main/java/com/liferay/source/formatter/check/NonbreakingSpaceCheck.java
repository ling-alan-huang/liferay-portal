/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;

/**
 * @author Hugo Huijser
 */
public class NonbreakingSpaceCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		String shortFileName = SourceUtil.getShortFileName(fileName);

		if (shortFileName.matches("Language_.+\\.properties")) {
			return content;
		}

		return StringUtil.replace(
			content, CharPool.NO_BREAK_SPACE, CharPool.SPACE);
	}

}