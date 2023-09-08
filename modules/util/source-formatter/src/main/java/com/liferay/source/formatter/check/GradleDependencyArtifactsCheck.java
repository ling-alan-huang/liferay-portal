/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class GradleDependencyArtifactsCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		return _renameDependencyNames(absolutePath, content);
	}

	private String _getArtifactString(String artifact) {
		String[] array = StringUtil.split(artifact, CharPool.COLON);

		if (array.length != 2) {
			return null;
		}

		return StringBundler.concat(
			"group: \"", array[0], "\", name: \"", array[1], "\"");
	}

	private String _renameDependencyNames(String absolutePath, String content) {
		List<String> renameArtifacts = getAttributeValues(
			_RENAME_ARTIFACTS_KEY, absolutePath);

		for (String renameArtifact : renameArtifacts) {
			String[] renameArtifactArray = StringUtil.split(
				renameArtifact, "->");

			if (renameArtifactArray.length != 2) {
				continue;
			}

			String newArtifactString = _getArtifactString(
				renameArtifactArray[1]);
			String oldArtifactString = _getArtifactString(
				renameArtifactArray[0]);

			if ((newArtifactString != null) && (oldArtifactString != null)) {
				content = StringUtil.replace(
					content, oldArtifactString, newArtifactString);
			}
		}

		return content;
	}

	private static final String _RENAME_ARTIFACTS_KEY = "renameArtifacts";

}