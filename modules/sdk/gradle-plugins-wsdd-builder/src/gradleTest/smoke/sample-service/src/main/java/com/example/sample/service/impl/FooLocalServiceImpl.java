/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.example.sample.service.impl;

import com.example.sample.service.base.FooLocalServiceBaseImpl;

/**
 * The implementation of the foo local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the {@link
 * com.example.sample.service.FooLocalService} interface. <p> This is a local
 * service. Methods of this service will not have security checks based on the
 * propagated JAAS credentials because this service can only be accessed from
 * within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see    FooLocalServiceBaseImpl
 * @see    com.example.sample.service.FooLocalServiceUtil
 */
public class FooLocalServiceImpl extends FooLocalServiceBaseImpl {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link com.example.sample.service.FooLocalServiceUtil} to access the foo local service.
	 */

}