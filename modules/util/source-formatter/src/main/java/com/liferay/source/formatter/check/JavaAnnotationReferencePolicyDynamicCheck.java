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

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;

import java.io.IOException;

import java.util.List;
import java.util.Map;

/**
 * @author Alan Huang
 */
public class JavaAnnotationReferencePolicyDynamicCheck
	extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws IOException {

		JavaClass javaClass = (JavaClass)javaTerm;

		String content = javaClass.getContent();

		if (javaClass.getParentJavaClass() != null) {
			return content;
		}

		List<String> annotationsBlocks = SourceUtil.getAnnotationsBlocks(
			content);

		for (String annotationsBlock : annotationsBlocks) {
			String indent = SourceUtil.getIndent(annotationsBlock);

			String newAnnotationsBlock = _formatAnnotations(
				annotationsBlock, indent);

			content = StringUtil.replace(
				content, "\n" + annotationsBlock, "\n" + newAnnotationsBlock);
		}

		return content;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private String _formatAnnotations(String annotationsBlock, String indent)
		throws IOException {

		List<String> annotations = SourceUtil.splitAnnotations(
			annotationsBlock, indent);

		for (String annotation : annotations) {
			annotation = annotation.trim();

			if (!annotation.startsWith("@Reference")) {
				return annotationsBlock;
			}

			Map<String, String> annotationMemberValuePair =
				SourceUtil.getAnnotationMemberValuePair(annotation);

			String cardinality = annotationMemberValuePair.get("cardinality");

			if (Validator.isNull(cardinality) ||
				!cardinality.equals("ReferenceCardinality.OPTIONAL")) {

				return annotationsBlock;
			}

			String policyOption = annotationMemberValuePair.get("policyOption");

			if (Validator.isNull(policyOption) ||
				!policyOption.equals("ReferencePolicyOption.GREEDY")) {

				return annotationsBlock;
			}

			String policy = annotationMemberValuePair.get("policy");

			if (Validator.isNotNull(policy) &&
				policy.equals("ReferencePolicy.DYNAMIC")) {

				return annotationsBlock;
			}

			int x = annotationsBlock.indexOf(
				"policyOption = ReferencePolicyOption.GREEDY");

			return StringUtil.insert(
				annotationsBlock, "policy = ReferencePolicy.DYNAMIC,", x);
		}

		return annotationsBlock;
	}

}