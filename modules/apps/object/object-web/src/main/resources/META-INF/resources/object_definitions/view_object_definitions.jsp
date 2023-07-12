<%--
/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewObjectDefinitionsDisplayContext viewObjectDefinitionsDisplayContext = (ViewObjectDefinitionsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<frontend-data-set:headless-display
	apiURL="<%= viewObjectDefinitionsDisplayContext.getAPIURL() %>"
	creationMenu="<%= viewObjectDefinitionsDisplayContext.getCreationMenu() %>"
	fdsActionDropdownItems="<%= viewObjectDefinitionsDisplayContext.getFDSActionDropdownItems() %>"
	fdsSortItemList="<%= viewObjectDefinitionsDisplayContext.getFDSSortItemList() %>"
	formName="fm"
	id="<%= ObjectDefinitionsFDSNames.OBJECT_DEFINITIONS %>"
	propsTransformer="js/components/FDSPropsTransformer/ObjectDefinitionFDSPropsTransformer"
	style="fluid"
/>

<div id="<portlet:namespace />addObjectDefinition">
	<react:component
		module="js/components/ModalAddObjectDefinition"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"apiURL", viewObjectDefinitionsDisplayContext.getAPIURL()
			).put(
				"storages", viewObjectDefinitionsDisplayContext.getStoragesJSONArray()
			).build()
		%>'
	/>
</div>

<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" var="baseResourceURL" />

<div id="<portlet:namespace />deleteObjectDefinition">
	<react:component
		module="js/components/ModalDeleteObjectDefinition"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"baseResourceURL", String.valueOf(baseResourceURL)
			).build()
		%>'
	/>
</div>