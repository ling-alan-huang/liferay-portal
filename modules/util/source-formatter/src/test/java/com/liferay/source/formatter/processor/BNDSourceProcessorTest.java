/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.petra.string.StringBundler;

import org.junit.Test;

/**
 * @author Alan Huang
 */
public class BNDSourceProcessorTest extends BaseSourceProcessorTestCase {

	@Test
	public void testDoNotCopy() throws Exception {
		test("DefinitionKeysDoNotCopy/common.testbnd");
		test(
			"DefinitionKeysDoNotCopy/foo-impl/bnd.testbnd",
			"Do not use \"-donotcopy\" in bnd.bnd", 4);
		test("DefinitionKeysDoNotCopy/src/bar-impl/bnd.testbnd");
	}

	@Test
	public void testFormatBndInstructions() throws Exception {
		test("FormatBndInstructions1/app.testbnd");
		test(
			"FormatBndInstructions2/app.testbnd",
			"Deprecated apps that are not published on Marketplace should be " +
				"moved to the archived folder");
		test(
			SourceProcessorTestParameters.create(
				"FormatBndInstructions3/app.testbnd"
			).addExpectedMessage(
				"The \"Liferay-Releng-Restart-Required\" can only be set to " +
					"false if a POSHI tests exists"
			).addExpectedMessage(
				StringBundler.concat(
					"The \"Liferay-Releng-Suite\" can be blank or one of the ",
					"following values \"collaboration, commerce, ",
					"forms-and-workflow, foundation, static, web-experience\"")
			));
	}

	@Test
	public void testFormatDefinitionKeys() throws Exception {
		test("FormatDefinitionKeys1/common.testbnd");
		test(
			SourceProcessorTestParameters.create(
				"FormatDefinitionKeys2/common.testbnd"
			).addExpectedMessage(
				"Unknown key \"-fixupmessagess\""
			).addExpectedMessage(
				"Unknown key \"Liferay-Portal-ServerInfo\""
			));
	}

	@Test
	public void testIncludeResourcePackageJSON() throws Exception {
		test("IncludeResourcePackageJSON/src/bar-web/bnd.testbnd");
		test(
			SourceProcessorTestParameters.create(
				"IncludeResourcePackageJSON/foo-web/bnd.testbnd"
			).addDependentFileName(
				"IncludeResourcePackageJSON/foo-web/package.testjson"
			).addExpectedMessage(
				"Do not include \"package.json\", Gradle already includes it", 6
			).addExpectedMessage(
				"Do not include \"package.json\", Gradle already includes it", 7
			));
	}

	@Test
	public void testIncludeResourceTestClasses() throws Exception {
		test("IncludeResourceTestClasses/src/baz-test/bnd.testbnd");
		test(
			SourceProcessorTestParameters.create(
				"IncludeResourceTestClasses/bar-test/bnd.testbnd"
			).addDependentFileName(
				"IncludeResourceTestClasses/bar-test/build.testgradle"
			));
		test(
			SourceProcessorTestParameters.create(
				"IncludeResourceTestClasses/foo-test/bnd.testbnd"
			).addDependentFileName(
				"IncludeResourceTestClasses/foo-test/build.testgradle"
			).addExpectedMessage(
				"Do not use \"test-classes/integration\" in bnd.bnd in test " +
					"modules",
				4
			).addExpectedMessage(
				"Do not use \"test-classes/integration\" in bnd.bnd in test " +
					"modules",
				5
			));
	}

	@Test
	public void testIncorrectBundleActivator() throws Exception {
		test(
			"IncorrectBundleActivator1/bnd.testbnd",
			"Incorrect Bundle-Activator, it should match " +
				"\"Bundle-SymbolicName\"");
		test(
			"IncorrectBundleActivator2/bnd.testbnd",
			"Incorrect Bundle-Activator, it should end with " +
				"\"BundleActivator\"");
		test(
			"IncorrectBundleActivator3/bnd.testbnd",
			"Incorrect Bundle-Activator, it should match " +
				"\"Bundle-SymbolicName\"");
	}

	@Test
	public void testIncorrectWhitespace() throws Exception {
		test("IncorrectWhitespace.testbnd");
	}

	@Test
	public void testRemoveInternalPrivatePackages() throws Exception {
		test("RemoveInternalPrivatePackages.testbnd");
	}

	@Test
	public void testRemoveNoValueDefinitionKey() throws Exception {
		test("RemoveNoValueDefinitionKey1.testbnd");
		test("RemoveNoValueDefinitionKey2.testbnd");
	}

}