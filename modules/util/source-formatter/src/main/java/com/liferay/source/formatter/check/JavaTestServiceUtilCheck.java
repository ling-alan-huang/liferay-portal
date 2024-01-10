package com.liferay.source.formatter.check;

import java.util.List;

import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaTerm;

public class JavaTestServiceUtilCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) throws Exception {

		if (!fileName.endsWith("Test.java")) {
			return javaTerm.getContent();
		}

		List<String> importNames = JavaSourceUtil.getImportNames(fileContent);

		System.out.println("This is a test from Seiphon!");

		for (String importName : importNames) {
			System.out.println(importName);
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return null;
	}

}
