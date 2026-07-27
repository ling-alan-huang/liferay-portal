/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Objects;

/**
 * @author Shuyang Zhou
 */
public class SystemGroupGuestUserCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (!Objects.equals(getMethodName(detailAST), "addGroup")) {
			return;
		}

		String systemGroupConstantName = null;
		boolean guestUser = false;

		for (DetailAST parameterExprDetailAST :
				getParameterExprDetailASTs(detailAST)) {

			List<String> names = getNames(parameterExprDetailAST, true);

			if (names.contains("getGuestUserId")) {
				guestUser = true;
			}

			if (!names.contains("GroupConstants")) {
				continue;
			}

			for (String name : _SYSTEM_GROUP_CONSTANT_NAMES) {
				if (names.contains(name)) {
					systemGroupConstantName = name;
				}
			}
		}

		if ((systemGroupConstantName != null) && !guestUser) {
			log(detailAST, _MSG_USE_GUEST_USER, systemGroupConstantName);
		}
	}

	private static final String _MSG_USE_GUEST_USER = "system.group.guest.user";

	private static final String[] _SYSTEM_GROUP_CONSTANT_NAMES = {
		"CALENDAR", "CMS", "CONTROL_PANEL", "FORMS", "USER_PERSONAL_SITE"
	};

}