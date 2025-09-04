/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action.test;

import org.junit.Assert;

/**
 * @author Andrew Betts
 */
public class EditServerMVCActionCommandTest {

	private void _testProcessAction() {
		try (AA
			aa =
					new AA();

			BB bb =
				new BB()) {

			Assert.fail();
		}
	}

}