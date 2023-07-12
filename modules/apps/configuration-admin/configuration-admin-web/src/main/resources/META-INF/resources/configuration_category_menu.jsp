<%--
/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String id = PortalUtil.generateRandomKey(request, "configuration_category_menu.jsp");

ConfigurationEntry configurationEntry = (ConfigurationEntry)request.getAttribute(ConfigurationAdminWebKeys.CONFIGURATION_ENTRY);

ConfigurationCategoryMenuDisplay configurationCategoryMenuDisplay = (ConfigurationCategoryMenuDisplay)request.getAttribute(ConfigurationAdminWebKeys.CONFIGURATION_CATEGORY_MENU_DISPLAY);
%>

<nav class="menubar menubar-transparent menubar-vertical-expand-md">
	<a aria-controls="<%= id %>" aria-expanded="false" class="menubar-toggler" data-toggle="liferay-collapse" href="#<%= id %>" role="button">
		<liferay-ui:message key="<%= configurationEntry.getName() %>" />

		<aui:icon image="caret-bottom" markupView="lexicon" />
	</a>

	<div class="collapse menubar-collapse" id="<%= id %>">
		<ul class="nav nav-nested">

			<%
			for (ConfigurationScopeDisplay configurationScopeDisplay : configurationCategoryMenuDisplay.getConfigurationScopeDisplays()) {
				if (configurationScopeDisplay.isEmpty()) {
					continue;
				}

				List<ConfigurationEntry> configurationEntries = configurationScopeDisplay.getConfigurationEntries();
			%>

				<li class="nav-item">
					<a class="nav-link text-uppercase">
						<liferay-ui:message key='<%= "scope." + configurationScopeDisplay.getScope() %>' />
					</a>

					<div>
						<ul class="nav nav-stacked">

							<%
							for (ConfigurationEntry curConfigurationEntry : configurationEntries) {
							%>

								<li class="nav-item">
									<aui:a cssClass='<%= configurationEntry.equals(curConfigurationEntry) ? "active nav-link" : "nav-link" %>' href="<%= curConfigurationEntry.getEditURL(renderRequest, renderResponse) %>"><%= curConfigurationEntry.getName() %></aui:a>
								</li>

							<%
							}
							%>

						</ul>
					</div>
				</li>

			<%
			}
			%>

		</ul>
	</div>
</nav>