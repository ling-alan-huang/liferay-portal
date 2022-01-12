# Checks for .properties

Check | Category | Description
----- | -------- | -----------
PropertiesArchivedModulesCheck | Bug Prevention | Finds `test.batch.class.names.includes` property value pointing to archived modules in `test.properties`. |
PropertiesBuildIncludeDirsCheck | Bug Prevention | Verifies property value of `build.include.dirs` in `build.properties`. |
PropertiesCommentsCheck | Styling | Validates comments in `.properties` files. |
PropertiesDefinitionKeysCheck | Styling | Sorts definition keys in `liferay-plugin-package.properties` file. |
PropertiesDependenciesFileCheck | Styling | Sorts the properties in `dependencies.properties` file. |
PropertiesEmptyLinesCheck | Styling | Finds missing and unnecessary empty lines. |
PropertiesImportedFilesContentCheck | Bug Prevention | Performs several checks on `imported-files.properties` file. |
[PropertiesLanguageKeysCheck](checks/properties_language_keys_check.markdown#propertieslanguagekeyscheck) | Bug Prevention | Checks that there is no HTML markup in language keys. |
PropertiesLanguageKeysOrderCheck | Styling | Sort language keys in `Language.properties` file. |
PropertiesLiferayPluginPackageFileCheck | Bug Prevention | Performs several checks on `liferay-plugin-package.properties` file. |
PropertiesLiferayPluginPackageLiferayVersionsCheck | Bug Prevention | Validates the version in `liferay-plugin-package.properties` file. |
PropertiesLongLinesCheck | Styling | Finds lines that are longer than the specified maximum line length. |
PropertiesMultiLineValuesOrderCheck | Styling | Verifies that property with multiple values is not on a single line. |
PropertiesPortalEnvironmentVariablesCheck | Documentation | Verifies that the environment property in the documentation matches the property name. |
PropertiesPortalFileCheck | Bug Prevention | Performs several checks on `portal.properties` or `portal-*.properties` file. |
PropertiesPortletFileCheck | Bug Prevention | Performs several checks on `portlet.properties` file. |
PropertiesReleaseBuildCheck | Bug Prevention | Verifies that the information in `release.properties` matches the information in `ReleaseInfo.java`. |
PropertiesServiceKeysCheck | Bug Prevention | Finds usage of legacy properties in `service.properties`. |
PropertiesSourceFormatterContentCheck | Bug Prevention | Performs several checks on `source-formatter.properties` file. |
PropertiesSourceFormatterFileCheck | Bug Prevention | Performs several checks on `source-formatter.properties` file. |
PropertiesStylingCheck | Styling | Applies rules to enforce consisteny in code style. |
PropertiesVerifyPropertiesCheck | Bug Prevention | Finds usage of legacy properties in `portal.properties` or `system.properties`. |
PropertiesWhitespaceCheck | Styling | Finds missing and unnecessary whitespace in `.properties` files. |