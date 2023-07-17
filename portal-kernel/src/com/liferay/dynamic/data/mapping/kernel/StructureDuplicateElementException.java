/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.kernel;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Rafael Praxedes
 */
public class StructureDuplicateElementException extends PortalException {

	public StructureDuplicateElementException() {
	}

	public StructureDuplicateElementException(String msg) {
		super(msg);
	}

	public StructureDuplicateElementException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public StructureDuplicateElementException(Throwable throwable) {
		super(throwable);
	}

}