/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ListUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

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
				"FDSTableSchemaBuilder", "FDSTableSchemaBuilderFactory",
				"addFDSTableSchemaField", "setSortable", "put", "putAll", "setProperty"));
	}

	@Override
	protected String getAssignClassName(DetailAST assignDetailAST) {
		DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
			return null;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) {
			return null;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return null;
		}

		List<String> names = getNames(firstChildDetailAST, false);

		if (names.size() != 2) {
			return null;
		}

		String methodCallClassName = names.get(0);
		String methodCallMethodName = names.get(1);

		
		String typeName = getVariableTypeName(
				firstChildDetailAST.getParent(), methodCallClassName, false);

		if (!typeName.equals("FDSTableSchemaBuilderFactory") || !methodCallMethodName.equals("create")) {
			return null;

		}

		return "FDSTableSchemaBuilder";
//		return getName(assignValueDetailAST);
	}

	@Override
	protected List<String> getSupportsFunctionMethodNames() {
		return ListUtil.fromArray("addFDSTableSchemaField");
	}

	@Override
	protected boolean isSupportsNestedMethodCalls() {
		return true;
	}

}