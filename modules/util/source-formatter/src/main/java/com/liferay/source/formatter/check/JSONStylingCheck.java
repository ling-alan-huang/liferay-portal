/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Iterator;

/**
 * @author Hugo Huijser
 */
public class JSONStylingCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (Validator.isNull(content)) {
			return StringPool.BLANK;
		}

		try {
			if (StringUtil.startsWith(
					StringUtil.trim(content), StringPool.OPEN_BRACKET)) {

				Object object = _checkJSONArray(
					new JSONArrayImpl(content), null);

				if (object instanceof JSONObject) {
					return JSONUtil.toString((JSONObject)object);
				}

				return JSONUtil.toString((JSONArray)object);
			}

			if (content.endsWith("\n") && fileName.endsWith("/package.json")) {
				JSONObject jsonObject = _checkJSONObject(
					new JSONObjectImpl(content));

				return JSONUtil.toString(jsonObject) + "\n";
			}

			return JSONUtil.toString(
				_checkJSONObject(new JSONObjectImpl(content)));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return content;
		}
	}

	private Object _checkJSONArray(JSONArray jsonArray, String key) {
		if (jsonArray.length() == 0) {
			return jsonArray;
		}

		JSONArray newJSONArray = new JSONArrayImpl();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (jsonObject != null) {
				JSONObject newJSONObject = _checkJSONObject(jsonObject);

				newJSONArray.put(newJSONObject);

				continue;
			}

			if (StringUtil.equals(key, "#cdata-value")) {
				break;
			}

			newJSONArray.put(jsonArray.get(i));
		}

		if (newJSONArray.length() == 0) {
			StringBundler sb = new StringBundler(jsonArray.length());

			for (int i = 0; i < jsonArray.length(); i++) {
				sb.append(StringUtil.trim(jsonArray.getString(i)));
			}

			try {
				return new JSONObjectImpl(sb.toString());
			}
			catch (JSONException jsonException) {
				return jsonArray;
			}
		}

		return newJSONArray;
	}

	private JSONObject _checkJSONObject(JSONObject jsonObject) {
		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			JSONArray jsonArray = jsonObject.getJSONArray(key);

			if (jsonArray == null) {
				JSONObject childJSONObject = jsonObject.getJSONObject(key);

				if (childJSONObject != null) {
					jsonObject.put(key, _checkJSONObject(childJSONObject));

					continue;
				}

				jsonObject.put(key, jsonObject.get(key));
			}
			else {
				jsonObject.put(key, _checkJSONArray(jsonArray, key));
			}
		}

		return jsonObject;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSONStylingCheck.class);

}