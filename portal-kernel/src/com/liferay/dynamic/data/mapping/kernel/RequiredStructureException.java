/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.kernel;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Rafael Praxedes
 */
public class RequiredStructureException extends PortalException {

	public RequiredStructureException() {
	}

	public RequiredStructureException(String msg) {
		super(msg);
	}

	public RequiredStructureException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public RequiredStructureException(Throwable throwable) {
		super(throwable);
	}

}