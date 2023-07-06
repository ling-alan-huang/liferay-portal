<%--
/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<c:choose>
	<c:when test="<%= siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuId() > 0 %>">

		<%
		SiteNavigationMenu siteNavigationMenu = SiteNavigationMenuLocalServiceUtil.fetchSiteNavigationMenu(siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuId());
		%>

		<c:choose>
			<c:when test="<%= siteNavigationMenu != null %>">
				<liferay-site-navigation:navigation-menu
					ddmTemplateGroupId="<%= siteNavigationMenuDisplayContext.getDisplayStyleGroupId() %>"
					ddmTemplateKey="<%= siteNavigationMenuDisplayContext.getDDMTemplateKey() %>"
					displayDepth="<%= siteNavigationMenuDisplayContext.getDisplayDepth() %>"
					expandedLevels="<%= siteNavigationMenuDisplayContext.getExpandedLevels() %>"
					preview="<%= siteNavigationMenuDisplayContext.isPreview() %>"
					rootItemId="<%= siteNavigationMenuDisplayContext.getRootMenuItemId() %>"
					rootItemLevel="<%= siteNavigationMenuDisplayContext.getRootMenuItemLevel() %>"
					rootItemType="<%= siteNavigationMenuDisplayContext.getRootMenuItemType() %>"
					siteNavigationMenuId="<%= siteNavigationMenu.getSiteNavigationMenuId() %>"
				/>
			</c:when>
			<c:otherwise>
				<div class="alert alert-warning">
					<liferay-ui:message key="the-selected-menu-does-not-exist" />
				</div>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="<%= (siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuType() == SiteNavigationConstants.TYPE_PRIVATE_PAGES_HIERARCHY) || (siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuType() == SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY) %>">
		<liferay-site-navigation:navigation-menu
			ddmTemplateGroupId="<%= siteNavigationMenuDisplayContext.getDisplayStyleGroupId() %>"
			ddmTemplateKey="<%= siteNavigationMenuDisplayContext.getDDMTemplateKey() %>"
			displayDepth="<%= siteNavigationMenuDisplayContext.getDisplayDepth() %>"
			expandedLevels="<%= siteNavigationMenuDisplayContext.getExpandedLevels() %>"
			preview="<%= siteNavigationMenuDisplayContext.isPreview() %>"
			rootItemId="<%= siteNavigationMenuDisplayContext.getRootMenuItemId() %>"
			rootItemLevel="<%= siteNavigationMenuDisplayContext.getRootMenuItemLevel() %>"
			rootItemType="<%= siteNavigationMenuDisplayContext.getRootMenuItemType() %>"
			siteNavigationMenuId="<%= 0 %>"
		/>
	</c:when>
	<c:when test="<%= (siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuType() == SiteNavigationConstants.TYPE_PRIMARY) || (siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuType() == SiteNavigationConstants.TYPE_SECONDARY) || (siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuType() == SiteNavigationConstants.TYPE_SOCIAL) %>">

		<%
		SiteNavigationMenu siteNavigationMenu = SiteNavigationMenuLocalServiceUtil.fetchSiteNavigationMenu(themeDisplay.getScopeGroupId(), siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuType());
		%>

		<c:choose>
			<c:when test="<%= siteNavigationMenu != null %>">
				<liferay-site-navigation:navigation-menu
					ddmTemplateGroupId="<%= siteNavigationMenuDisplayContext.getDisplayStyleGroupId() %>"
					ddmTemplateKey="<%= siteNavigationMenuDisplayContext.getDDMTemplateKey() %>"
					displayDepth="<%= siteNavigationMenuDisplayContext.getDisplayDepth() %>"
					expandedLevels="<%= siteNavigationMenuDisplayContext.getExpandedLevels() %>"
					preview="<%= siteNavigationMenuDisplayContext.isPreview() %>"
					rootItemId="<%= siteNavigationMenuDisplayContext.getRootMenuItemId() %>"
					rootItemLevel="<%= siteNavigationMenuDisplayContext.getRootMenuItemLevel() %>"
					rootItemType="<%= siteNavigationMenuDisplayContext.getRootMenuItemType() %>"
					siteNavigationMenuId="<%= siteNavigationMenu.getSiteNavigationMenuId() %>"
				/>
			</c:when>
			<c:otherwise>
				<div class="alert alert-warning">
					<liferay-ui:message arguments="<%= siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuTypeLabel() %>" key="there-is-no-x-available-for-the-current-site" />
				</div>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<div class="alert alert-info text-center">
			<aui:a href="javascript:void(0);" onClick="<%= portletDisplay.getURLConfigurationJS() %>"><liferay-ui:message key="configure" /></aui:a>
		</div>
	</c:otherwise>
</c:choose>