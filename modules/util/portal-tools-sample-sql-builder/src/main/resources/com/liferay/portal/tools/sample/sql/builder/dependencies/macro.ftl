<#macro insertDDMConten
	_currentIndex = "-1"
	_ddmStructureId
	_entry
	_ddmStructureVersionId = 0
	_ddmStorageLinkId
>
</#macro>

<button
	data-term-id="${entry.getValue()}"
	class="btn-link btn btn-unstyled facet-term ${(entry.isSelected())?then('facet-term-selected', 'facet-term-unselected')} term-name"
	disabled=""
	onClick="Liferay.Search.FacetUtil.changeSelection(event);"
>
	${htmlUtil.escape(entry.getDisplayName())}
</button>