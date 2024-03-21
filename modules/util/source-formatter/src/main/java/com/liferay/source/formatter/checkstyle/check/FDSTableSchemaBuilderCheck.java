/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ListUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Objects;

/**
 * @author Alan Huang
 */
public class FDSTableSchemaBuilderCheck extends BaseBuilderCheck {

	@Override
	protected boolean allowNullValues() {
		return false;
	}

	@Override
	protected List<BaseBuilderCheck.BuilderInformation>
		doGetBuilderInformationList() {

		return ListUtil.fromArray(
			new BaseBuilderCheck.BuilderInformation(
				"FDSTableSchema", "FDSTableSchemaBuilder", "add"));
	}

	@Override
	protected String getAssignClassName(DetailAST assignDetailAST) {
//		return getNewInstanceTypeName(assignDetailAST);
		

		for (BaseBuilderCheck.BuilderInformation builderInformation :
				getBuilderInformationList()) {

			for (String className : getNames(assignDetailAST, true)) {
				if (Objects.equals(
						builderInformation.getBuilderClassName(), className)) {

					return null;
				}
			}
		}

		DetailAST parentDetailAST = assignDetailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.VARIABLE_DEF) {
			return getTypeName(parentDetailAST, false);
		}

		String variableName = getName(assignDetailAST);

		if (variableName != null) {
			return getVariableTypeName(assignDetailAST, variableName, false);
		}

		return null;
	
	}

	@Override
	protected List<String> getSupportsFunctionMethodNames() {
		return ListUtil.fromArray("add");
	}

	@Override
	protected boolean isSupportsNestedMethodCalls() {
		return true;
	}

}