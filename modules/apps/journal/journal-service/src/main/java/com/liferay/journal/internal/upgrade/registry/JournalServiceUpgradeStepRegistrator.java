/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.registry;

import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.comment.upgrade.DiscussionSubscriptionClassNameUpgradeProcess;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService;
import com.liferay.dynamic.data.mapping.util.DefaultDDMStructureHelper;
import com.liferay.dynamic.data.mapping.util.FieldsToDDMFormValuesConverter;
import com.liferay.journal.content.compatibility.converter.JournalContentCompatibilityConverter;
import com.liferay.journal.internal.upgrade.helper.JournalArticleImageUpgradeHelper;
import com.liferay.journal.internal.upgrade.v0_0_3.JournalArticleTypeUpgradeProcess;
import com.liferay.journal.internal.upgrade.v0_0_4.SchemaUpgradeProcess;
import com.liferay.journal.internal.upgrade.v0_0_5.JournalUpgradeProcess;
import com.liferay.journal.internal.upgrade.v0_0_5.UpgradeCompanyId;
import com.liferay.journal.internal.upgrade.v0_0_5.UpgradeJournalArticles;
import com.liferay.journal.internal.upgrade.v0_0_5.UpgradeJournalDisplayPreferences;
import com.liferay.journal.internal.upgrade.v0_0_5.UpgradeLastPublishDate;
import com.liferay.journal.internal.upgrade.v0_0_5.UpgradePortletSettings;
import com.liferay.journal.internal.upgrade.v0_0_6.ImageTypeContentAttributesUpgradeProcess;
import com.liferay.journal.internal.upgrade.v0_0_8.ArticleAssetsUpgradeProcess;
import com.liferay.journal.internal.upgrade.v0_0_8.ArticleExpirationDateUpgradeProcess;
import com.liferay.journal.internal.upgrade.v0_0_8.ArticleSystemEventsUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_0_0.JournalArticleImageUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_0_1.JournalContentSearchUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_0.DocumentLibraryTypeContentUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_0.ImageTypeContentUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_0.JournalArticleLocalizedValuesUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_1.FileUploadsConfigurationUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_2.CheckIntervalConfigurationUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_3.ResourcePermissionsUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_5.ContentImagesUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_6.AssetDisplayPageEntryUpgradeProcess;
import com.liferay.journal.internal.upgrade.v2_0_0.util.JournalArticleTable;
import com.liferay.journal.internal.upgrade.v2_0_0.util.JournalFeedTable;
import com.liferay.journal.internal.upgrade.v2_0_0.util.JournalFolderTable;
import com.liferay.journal.internal.upgrade.v3_3_0.StorageLinksUpgradeProcess;
import com.liferay.journal.internal.upgrade.v3_5_0.JournalArticleContentUpgradeProcess;
import com.liferay.journal.internal.upgrade.v3_5_1.JournalArticleDataFileEntryIdUpgradeProcess;
import com.liferay.journal.internal.upgrade.v4_0_0.JournalArticleDDMFieldsUpgradeProcess;
import com.liferay.journal.internal.upgrade.v4_1_0.JournalArticleExternalReferenceCodeUpgradeProcess;
import com.liferay.journal.internal.upgrade.v4_3_1.BasicWebContentAssetEntryClassTypeIdUpgradeProcess;
import com.liferay.journal.internal.upgrade.v4_4_0.GlobalJournalArticleUrlTitleUpgradeProcess;
import com.liferay.journal.internal.upgrade.v4_4_4.JournalFeedTypeUpgradeProcess;
import com.liferay.journal.internal.upgrade.v5_1_0.JournalArticleDDMStructureIdUpgradeProcess;
import com.liferay.journal.internal.upgrade.v5_1_1.JournalArticleAssetEntryClassTypeIdUpgradeProcess;
import com.liferay.journal.internal.upgrade.v5_2_0.JournalFeedDDMStructureIdUpgradeProcess;
import com.liferay.journal.internal.upgrade.v6_1_0.JournalArticleSmallImageSourceUpgradeProcess;
import com.liferay.journal.internal.upgrade.v6_1_1.JournalArticleLayoutClassedModelUsageUpgradeProcess;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.util.JournalConverter;
import com.liferay.portal.change.tracking.store.CTStoreFactory;
import com.liferay.portal.configuration.upgrade.PrefsPropsToConfigurationUpgradeHelper;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.capabilities.PortalCapabilityLocator;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(service = UpgradeStepRegistrator.class)
public class JournalServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {



	private static final Log _log = LogFactoryUtil.getLog(
		JournalServiceUpgradeStepRegistrator.class);

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Portal _portal;

	// See LPS-82746

	@Reference
	private PortalCapabilityLocator _portalCapabilityLocator;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	@Reference
	private PrefsPropsToConfigurationUpgradeHelper
		_prefsPropsToConfigurationUpgradeHelper;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.layout.service)(&(release.schema.version>=1.0.0)))"
	)
	private Release _release;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.layout.service)(&(release.schema.version>=1.0.0)))"
	)
	private Release _arelease;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourceActions _resourceActions;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SettingsLocatorHelper _settingsLocatorHelper;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

	@Reference(target = "(default=true)")
	private Store _store;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private SystemEventLocalService _systemEventLocalService;

	@Reference
	private UserLocalService _userLocalService;

}