<%--
/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
UserItemSelectorViewDisplayContext userItemSelectorViewDisplayContext = (UserItemSelectorViewDisplayContext)request.getAttribute(UserItemSelectorViewConstants.USER_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT);

String displayStyle = userItemSelectorViewDisplayContext.getDisplayStyle();
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new UserItemSelectorViewManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, userItemSelectorViewDisplayContext) %>"
/>

<clay:container-fluid
	id='<%= liferayPortletResponse.getNamespace() + "userSelectorWrapper" %>'
>
	<liferay-ui:search-container
		id="<%= userItemSelectorViewDisplayContext.getSearchContainerId() %>"
		searchContainer="<%= userItemSelectorViewDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.User"
			cssClass="user-row"
			keyProperty="userId"
			modelVar="user"
		>

			<%
			row.setData(
				HashMapBuilder.<String, Object>put(
					"id", user.getUserId()
				).put(
					"name", user.getFullName()
				).build());
			%>

			<c:choose>
				<c:when test='<%= displayStyle.equals("descriptive") %>'>
					<liferay-ui:search-container-column-text>
						<liferay-ui:user-portrait
							userId="<%= user.getUserId() %>"
						/>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<h5 class="table-title"><%= user.getFullName() %></h5>

						<h6 class="text-default">
							<span><%= user.getScreenName() %></span>
						</h6>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= displayStyle.equals("icon") %>'>

					<%
					row.setCssClass("card-page-item card-page-item-asset selectable");
					%>

					<liferay-ui:search-container-column-text>
						<clay:user-card
							userCard="<%= new SelectUserUserCard(user, renderRequest, searchContainer.getRowChecker()) %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:otherwise>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-title"
						name="name"
						value="<%= HtmlUtil.escape(user.getFullName()) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="screen-name"
						property="screenName"
					/>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= userItemSelectorViewDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
			searchContainer="<%= userItemSelectorViewDisplayContext.getSearchContainer() %>"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>