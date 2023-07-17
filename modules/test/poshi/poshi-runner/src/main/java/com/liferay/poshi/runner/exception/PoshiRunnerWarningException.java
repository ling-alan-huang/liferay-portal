/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class PoshiRunnerWarningException extends Exception {

	public static void addException(
		PoshiRunnerWarningException poshiRunnerWarningException) {

		_poshiRunnerWarningExceptions.add(poshiRunnerWarningException);
	}

	public static List<PoshiRunnerWarningException>
		getPoshiRunnerWarningExceptions() {

		return _poshiRunnerWarningExceptions;
	}

	public PoshiRunnerWarningException(String msg) {
		super(msg);

		_poshiRunnerWarningExceptions.add(this);
	}

	public PoshiRunnerWarningException(String msg, Throwable throwable) {
		super(msg, throwable);

		_poshiRunnerWarningExceptions.add(this);
	}

	private static final List<PoshiRunnerWarningException>
		_poshiRunnerWarningExceptions = new ArrayList<>();

}