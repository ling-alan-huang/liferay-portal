/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.kernel;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Leonardo Barros
 */
public class StorageFieldRequiredException extends PortalException {

	public StorageFieldRequiredException() {
	}

	public StorageFieldRequiredException(String msg) {
		super(msg);
	}

	public StorageFieldRequiredException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public StorageFieldRequiredException(Throwable throwable) {
		super(throwable);
	}

}