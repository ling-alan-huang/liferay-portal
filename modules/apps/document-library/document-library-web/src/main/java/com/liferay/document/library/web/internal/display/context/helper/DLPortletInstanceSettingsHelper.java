/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.helper;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.KeyValuePairComparator;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.view.count.ViewCountManagerUtil;
import com.liferay.portal.util.PropsValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Iván Zaera
 */
public class DLPortletInstanceSettingsHelper {

	public DLPortletInstanceSettingsHelper(DLRequestHelper dlRequestHelper) {
		_dlRequestHelper = dlRequestHelper;
	}

	public List<KeyValuePair> getAvailableDisplayViews() {
		if (_availableDisplayViewKeyValuePairs == null) {
			_populateDisplayViews();
		}

		return _availableDisplayViewKeyValuePairs;
	}

	public List<KeyValuePair> getAvailableEntryColumns() {
		if (_availableEntryColumnKeyValuePairs == null) {
			_populateEntryColumns();
		}

		return _availableEntryColumnKeyValuePairs;
	}

	public List<KeyValuePair> getAvailableMimeTypes() {
		if (_availableMimeTypeKeyValuePairs == null) {
			_populateMimeTypes();
		}

		return _availableMimeTypeKeyValuePairs;
	}

	public List<KeyValuePair> getCurrentDisplayViews() {
		if (_currentDisplayViewKeyValuePairs == null) {
			_populateDisplayViews();
		}

		return _currentDisplayViewKeyValuePairs;
	}

	public List<KeyValuePair> getCurrentEntryColumns() {
		if (_currentEntryColumnKeyValuePairs == null) {
			_populateEntryColumns();
		}

		return _currentEntryColumnKeyValuePairs;
	}

	public List<KeyValuePair> getCurrentMimeTypes() {
		if (_currentMimeTypeKeyValuePairs == null) {
			_populateMimeTypes();
		}

		return _currentMimeTypeKeyValuePairs;
	}

