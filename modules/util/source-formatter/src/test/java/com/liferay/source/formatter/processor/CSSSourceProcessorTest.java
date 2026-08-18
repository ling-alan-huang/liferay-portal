/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import org.junit.Test;

/**
 * @author Alan Huang
 */
public class CSSSourceProcessorTest extends BaseSourceProcessorTestCase {

	@Test
	public void testIncorrectEmptyLines() throws Exception {
		test("IncorrectEmptyLines.testscss");
	}

	@Test
	public void testScssProperitesOrder() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"IncorrectScssProperitesOrder.testscss"
			).addExpectedMessage(
				"\"color\" should come after \"box-shadow\"", 4
			).addExpectedMessage(
				"\"box-shadow\" should come after \"background-color\"", 5
			));
	}

	@Test
	public void testScssVariablesOrder() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"IncorrectScssVariablesOrder.testscss"
			).addExpectedMessage(
				"\"$link-hover-color\" should come after \"$link-decoration\"",
				3
			).addExpectedMessage(
				"\"$link-decoration\" should come after \"$link-color\"", 4
			));
	}

}