/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.content.processor;

import com.liferay.osgi.util.StringPlus;

import java.util.List;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Gergely Mathe
 * @author Máté Thurzó
 */
public class ExportImportContentProcessorRegistryUtil {

	public static ExportImportContentProcessor<String>
		getExportImportContentProcessor(String className) {

		return _exportImportContentProcessorRegistryUtil.
			_getExportImportContentProcessor(className);
	}

	private class ExportImportContentProcessorServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ExportImportContentProcessor<String>,
			 ExportImportContentProcessor<String>> {

		@Override
		public void modifiedService(
			ServiceReference<ExportImportContentProcessor<String>>
				serviceReference,
			ExportImportContentProcessor<String> exportImportContentProcessor) {

			removedService(serviceReference, exportImportContentProcessor);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<ExportImportContentProcessor<String>>
				serviceReference,
			ExportImportContentProcessor<String> exportImportContentProcessor) {

			_bundleContext.ungetService(serviceReference);

			List<String> modelClassNames = StringPlus.asList(
				serviceReference.getProperty("model.class.name"));

			for (String modelClassName : modelClassNames) {
				_exportImportContentProcessors.remove(modelClassName);
			}
		}

	}

}