	public String[] getEntryColumns() {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String[] entryColumns = dlPortletInstanceSettings.getEntryColumns();

		String portletName = _dlRequestHelper.getPortletName();

		if (!isShowActions()) {
			entryColumns = ArrayUtil.remove(entryColumns, "action");
		}
		else if (!portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY) &&
				 !portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN) &&
				 !ArrayUtil.contains(entryColumns, "action")) {

			entryColumns = ArrayUtil.append(entryColumns, "action");
		}

		return entryColumns;
	}

	public Folder getRootFolder() throws PortalException {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String rootFolderExternalReferenceCode =
			dlPortletInstanceSettings.getRootFolderExternalReferenceCode();

		if (Validator.isBlank(rootFolderExternalReferenceCode)) {
			return null;
		}

		ThemeDisplay themeDisplay = _dlRequestHelper.getThemeDisplay();

		Group selectedGroup =
			GroupLocalServiceUtil.getGroupByExternalReferenceCode(
				dlPortletInstanceSettings.
					getSelectedGroupExternalReferenceCode(),
				themeDisplay.getCompanyId());

		return DLAppLocalServiceUtil.getFolderByExternalReferenceCode(
			rootFolderExternalReferenceCode, selectedGroup.getGroupId());
	}

	public long getRootFolderId() throws PortalException {
		Folder rootFolder = getRootFolder();

		if (rootFolder == null) {
			return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		return rootFolder.getFolderId();
	}

	public long getSelectedRepositoryId() {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String selectedGroupExternalReferenceCode =
			dlPortletInstanceSettings.getSelectedGroupExternalReferenceCode();

		if (Validator.isBlank(selectedGroupExternalReferenceCode)) {
			return 0;
		}

		try {
			ThemeDisplay themeDisplay = _dlRequestHelper.getThemeDisplay();

			Group selectedGroup =
				GroupLocalServiceUtil.getGroupByExternalReferenceCode(
					selectedGroupExternalReferenceCode,
					themeDisplay.getCompanyId());

			String selectedRepositoryExternalReferenceCode =
				dlPortletInstanceSettings.
					getSelectedRepositoryExternalReferenceCode();

			if (Validator.isBlank(selectedRepositoryExternalReferenceCode)) {
				return selectedGroup.getGroupId();
			}

			Repository selectedRepository =
				RepositoryLocalServiceUtil.getRepositoryByExternalReferenceCode(
					selectedRepositoryExternalReferenceCode,
					selectedGroup.getGroupId());

			return selectedRepository.getRepositoryId();
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	public boolean isShowActions() {
		String portletName = _dlRequestHelper.getPortletName();
		String portletResource = _dlRequestHelper.getPortletResource();

		if (portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN) ||
			portletName.equals(PortletKeys.MY_WORKFLOW_TASK) ||
			portletResource.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN) ||
			portletResource.equals(PortletKeys.MY_WORKFLOW_TASK)) {

			return true;
		}

		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		return dlPortletInstanceSettings.isShowActions();
	}

	public boolean isShowSearch() {
		String portletName = _dlRequestHelper.getPortletName();

		if (portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {
			return true;
		}

		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		return dlPortletInstanceSettings.isShowFoldersSearch();
	}

	public boolean isShowTabs() {
		String portletName = _dlRequestHelper.getPortletName();

		return portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);
	}

	private String[] _getAllEntryColumns() {
		String allEntryColumns = "name,description,size,status";

		if (ViewCountManagerUtil.isViewCountEnabled(
				ClassNameLocalServiceUtil.getClassNameId(
					DLFileEntryConstants.getClassName()))) {

			allEntryColumns += ",downloads";
		}

		if (isShowActions()) {
			allEntryColumns += ",action";
		}

		allEntryColumns += ",modified-date,create-date";

		return StringUtil.split(allEntryColumns);
	}

	private void _populateDisplayViews() {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String[] displayViews = dlPortletInstanceSettings.getDisplayViews();

		_currentDisplayViewKeyValuePairs = new ArrayList<>();

		for (String displayView : displayViews) {
			_currentDisplayViewKeyValuePairs.add(
				new KeyValuePair(
					displayView,
					LanguageUtil.get(
						_dlRequestHelper.getLocale(),
						_displayViews.get(displayView))));
		}

		Arrays.sort(displayViews);

		_availableDisplayViewKeyValuePairs = new ArrayList<>();

		Set<String> allDisplayViews = SetUtil.fromArray(
			PropsValues.DL_DISPLAY_VIEWS);

		for (String displayView : allDisplayViews) {
			if (Arrays.binarySearch(displayViews, displayView) < 0) {
				_availableDisplayViewKeyValuePairs.add(
					new KeyValuePair(
						displayView,
						LanguageUtil.get(
							_dlRequestHelper.getLocale(),
							_displayViews.get(displayView))));
			}
		}

		_availableDisplayViewKeyValuePairs = ListUtil.sort(
			_availableDisplayViewKeyValuePairs,
			new KeyValuePairComparator(false, true));
	}

	private void _populateEntryColumns() {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String[] entryColumns = dlPortletInstanceSettings.getEntryColumns();

		_currentEntryColumnKeyValuePairs = new ArrayList<>();

		for (String entryColumn : entryColumns) {
			if (entryColumn.equals("action") && !isShowActions()) {
				continue;
			}

			_currentEntryColumnKeyValuePairs.add(
				new KeyValuePair(
					entryColumn,
					LanguageUtil.get(
						_dlRequestHelper.getLocale(), entryColumn)));
		}

		Arrays.sort(entryColumns);

		_availableEntryColumnKeyValuePairs = new ArrayList<>();

		Set<String> allEntryColumns = SetUtil.fromArray(_getAllEntryColumns());

		for (String entryColumn : allEntryColumns) {
			if (Arrays.binarySearch(entryColumns, entryColumn) < 0) {
				_availableEntryColumnKeyValuePairs.add(
					new KeyValuePair(
						entryColumn,
						LanguageUtil.get(
							_dlRequestHelper.getLocale(), entryColumn)));
			}
		}

		_availableEntryColumnKeyValuePairs = ListUtil.sort(
			_availableEntryColumnKeyValuePairs,
			new KeyValuePairComparator(false, true));
	}

	private void _populateMimeTypes() {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String[] mediaGalleryMimeTypes =
			dlPortletInstanceSettings.getMimeTypes();

		Arrays.sort(mediaGalleryMimeTypes);

		ThemeDisplay themeDisplay = _dlRequestHelper.getThemeDisplay();

		_currentMimeTypeKeyValuePairs = new ArrayList<>();

		for (String mimeType : mediaGalleryMimeTypes) {
			_currentMimeTypeKeyValuePairs.add(
				new KeyValuePair(
					mimeType,
					LanguageUtil.get(themeDisplay.getLocale(), mimeType)));
		}

		_availableMimeTypeKeyValuePairs = new ArrayList<>();

		Set<String> allMediaGalleryMimeTypes =
			DLUtil.getAllMediaGalleryMimeTypes();

		for (String mimeType : allMediaGalleryMimeTypes) {
			if (Arrays.binarySearch(mediaGalleryMimeTypes, mimeType) < 0) {
				_availableMimeTypeKeyValuePairs.add(
					new KeyValuePair(
						mimeType,
						LanguageUtil.get(themeDisplay.getLocale(), mimeType)));
			}
		}
	}

	private static final Map<String, String> _displayViews = HashMapBuilder.put(
		"descriptive", "list"
	).put(
		"icon", "cards"
	).put(
		"list", "table"
	).build();

	private List<KeyValuePair> _availableDisplayViewKeyValuePairs;
	private List<KeyValuePair> _availableEntryColumnKeyValuePairs;
	private List<KeyValuePair> _availableMimeTypeKeyValuePairs;
	private List<KeyValuePair> _currentDisplayViewKeyValuePairs;
	private List<KeyValuePair> _currentEntryColumnKeyValuePairs;
	private List<KeyValuePair> _currentMimeTypeKeyValuePairs;
	private final DLRequestHelper _dlRequestHelper;

}