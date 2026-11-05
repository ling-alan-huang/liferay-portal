/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import org.junit.Test;

/**
 * @author Alan Huang
 */
public class GradleSourceProcessorTest extends BaseSourceProcessorTestCase {

	@Test
	public void testBouncyCastleFIPS() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"BouncyCastleFIPS.testgradle"
			).addExpectedMessage(
				"Do not use non-FIPS BouncyCastle, see LPD-90318", 3
			).addExpectedMessage(
				"Do not use non-FIPS BouncyCastle, see LPD-90318", 4
			));
	}

	@Test
	public void testMissingLineBreaksAroundCurlyBraces() throws Exception {
		test("MissingLineBreaksAroundCurlyBraces.testgradle");
	}

	@Test
	public void testSortDependencies() throws Exception {
		test("SortDependencies.testgradle");
	}

	@Test
	public void testSortFileNames() throws Exception {
		test("SortFileNames.testgradle");
	}

	@Test
	public void testSortMapKeys() throws Exception {
		test("SortMapKeys1.testgradle");
		test("SortMapKeys2.testgradle");
		test("SortMapKeys3.testgradle");
		test("SortMapKeys4.testgradle");
		test("SortMapKeys5.testgradle");
	}

	@Test
	public void testStyling() throws Exception {
		test("Styling.testgradle");
	}

	@Test
	public void testUnusedProjectDependencies() throws Exception {
		String dirName =
			"gradle/unused-project-dependencies/modules/apps/sample/";

		SourceProcessorTestParameters sourceProcessorTestParameters =
			SourceProcessorTestParameters.create(
				dirName + "sample-web/build.testgradle");

		for (String dependentFileName :
				new String[] {
					"sample-api/bnd.testbnd",
					"sample-api/src/main/java/com/liferay/sample/api" +
						"/SampleAPI.testjava",
					"sample-base/bnd.testbnd",
					"sample-base/src/main/java/com/liferay/sample/base" +
						"/BaseSample.testjava",
					"sample-bnd/bnd.testbnd",
					"sample-bnd/src/main/java/com/liferay/sample/bnd" +
						"/SampleBND.testjava",
					"sample-function/bnd.testbnd",
					"sample-function/src/main/java/com/liferay/sample" +
						"/function/SampleFunction.testjava",
					"sample-spi/bnd.testbnd",
					"sample-spi/src/main/java/com/liferay/sample/spi" +
						"/SampleSPI.testjava",
					"sample-taglib/bnd.testbnd",
					"sample-taglib/src/main/java/com/liferay/sample/taglib" +
						"/SampleTag.testjava",
					"sample-taglib/src/main/resources/META-INF" +
						"/liferay-sample.testtld",
					"sample-test-util/bnd.testbnd",
					"sample-test-util/src/main/java/com/liferay/sample/test" +
						"/util/SampleTestUtil.testjava",
					"sample-unused/bnd.testbnd",
					"sample-unused/src/main/java/com/liferay/sample/unused" +
						"/SampleUnused.testjava",
					"sample-web/bnd.testbnd",
					"sample-web/src/main/java/com/liferay/sample/web/internal" +
						"/SampleWeb.testjava",
					"sample-web/src/main/resources/META-INF/resources" +
						"/view.testjsp",
					"sample-web/src/testIntegration/java/com/liferay/sample" +
						"/web/internal/test/SampleWebTest.testjava"
				}) {

			sourceProcessorTestParameters.addDependentFileName(
				dirName + dependentFileName);
		}

		test(
			sourceProcessorTestParameters.addExpectedMessage(
				"The project dependency \":apps:sample:sample-unused\" is " +
					"unused because no source, JSP, taglib, or \"bnd.bnd\" " +
						"reference to it was found",
				8
			).addExpectedMessage(
				"The project dependency \":apps:sample:sample-unused\" is " +
					"unused because no source, JSP, taglib, or \"bnd.bnd\" " +
						"reference to it was found",
				11
			));
	}

}