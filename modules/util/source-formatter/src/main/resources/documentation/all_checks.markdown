# All Checks

Check | Category | File Extensions | Description
----- | -------- | --------------- | -----------
[AnnotationUseStyleCheck](https://checkstyle.sourceforge.io/config_annotation.html#AnnotationUseStyle) | Styling | .java | Checks the style of elements in annotations. |
[AnonymousClassCheck](checks/anonymous_class_check.markdown#anonymousclasscheck) | Bug Prevention | .java | Checks for serialization issue when using anonymous class. |
AppendCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks instances where literal Strings are appended. |
ArquillianCheck | Bug Prevention | .java | Checks for correct use of `com.liferay.arquillian.extension.junit.bridge.junit.Arquillian`. |
[ArrayCheck](checks/array_check.markdown#arraycheck) | Performance | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks if performance can be improved by using different mehods that can be used by collections. |
[ArrayTypeStyleCheck](https://checkstyle.sourceforge.io/config_misc.html#ArrayTypeStyle) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks the style of array type definitions. |
[AssertEqualsCheck](checks/assert_equals_check.markdown#assertequalscheck) | Styling | .java | Checks that additional information is provided when calling `Assert.assertEquals`. |
AssignAsUsedCheck | Performance | .java | Finds cases where an assign statement can be inlined or moved closer to where it is used. |
[AttributeOrderCheck](checks/attribute_order_check.markdown#attributeordercheck) | Styling | .java | Checks that attributes in anonymous classes are ordered alphabetically. |
[AvoidNestedBlocksCheck](https://checkstyle.sourceforge.io/config_blocks.html#AvoidNestedBlocks) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds nested blocks (blocks that are used freely in the code). |
[AvoidStarImportCheck](https://checkstyle.sourceforge.io/config_imports.html#AvoidStarImport) | Bug Prevention | .java | Checks that there are no import statements that use the * notation. |
[BNDBundleActivatorCheck](checks/bnd_bundle_activator_check.markdown#bndbundleactivatorcheck) | Bug Prevention | .bnd | Validates property value for `Bundle-Activator`. |
[BNDBundleCheck](checks/bnd_bundle_check.markdown#bndbundlecheck) | Bug Prevention | .bnd | Validates `Liferay-Releng-*` properties. |
[BNDBundleInformationCheck](checks/bnd_bundle_information_check.markdown#bndbundleinformationcheck) | Bug Prevention | .bnd | Validates property values for `Bundle-Version`, `Bundle-Name` and `Bundle-SymbolicName`. |
BNDCapabilityCheck | Styling | .bnd | Sorts and applies logic to fix line breaks to property values for `Provide-Capability` and `Require-Capability`. |
[BNDDefinitionKeysCheck](checks/bnd_definition_keys_check.markdown#bnddefinitionkeyscheck) | Bug Prevention | .bnd | Validates definition keys in `.bnd` files. |
BNDDeprecatedAppBNDsCheck | Miscellaneous | .bnd | Checks for redundant `app.bnd` in deprecated or archived modules. |
[BNDDirectoryNameCheck](checks/bnd_directory_name_check.markdown#bnddirectorynamecheck) | Bug Prevention | .bnd | Checks if the directory names of the submodules match the parent module name. |
[BNDExportsCheck](checks/bnd_exports_check.markdown#bndexportscheck) | Bug Prevention | .bnd | Checks that modules not ending with `-api`, `-client`, `-spi`, `-tablig`, `-test-util` do not export packages. |
[BNDImportsCheck](checks/bnd_imports_check.markdown#bndimportscheck) | Styling | .bnd | Sorts class names and checks for use of wildcards in property values for `-conditionalpackage`, `-exportcontents` and `Export-Package`. |
[BNDIncludeResourceCheck](checks/bnd_include_resource_check.markdown#bndincluderesourcecheck) | Bug Prevention | .bnd | Checks for unnesecarry including of `test-classes/integration`. |
[BNDLiferayEnterpriseAppCheck](checks/bnd_liferay_enterprise_app_check.markdown#bndliferayenterpriseappcheck) | Bug Prevention | .bnd | Checks for correct use of property `Liferay-Enterprise-App`. |
[BNDLiferayRelengBundleCheck](checks/bnd_liferay_releng_bundle_check.markdown#bndliferayrelengbundlecheck) | Bug Prevention | .bnd | Checks if `.lfrbuild-release-src` file exists for DXP module with `Liferay-Releng-Bundle: true` |
[BNDLiferayRelengCategoryCheck](checks/bnd_liferay_releng_category_check.markdown#bndliferayrelengcategorycheck) | Bug Prevention | .bnd | Validates `Liferay-Releng-Category` properties |
BNDLineBreaksCheck | Styling | .bnd | Finds missing and unnecessary line breaks in `.bnd` files. |
BNDMultipleAppBNDsCheck | Bug Prevention | .bnd | Checks for duplicate `app.bnd` (when both `/apps/` and `/apps/dxp/` contain the same module). |
[BNDRangeCheck](checks/bnd_range_check.markdown#bndrangecheck) | Bug Prevention | .bnd | Checks for use or range expressions. |
BNDRunInstructionsOrderCheck | Styling | .bndrun | Sorts definition keys alphabetically. |
[BNDSchemaVersionCheck](checks/bnd_schema_version_check.markdown#bndschemaversioncheck) | Bug Prevention | .bnd | Checks for incorrect use of property `Liferay-Require-SchemaVersion`. |
BNDStylingCheck | Styling | .bnd | Applies rules to enforce consisteny in code style. |
[BNDSuiteCheck](checks/bnd_suite_check.markdown#bndsuitecheck) | Miscellaneous | .bnd | Checks that deprecated apps are moved to the `archived` folder. |
[BNDWebContextPathCheck](checks/bnd_web_context_path_check.markdown#bndwebcontextpathcheck) | Bug Prevention | .bnd | Checks if the property value for `Web-ContextPath` matches the module directory. |
BNDWhitespaceCheck | Styling | .bnd | Finds missing and unnecessary whitespace in `.bnd` files. |
CDNCheck | Bug Prevention | | Checks the URL in `artifact.properties` files. |
CQLKeywordCheck | Bug Prevention | .cql | Checks that Cassandra keywords are upper case. |
CSSCommentsCheck | Styling | .css or .scss | Validates comments in `.css` files. |
CSSImportsCheck | Styling | .css or .scss | Sorts and groups imports in `.css` files. |
CSSPropertiesOrderCheck | Styling | .css or .scss | Sorts properties in `.css` files. |
[CamelCaseNameCheck](checks/camel_case_name_check.markdown#camelcasenamecheck) | Naming Conventions | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks variable names for correct use of `CamelCase`. |
[ChainingCheck](checks/chaining_check.markdown#chainingcheck) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that chaining is only applied on certain types and methods. |
[CodeownersFileLocationCheck](checks/codeowners_file_location_check.markdown#codeownersfilelocationcheck) | Bug Prevention | CODEOWNERS | Checks that `CODEOWNERS` files are located in `.github` directory. |
CodeownersWhitespaceCheck | Styling | CODEOWNERS | Finds missing and unnecessary whitespace in `CODEOWNERS` files. |
[CompanyIterationCheck](checks/company_iteration_check.markdown#companyiterationcheck) | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that `CompanyLocalService.forEachCompany` or `CompanyLocalService.forEachCompanyId` is used when iterating over companies |
CompatClassImportsCheck | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that classes are imported from `compat` modules, when possible. |
ConcatCheck | Performance | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks for correct use of `StringBundler.concat`. |
ConfigDefinitionKeysCheck | Styling | .cfg or .config | Sorts definition keys in `.config` files. |
ConstantNameCheck | Naming Conventions | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that variable names of constants follow correct naming rules. |
ConstructorGlobalVariableDeclarationCheck | Performance | .java | Checks that initial values of global variables are not set in the constructor. |
[ConstructorMissingEmptyLineCheck](checks/constructor_missing_empty_line_check.markdown#constructormissingemptylinecheck) | Styling | .java | Checks for line breaks when assiging variables in constructor. |
ConsumerTypeAnnotationCheck | Bug Prevention | .java | Performs several checks on classes with @ConsumerType annotation. |
ContractionsCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds contractions in Strings (such as `can't` or `you're`). |
[CopyrightCheck](checks/copyright_check.markdown#copyrightcheck) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Validates `copyright` header. |
CreationMenuBuilderCheck | Miscellaneous | .java | Checks that `CreationMenuBuilder` is used when possible. |
DTOEnumCreationCheck | Bug Prevention | .java | Checks the creation of DTO enum. |
[DefaultComesLastCheck](https://checkstyle.sourceforge.io/config_coding.html#DefaultComesLast) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that the `default` is after all the cases in a `switch` statement. |
DeprecatedAPICheck | Bug Prevention | .java | Finds calls to deprecated classes, constructors, fields or methods. |
DockerfileEmptyLinesCheck | Styling | Dockerfile | Finds missing and unnecessary empty lines. |
DockerfileInstructionCheck | Styling | Dockerfile | Performs styling rules on instructions in `Dockerfile` files. |
EmptyCollectionCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that there are no calls to `Collections.EMPTY_LIST`, `Collections.EMPTY_MAP` or `Collections.EMPTY_SET`. |
EmptyConstructorCheck | Bug Prevention | .java | Finds unnecessary empty constructors. |
EnumConstantDividerCheck | Styling | .java | Find unnecessary empty lines between enum constants. |
EnumConstantOrderCheck | Styling | .java | Checks the order of enum constants. |
EqualClauseIfStatementsCheck | Styling | .java | Finds consecutive if-statements with identical clauses. |
[ExceptionCheck](checks/exception_check.markdown#exceptioncheck) | Performance | .java | Finds private methods that throw unnecessary exception. |
ExceptionMessageCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Validates messages that are passed to exceptions. |
ExceptionVariableNameCheck | Naming Conventions | .java | Validates variable names that have type `*Exception`. |
FTLEmptyLinesCheck | Styling | .ftl | Finds missing and unnecessary empty lines. |
FTLIfStatementCheck | Styling | .ftl | Finds incorrect use of parentheses in statement. |
FTLImportsCheck | Styling | .ftl | Sorts and groups imports in `.ftl` files. |
FTLLiferayVariableOrderCheck | Styling | .ftl | Sorts assign statement of `liferay_*` variables. |
FTLStringRelationalOperatorCheck | Styling | .ftl | Finds cases of `==` or `!=` where `stringUtil.equals`, `validator.isNotNull` or `validator.isNull` can be used instead. |
FTLStylingCheck | Styling | .ftl | Applies rules to enforce consisteny in code style. |
FTLTagAttributesCheck | Styling | .ftl | Sorts and formats attributes values in tags. |
FTLTagCheck | Styling | .ftl | Finds cases where consecutive `#assign` can be combined. |
FTLWhitespaceCheck | Styling | .ftl | Finds missing and unnecessary whitespace in `.ftl` files. |
FactoryCheck | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds cases where `*Factory` should be used when creating new instances of an object. |
FilterStringWhitespaceCheck | Bug Prevention | .java | Finds missing and unnecessary whitespace in the value of the filter string in `ServiceTrackerFactory.open` or `WaiterUtil.waitForFilter`. |
[FrameworkBundleCheck](checks/framework_bundle_check.markdown#frameworkbundlecheck) | Performance | .java | Checks that `org.osgi.framework.Bundle.getHeaders()` is not used. |
FullyQualifiedNameCheck | Miscellaneous | .java | Finds cases where a Fully Qualified Name is used instead of importing a class. |
[GenericTypeCheck](checks/generic_type_check.markdown#generictypecheck) | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that generics are always specified to provide compile-time checking and removing the risk of `ClassCastException` during runtime. |
[GetterUtilCheck](checks/getter_util_check.markdown#getterutilcheck) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds cases where the default value is passed to `GetterUtil.get*` or `ParamUtil.get*`. |
GradleBlockOrderCheck | Styling | .gradle | Sorts logic in gradle build files. |
GradleBodyCheck | Styling | .gradle | Applies rules to enforce consisteny in the body of gradle build files. |
[GradleDependenciesCheck](checks/gradle_dependencies_check.markdown#gradledependenciescheck) | Performance | .gradle | Checks that modules are not depending on other modules. |
[GradleDependencyArtifactsCheck](checks/gradle_dependency_artifacts_check.markdown#gradledependencyartifactscheck) | Bug Prevention | .gradle | Checks that value `default` is not used for attribute `version`. |
GradleDependencyConfigurationCheck | Bug Prevention | .gradle | Validates the scope of dependencies in build gradle files. |
GradleDependencyVersionCheck | Bug Prevention | .gradle | Checks the version for dependencies in gradle build files. |
GradleExportedPackageDependenciesCheck | Bug Prevention | .gradle | Validates dependencies in gradle build files. |
GradleImportsCheck | Styling | .gradle | Sorts and groups imports in `.gradle` files. |
GradleIndentationCheck | Styling | .gradle | Finds incorrect indentation in gradle build files. |
GradleJavaVersionCheck | Bug Prevention | .gradle | Checks values of properties `sourceCompatibility` and `targetCompatibility` in gradle build files. |
GradlePropertiesCheck | Bug Prevention | .gradle | Validates property values in gradle build files. |
GradleProvidedDependenciesCheck | Bug Prevention | .gradle | Validates the scope of dependencies in build gradle files. |
[GradleRequiredDependenciesCheck](checks/gradle_required_dependencies_check.markdown#gradlerequireddependenciescheck) | Bug Prevention | .gradle | Validates the dependencies in `/required-dependencies/required-dependencies/build.gradle`. |
GradleStylingCheck | Styling | .gradle | Applies rules to enforce consisteny in code style. |
[GradleTaskCreationCheck](checks/gradle_task_creation_check.markdown#gradletaskcreationcheck) | Styling | .gradle | Checks that a task is declared on a separate line before the closure. |
GradleTestDependencyVersionCheck | Bug Prevention | .gradle | Checks the version for dependencies in gradle build files. |
HTMLEmptyLinesCheck | Styling | .html | Finds missing and unnecessary empty lines. |
HTMLWhitespaceCheck | Styling | .html | Finds missing and unnecessary whitespace in `.html` files. |
[IfStatementCheck](checks/if_statement_check.markdown#ifstatementcheck) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds empty if-statements and consecutive if-statements with identical bodies |
[IncorrectFileLocationCheck](checks/incorrect_file_location_check.markdown#incorrectfilelocationcheck) | Bug Prevention | | Checks that `/src/*/java/` only contains `.java` files. |
InstanceofOrderCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Check the order of `instanceof` calls. |
ItemBuilderCheck | Miscellaneous | .java | Checks that `DropdownItemBuilder`, `LabelItemBuilder` or `NavigationItemBuilder` is used when possible. |
ItemListBuilderCheck | Miscellaneous | .java | Checks that `DropdownItemListBuilder`, `LabelItemListBuilder` or `NavigationItemListBuilder` is used when possible. |
[JSLodashDependencyCheck](checks/js_lodash_dependency_check.markdown#jslodashdependencycheck) | Bug Prevention | .js or .jsx | Finds incorrect use of `AUI._`. |
[JSONDeprecatedPackagesCheck](checks/json_deprecated_packages_check.markdown#jsondeprecatedpackagescheck) | Bug Prevention | .json or .npmbridgerc | Finds incorrect use of deprecated packages in `package.json` files. |
JSONNamingCheck | Naming Conventions | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks if variable names follow naming conventions. |
JSONPackageJSONBNDVersionCheck | Bug Prevention | .json or .npmbridgerc | Checks the version for dependencies in `package.json` files. |
JSONPackageJSONCheck | Bug Prevention | .json or .npmbridgerc | Checks content of `package.json` files. |
JSONPackageJSONDependencyVersionCheck | Bug Prevention | .json or .npmbridgerc | Checks the version for dependencies in `package.json` files. |
JSONStylingCheck | Styling | .json or .npmbridgerc | Applies rules to enforce consisteny in code style. |
[JSONUtilCheck](checks/json_util_check.markdown#jsonutilcheck) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks for utilization of class `JSONUtil`. |
[JSONValidationCheck](checks/json_validation_check.markdown#jsonvalidationcheck) | Bug Prevention | .json or .npmbridgerc | Validates content of `.json` files. |
[JSPArrowFunctionCheck](checks/jsp_arrow_function_check.markdown#jsparrowfunctioncheck) | Bug Prevention | .jsp, .jspf, .tag, .tpl or .vm | Checks that there are no array functions. |
JSPCoreTaglibCheck | Styling | .jsp, .jspf, .tag, .tpl or .vm | Finds cases where a `c:choose` or `c:if` tag can be used instead of an if-statement. |
[JSPDefineObjectsCheck](checks/jsp_define_objects_check.markdown#jspdefineobjectscheck) | Performance | .jsp, .jspf, .tag, .tpl or .vm | Checks for unnesecarry duplication of code that already exists in `defineObjects`. |
JSPEmptyLinesCheck | Styling | .jsp, .jspf, .tag, .tpl or .vm | Finds missing and unnecessary empty lines. |
JSPExceptionOrderCheck | Styling | .jsp, .jspf, .tag, .tpl or .vm | Checks the order of exceptions in `.jsp` files. |
[JSPFileNameCheck](checks/jsp_file_name_check.markdown#jspfilenamecheck) | Naming Conventions | .jsp, .jspf, .tag, .tpl or .vm | Checks if the file name of `.jsp` or `.jspf` follows the naming conventions. |
[JSPFunctionNameCheck](checks/jsp_function_name_check.markdown#jspfunctionnamecheck) | Naming Conventions | .jsp, .jspf, .tag, .tpl or .vm | Check if the names of functions in `.jsp` files follow naming conventions. |
[JSPIllegalSyntaxCheck](checks/jsp_illegal_syntax_check.markdown#jspillegalsyntaxcheck) | Bug Prevention | .jsp, .jspf, .tag, .tpl or .vm | Finds incorrect use of `System.out.print`, `console.log` or `debugger.*` in `.jsp` files. |
[JSPImportsCheck](checks/jsp_imports_check.markdown#jspimportscheck) | Styling | .jsp, .jspf, .tag, .tpl or .vm | Sorts and groups imports in `.jsp` files. |
[JSPIncludeCheck](checks/jsp_include_check.markdown#jspincludecheck) | Bug Prevention | .jsp, .jspf, .tag, .tpl or .vm | Validates values of `include` in `.jsp` files. |
JSPIndentationCheck | Styling | .jsp, .jspf, .tag, .tpl or .vm | Finds incorrect indentation in `.jsp` files. |
JSPInlineVariableCheck | Styling | .jsp, .jspf, .tag, .tpl or .vm | Finds cases where variables can be inlined. |
JSPJavaParserCheck | Styling | .jsp, .jspf, .tag, .tpl or .vm | Performs JavaParser on `.java` files. |
JSPLanguageKeysCheck | Bug Prevention | .jsp, .jspf, .tag, .tpl or .vm | Finds missing language keys in `Language.properties`. |
JSPLanguageUtilCheck | Bug Prevention | .jsp, .jspf, .tag, .tpl or .vm | Finds cases where Locale is passed to `LanguageUtil.get` instead of `HttpServletRequest`. |
JSPLineBreakCheck | Styling | .jsp, .jspf, .tag, .tpl or .vm | Finds missing and unnecessary line breaks in `.jsp` lines. |
JSPLogFileNameCheck | Bug Prevention | .jsp, .jspf, .tag, .tpl or .vm | Validates the value that is passed to `LogFactoryUtil.getLog` in `.jsp`. |
[JSPMethodCallsCheck](checks/jsp_method_calls_check.markdown#jspmethodcallscheck) | Bug Prevention | .jsp, .jspf, .tag, .tpl or .vm | Checks that type `LiferayPortletResponse` is used to call `getNamespace()`. |
[JSPMissingTaglibsCheck](checks/jsp_missing_taglibs_check.markdown#jspmissingtaglibscheck) | Bug Prevention | .jsp, .jspf, .tag, .tpl or .vm | Checks for missing taglibs. |
[JSPModuleIllegalImportsCheck](checks/jsp_module_illegal_imports_check.markdown#jspmoduleillegalimportscheck) | Miscellaneous | .jsp, .jspf, .tag, .tpl or .vm | Finds incorrect use of `com.liferay.registry.Registry` or `com.liferay.util.ContentUtil`. |
JSPParenthesesCheck | Miscellaneous | .jsp, .jspf, .tag, .tpl or .vm | Finds incorrect use of parentheses in statement. |
JSPRedirectBackURLCheck | Miscellaneous | .jsp, .jspf, .tag, .tpl or .vm | Validates values of variable `redirect`. |
[JSPSendRedirectCheck](checks/jsp_send_redirect_check.markdown#jspsendredirectcheck) | Bug Prevention | .jsp, .jspf, .tag, .tpl or .vm | Checks that there are no calls to `HttpServletResponse.sendRedirect` from `jsp` files. |
[JSPServiceUtilCheck](checks/jsp_service_util_check.markdown#jspserviceutilcheck) | Miscellaneous | .jsp, .jspf, .tag, .tpl or .vm | Finds incorrect use of `*ServiceUtil` in `.jsp` files in modules. |
JSPSessionKeysCheck | Bug Prevention | .jsp, .jspf, .tag, .tpl or .vm | Checks that messages send to `SessionsErrors` or `SessionMessages` follow naming conventions. |
JSPStylingCheck | Styling | .jsp, .jspf, .tag, .tpl or .vm | Applies rules to enforce consisteny in code style. |
JSPTagAttributesCheck | Bug Prevention | .jsp, .jspf, .tag, .tpl or .vm | Performs several checks on tag attributes. |
[JSPTaglibVariableCheck](checks/jsp_taglib_variable_check.markdown#jsptaglibvariablecheck) | Naming Conventions | .jsp, .jspf, .tag, .tpl or .vm | Checks if variable names follow naming conventions. |
[JSPUnusedJSPFCheck](checks/jsp_unused_jspf_check.markdown#jspunusedjspfcheck) | Performance | .jsp, .jspf, .tag, .tpl or .vm | Finds `.jspf` files that are not used. |
JSPUnusedTermsCheck | Performance | .jsp, .jspf, .tag, .tpl or .vm | Finds taglibs, variables and imports that are unused. |
JSPUpgradeRemovedTagsCheck | Upgrade | .jsp, .jspf, .tag, .tpl or .vm | Finds removed tags when upgrading. |
JSPVarNameCheck | Styling | .jsp, .jspf, .tag, .tpl or .vm | Checks that values of attribute `var` follow naming conventions. |
JSPVariableOrderCheck | Styling | .jsp, .jspf, .tag, .tpl or .vm | Checks if variable names are in alphabetical order. |
JSPWhitespaceCheck | Styling | .jsp, .jspf, .tag, .tpl or .vm | Finds missing and unnecessary whitespace in `.jsp` files. |
JSPXSSVulnerabilitiesCheck | Security | .jsp, .jspf, .tag, .tpl or .vm | Finds xss vulnerabilities. |
JSStylingCheck | Styling | .js or .jsx | Applies rules to enforce consisteny in code style. |
JSWhitespaceCheck | Styling | .js or .jsx | Finds missing and unnecessary whitespace in `.js` files. |
Java2HTMLCheck | Miscellaneous | .java | Finds incorrect use of `.java.html` in `.jsp` files. |
[JavaAPISignatureCheck](checks/java_api_signature_check.markdown#javaapisignaturecheck) | Bug Prevention | .java | Checks that types `HttpServletRequest`, `HttpServletResponse`, `ThemeDisplay`, and `ServiceContext` are not used in API method signatures. |
JavaAbstractMethodCheck | Bug Prevention | .java | Finds incorrect `abstract` methods in `interface`. |
JavaAggregateTestRuleParameterOrderCheck | Styling | .java | Checks the order of parameters in `new AggregateTestRule` calls. |
JavaAnnotationDefaultAttributeCheck | Styling | .java | Finds cases where the default value is passed to annotations in package `*.bnd.annotations` or `*.bind.annotations`. |
JavaAnnotationsCheck | Bug Prevention | .java | Performs several checks on annotations. |
[JavaAnonymousInnerClassCheck](checks/java_anonymous_inner_class_check.markdown#javaanonymousinnerclasscheck) | Bug Prevention | .java | Performs several checks on anonymous classes. |
JavaAssertEqualsCheck | Styling | .java | Validates `Assert.assertEquals` calls. |
[JavaBaseUpgradeCallableCheck](checks/java_base_upgrade_callable_check.markdown#javabaseupgradecallablecheck) | Bug Prevention | .java | Checks that BaseUpgradeCallable is used instead of Callable or Runnable in Upgrade and Verify classes. |
JavaBooleanStatementCheck | Bug Prevention | .java | Performs several checks on variable declaration of type `Boolean`. |
JavaBooleanUsageCheck | Bug Prevention | .java | Finds incorrect use of passing boolean values in `setAttribute` calls. |
JavaClassNameCheck | Naming Conventions | .java | Checks if class names follow naming conventions. |
JavaCleanUpMethodSuperCleanUpCheck | Bug Prevention | .java | Checks that `cleanUp` method in `*Tag` class with `@Override` annotation calls the `cleanUp` method of the superclass. |
[JavaCleanUpMethodVariablesCheck](checks/java_clean_up_method_variables_check.markdown#javacleanupmethodvariablescheck) | Bug Prevention | .java | Checks that variables in `Tag` classes get cleaned up properly. |
JavaCollapseImportsCheck | Performance | .java | Collapses imports that use wildcard |
[JavaCollatorUtilCheck](checks/java_collator_util_check.markdown#javacollatorutilcheck) | Bug Prevention | .java | Checks for correct use of `Collator`. |
[JavaComponentActivateCheck](checks/java_component_activate_check.markdown#javacomponentactivatecheck) | Naming Conventions | .java | Checks if methods with annotation `@Activate` or `@Deactivate` follow naming conventions. |
JavaComponentAnnotationsCheck | Bug Prevention | .java | Performs several checks on classes with `@Component` annotation. |
[JavaConfigurationAdminCheck](checks/java_configuration_admin_check.markdown#javaconfigurationadmincheck) | Bug Prevention | .java | Checks for correct use of `location == ?` when calling `org.osgi.service.cm.ConfigurationAdmin#createFactoryConfiguration`. |
[JavaConfigurationCategoryCheck](checks/java_configuration_category_check.markdown#javaconfigurationcategorycheck) | Bug Prevention | .java | Checks that the value of `category` in `@ExtendedObjectClassDefinition` matches the `categoryKey` of the corresponding class in `configuration-admin-web`. |
[JavaConstructorParametersCheck](checks/java_constructor_parameters_check.markdown#javaconstructorparameterscheck) | Styling | .java | Checks that the order of variable assignments matches the order of the parameters in the constructor signature. |
JavaConstructorSuperCallCheck | Styling | .java | Finds unnecessary call to no-argument constructor of the superclass. |
JavaDeprecatedJavadocCheck | Javadoc | .java | Checks if the `@deprecated` javadoc is pointing to the correct version. |
JavaDeprecatedKernelClassesCheck | Bug Prevention | .java | Finds calls to deprecated classes `com.liferay.portal.kernel.util.CharPool` and `com.liferay.portal.kernel.util.StringPool`. |
JavaDeserializationSecurityCheck | Security | .java | Finds Java serialization vulnerabilities. |
JavaDiamondOperatorCheck | Miscellaneous | .java | Finds cases where Diamond Operator is not used. |
JavaDuplicateVariableCheck | Miscellaneous | .java | Finds variables where a variable with the same name already exists in an extended class. |
[JavaElseStatementCheck](checks/java_else_statement_check.markdown#javaelsestatementcheck) | Miscellaneous | .java | Finds unnecessary `else` statements (when the `if` statement ends with a `return` statement). |
JavaEmptyLineAfterSuperCallCheck | Miscellaneous | .java | Finds missing emptly line after a `super` call. |
JavaEmptyLinesCheck | Styling | .java | Finds missing and unnecessary empty lines. |
JavaExceptionCheck | Naming Conventions | .java | Checks that variable names of exceptions in `catch` statements follow naming conventions. |
JavaFinalVariableCheck | Styling | .java | Finds cases of unneeded `final` modifiers for variables and parameters. |
[JavaFinderCacheCheck](checks/java_finder_cache_check.markdown#javafindercachecheck) | Bug Prevention | .java | Checks that the method `BasePersistenceImpl.fetchByPrimaryKey` is overridden, when using `FinderPath`. |
JavaFinderImplCustomSQLCheck | Bug Prevention | .java | Checks that hardcoded SQL values in `*FinderImpl` classes match the SQL in the `.xml` file in the `custom-sql` directory. |
[JavaForLoopCheck](checks/java_for_loop_check.markdown#javaforloopcheck) | Styling | .java | Checks if a Enhanced For Loop can be used instead of a Simple For Loop. |
[JavaHelperUtilCheck](checks/java_helper_util_check.markdown#javahelperutilcheck) | Naming Conventions | .java | Finds incorrect use of `*Helper` or `*Util` classes. |
JavaHibernateSQLCheck | Performance | .java | Finds calls to `com.liferay.portal.kernel.dao.orm.Session.createSQLQuery` (use `Session.createSynchronizedSQLQuery` instead). |
JavaIOExceptionCheck | Styling | .java | Validates use of `IOException`. |
JavaIgnoreAnnotationCheck | Bug Prevention | .java | Finds methods with `@Ignore` annotation in test classes. |
JavaIllegalImportsCheck | Bug Prevention | .java | Finds cases of incorrect use of certain classes. |
JavaImportsCheck | Styling | .java | Sorts and groups imports in `.java` files. |
[JavaIndexableCheck](checks/java_indexable_check.markdown#javaindexablecheck) | Bug Prevention | .java | Checks that the type gets returned when using annotation `@Indexable`. |
JavaInnerClassImportsCheck | Styling | .java | Finds cases where inner classes are imported. |
JavaInterfaceCheck | Bug Prevention | .java | Checks that `interface` is not `static`. |
JavaInternalPackageCheck | Bug Prevention | .java | Performs several checks on class in `internal` package. |
JavaJSPDynamicIncludeCheck | Bug Prevention | .java | Performs several checks on `*JSPDynamicInclude` class. |
[JavaLocalSensitiveComparisonCheck](checks/java_local_sensitive_comparison_check.markdown#javalocalsensitivecomparisoncheck) | Bug Prevention | .java | Checks that `java.text.Collator` is used when comparing localized values. |
JavaLogClassNameCheck | Bug Prevention | .java | Checks the name of the class that is passed in `LogFactoryUtil.getLog`. |
[JavaLogLevelCheck](checks/java_log_level_check.markdown#javaloglevelcheck) | Bug Prevention | .java | Checks that the correct log messages are printed. |
JavaLongLinesCheck | Styling | .java | Finds lines that are longer than the specified maximum line length. |
JavaMapBuilderGenericsCheck | Bug Prevention | .java | Finds missing or unnecessary generics on `*MapBuilder.put` calls. |
[JavaMetaAnnotationsCheck](checks/java_meta_annotations_check.markdown#javametaannotationscheck) | Bug Prevention | .java | Checks for correct use of attributes `description` and `name` in annotation `@aQute.bnd.annotation.metatype.Meta`. |
JavaMissingOverrideCheck | Bug Prevention | .java | Finds missing @Override annotations. |
JavaMissingXMLPublicIdsCheck | Bug Prevention | .java | Finds missing public IDs for check XML files. |
JavaModifiedServiceMethodCheck | Bug Prevention | .java | Finds missing empty lines before `removedService` or `addingService` calls. |
[JavaModuleComponentCheck](checks/java_module_component_check.markdown#javamodulecomponentcheck) | Bug Prevention | .java | Checks for use of `@Component` in `-api` or `-spi` modules. |
[JavaModuleExposureCheck](checks/java_module_exposure_check.markdown#javamoduleexposurecheck) | Bug Prevention | .java | Checks for exposure of `SPI` types in `API`. |
JavaModuleIllegalImportsCheck | Bug Prevention | .java | Finds cases of incorrect use of certain classes in modules. |
JavaModuleInternalImportsCheck | Bug Prevention | .java | Finds cases where a module imports an `internal` class from another class. |
JavaModuleJavaxPortletInitParamTemplatePathCheck | Bug Prevention | .java | Validates the value of `javax.portlet.init-param.template-path`. |
JavaModuleServiceProxyFactoryCheck | Bug Prevention | .java | Finds cases of `ServiceProxyFactory.newServiceTrackedInstance`. |
JavaModuleServiceReferenceCheck | Bug Prevention | .java | Finds cases where `@BeanReference` annotation should be used instead of `@ServiceReference` annotation. |
[JavaModuleTestCheck](checks/java_module_test_check.markdown#javamoduletestcheck) | Bug Prevention | .java | Checks package names in tests. |
[JavaMultiPlusConcatCheck](checks/java_multi_plus_concat_check.markdown#javamultiplusconcatcheck) | Performance | .java | Checks that we do not concatenate more than 3 String objects. |
JavaOSGiReferenceCheck | Bug Prevention | .java | Performs several checks on classes with `@Component` annotation. |
[JavaPackagePathCheck](checks/java_package_path_check.markdown#javapackagepathcheck) | Bug Prevention | .java | Checks that the package name matches the file location. |
[JavaProcessCallableCheck](checks/java_process_callable_check.markdown#javaprocesscallablecheck) | Bug Prevention | .java | Checks that a class implementing `ProcessCallable` assigns a `serialVersionUID`. |
JavaProviderTypeAnnotationCheck | Bug Prevention | .java | Performs several checks on classes with `@ProviderType` annotation. |
JavaRedundantConstructorCheck | Bug Prevention | .java | Finds unnecessary empty constructor. |
JavaReleaseInfoCheck | Bug Prevention | .java | Validates information in `ReleaseInfo.java`. |
[JavaResultSetCheck](checks/java_result_set_check.markdown#javaresultsetcheck) | Bug Prevention | .java | Checks for correct use `java.sql.ResultSet.getInt(int)`. |
JavaReturnStatementCheck | Styling | .java | Finds unnecessary `else` statement (when `if` and `else` statement both end with `return` statement). |
[JavaSeeAnnotationCheck](checks/java_see_annotation_check.markdown#javaseeannotationcheck) | Bug Prevention | .java | Checks for nested annotations inside `@see`. |
JavaServiceImplCheck | Bug Prevention | .java | Ensures that `afterPropertiesSet` and `destroy` methods in `*ServiceImpl` always call the method with the same name in the superclass. |
JavaServiceObjectCheck | Styling | .java | Checks for correct use of `*.is*` instead of `*.get*` when calling methods generated by ServiceBuilder. |
[JavaServiceTrackerFactoryCheck](checks/java_service_tracker_factory_check.markdown#javaservicetrackerfactorycheck) | Performance | .java | Checks that there are no calls to deprecatred method `ServiceTrackerFactory.open(java.lang.Class)`. |
[JavaServiceUtilCheck](checks/java_service_util_check.markdown#javaserviceutilcheck) | Bug Prevention | .java | Checks that there are no calls to `*ServiceImpl` from a `*ServiceUtil` class. |
JavaSessionCheck | Performance | .java | Finds unnecessary calls to `Session.flush()` (calls that are followed by `Session.clear()`). |
[JavaSignatureParametersCheck](checks/java_signature_parameters_check.markdown#javasignatureparameterscheck) | Styling | .java | Checks the order of parameters. |
JavaSourceFormatterDocumentationCheck | Documentation | .java | Finds SourceFormatter checks that have no documentation. |
JavaStagedModelDataHandlerCheck | Bug Prevention | .java | Finds missing method `setMvccVersion` in class extending `BaseStagedModelDataHandler` in module that has `mvcc-enabled=true` in `service.xml`. |
JavaStaticBlockCheck | Bug Prevention | .java | Performs several checks on `static` blocks. |
[JavaStaticImportsCheck](checks/java_static_imports_check.markdown#javastaticimportscheck) | Styling | .java | Checks that there are no static imports. |
JavaStaticMethodCheck | Bug Prevention | .java | Finds cases where methods are unncessarily declared static. |
JavaStaticVariableDependencyCheck | Bug Prevention | .java | Checks that static variables in the same class that depend on each other are correctly defined. |
[JavaStopWatchCheck](checks/java_stop_watch_check.markdown#javastopwatchcheck) | Bug Prevention | .java | Checks for potential NullPointerException when using `StopWatch`. |
[JavaStringBundlerConcatCheck](checks/java_string_bundler_concat_check.markdown#javastringbundlerconcatcheck) | Performance | .java | Finds calls to `StringBundler.concat` with less than 3 parameters. |
JavaStringBundlerInitialCapacityCheck | Performance | .java | Checks the initial capacity of new instances of `StringBundler`. |
JavaStringStartsWithSubstringCheck | Bug Prevention | .java | Checks for uses of `contains` followed by `substring`, which should be `startsWith` instead. |
JavaStylingCheck | Styling | .java | Applies rules to enforce consisteny in code style. |
[JavaSwitchCheck](checks/java_switch_check.markdown#javaswitchcheck) | Styling | .java | Checks that `if/else` statement is used instead of `switch` statement. |
JavaSystemEventAnnotationCheck | Bug Prevention | .java | Finds missing method `setDeletionSystemEventStagedModelTypes` in class with annotation @SystemEvent. |
JavaSystemExceptionCheck | Bug Prevention | .java | Finds unnecessary SystemExceptions. |
JavaTaglibMethodCheck | Bug Prevention | .java | Checks that a `*Tag` class has a `set*` and `get*` or `is*` method for each attribute. |
JavaTermDividersCheck | Styling | .java | Finds missing or unnecessary empty lines between javaterms. |
JavaTermOrderCheck | Styling | .java | Checks the order of javaterms. |
JavaTermStylingCheck | Styling | .java | Applies rules to enforce consisteny in code style. |
[JavaTestMethodAnnotationsCheck](checks/java_test_method_annotations_check.markdown#javatestmethodannotationscheck) | Naming Conventions | .java | Checks if methods with test annotations follow the naming conventions. |
JavaTransactionBoundaryCheck | Bug Prevention | .java | Finds direct `add*` or `get*` calls in `*ServiceImpl` (those should use the `*service` global variable instead). |
[JavaUnsafeCastingCheck](checks/java_unsafe_casting_check.markdown#javaunsafecastingcheck) | Bug Prevention | .java | Checks for potential ClassCastException. |
[JavaUnusedSourceFormatterChecksCheck](checks/java_unused_source_formatter_checks_check.markdown#javaunusedsourceformattercheckscheck) | Miscellaneous | .java | Finds `*Check` classes that are not configured. |
[JavaUpgradeAlterCheck](checks/java_upgrade_alter_check.markdown#javaupgradealtercheck) | Bug Prevention | .java | Performs several checks on `alter` calls in Upgrade classes. |
[JavaUpgradeClassCheck](checks/java_upgrade_class_check.markdown#javaupgradeclasscheck) | Bug Prevention | .java | Performs several checks on Upgrade classes. |
JavaUpgradeConnectionCheck | Bug Prevention | .java | Finds cases where `DataAccess.getConnection` is used (instead of using the availabe global variable `connection`). |
[JavaUpgradeIndexCheck](checks/java_upgrade_index_check.markdown#javaupgradeindexcheck) | Bug Prevention | .java | Finds cases where the service builder indexes are updated manually in Upgrade classes. This is not needed because Liferay takes care of it. |
JavaUpgradeVersionCheck | Bug Prevention | .java | Verifies that the correct upgrade versions are used in classes that implement `UpgradeStepRegistrator`. |
JavaVariableTypeCheck | Bug Prevention | .java | Performs several checks on the modifiers on variables. |
JavaVerifyUpgradeConnectionCheck | Bug Prevention | .java | Finds cases where `DataAccess.getConnection` is used (instead of using the availabe global variable `connection`). |
JavaXMLSecurityCheck | Security | .java | Finds possible XXE or Quadratic Blowup security vulnerabilities. |
JavadocCheck | Javadoc | .java | Performs several checks on javadoc. |
[JavadocStyleCheck](https://checkstyle.sourceforge.io/config_javadoc.html#JavadocStyle) | Javadoc | .java | Validates Javadoc comments to help ensure they are well formed. |
LFRBuildContentCheck | Bug Prevention | .lfrbuild-* | Finds `.lfrbuild*` files that are not empty. |
LFRBuildReadmeCheck | Documentation | .lfrbuild-* | Checks that `.lfrbuild*` files are documented in a marker file. |
LPS42924Check | Bug Prevention | .java | Finds cases where `PortalUtil.getClassName*` (instead of calling `classNameLocalService` directly). |
[LambdaCheck](checks/lambda_check.markdown#lambdacheck) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that `lambda` statements are as simple as possible. |
LanguageKeysCheck | Bug Prevention | .java, .js or .jsx | Finds missing language keys in `Language.properties`. |
[ListUtilCheck](checks/list_util_check.markdown#listutilcheck) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks for utilization of class `ListUtil`. |
LiteralStringEqualsCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds cases where `Objects.equals` should be used. |
[LocalFinalVariableNameCheck](https://checkstyle.sourceforge.io/config_naming.html#LocalFinalVariableName) | Naming Conventions | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that local final variable names conform to a specified pattern. |
LocalPatternCheck | Performance | .java | Checks that a `java.util.Pattern` variable is declared globally, so that it is initiated only once. |
[LocalVariableNameCheck](https://checkstyle.sourceforge.io/config_naming.html#LocalVariableName) | Naming Conventions | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that local, non-final variable names conform to a specified pattern. |
LocaleUtilCheck | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds cases where `com.liferay.portal.kernel.util.LocaleUtil` should be used (instead of `java.util.Locale`). |
LogMessageCheck | Styling | .java | Validates messages that are passed to `log.*` calls. |
LogParametersCheck | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Validates the values of parameters passed to `_log.*` calls. |
[MVCCommandNameCheck](checks/mvc_command_name_check.markdown#mvccommandnamecheck) | Naming Conventions | .java | Checks for consistent naming for values of `mvc.command.name`. |
MapBuilderCheck | Miscellaneous | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that `ConcurrentHashMapBuilder`, `HashMapBuilder`, `LinkedHashMapBuilder` or `TreeMapBuilder` is used when possible. |
[MapIterationCheck](checks/map_iteration_check.markdown#mapiterationcheck) | Performance | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that there are no unnecessary map iterations. |
MarkdownEmptyLinesCheck | Styling | .markdown or .md | Finds missing and unnecessary empty lines. |
MarkdownFileExtensionCheck | Styling | .markdown or .md | Finds `markdown` files with `.md` extension (use `.markdown`). |
MarkdownSourceFormatterDocumentationCheck | Documentation | .markdown or .md | Validates the header of Source Formatter documentation files. |
MarkdownSourceFormatterReadmeCheck | Documentation | .markdown or .md | Generates Source Formatter documentation index files. |
MarkdownStylingCheck | Styling | .markdown or .md | Applies rules to enforce consisteny in code style. |
MarkdownWhitespaceCheck | Styling | .markdown or .md | Finds missing and unnecessary whitespace in `.markdown` files. |
[MemberNameCheck](https://checkstyle.sourceforge.io/config_naming.html#MemberName) | Naming Conventions | .java, .java, .jsp, .jsp, .jspf, .jspf, .tag, .tag, .tpl, .tpl, .vm or .vm | Checks that instance variable names conform to a specified pattern. |
MethodCallsOrderCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Sorts method calls for certain object (for example, `put` calls in `java.util.HashMap`). |
[MethodNameCheck](https://checkstyle.sourceforge.io/config_naming.html#MethodName) | Naming Conventions | .java, .java, .jsp, .jsp, .jspf, .jspf, .tag, .tag, .tpl, .tpl, .vm or .vm | Checks that method names conform to a specified pattern. |
MethodNamingCheck | Naming Conventions | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that method names follow naming conventions. |
[MethodParamPadCheck](https://checkstyle.sourceforge.io/config_whitespace.html#MethodParamPad) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks the padding between the identifier of a method definition, constructor definition, method call, or constructor invocation; and the left parenthesis of the parameter list. |
MissingAuthorCheck | Javadoc | .java | Finds classes that have no `@author` specified. |
[MissingDeprecatedCheck](https://checkstyle.sourceforge.io/config_annotation.html#MissingDeprecated) | Bug Prevention | .java | Verifies that the annotation @Deprecated and the Javadoc tag @deprecated are both present when either of them is present. |
MissingDeprecatedJavadocCheck | Javadoc | .java | Verifies that the annotation @Deprecated and the Javadoc tag @deprecated are both present when either of them is present. |
MissingDiamondOperatorCheck | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks for missing diamond operator for types that require diamond operator. |
[MissingEmptyLineCheck](checks/missing_empty_line_check.markdown#missingemptylinecheck) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks for missing line breaks around variable declarations. |
MissingModifierCheck | Bug Prevention | .java | Verifies that a method or global variable has a modifier specified. |
MissingParenthesesCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds missing parentheses in conditional statement. |
[ModifierOrderCheck](https://checkstyle.sourceforge.io/config_modifier.html#ModifierOrder) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that the order of modifiers conforms to the suggestions in the Java Language specification, § 8.1.1, 8.3.1, 8.4.3 and 9.4. |
[MultipleVariableDeclarationsCheck](https://checkstyle.sourceforge.io/config_coding.html#MultipleVariableDeclarations) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that each variable declaration is in its own statement and on its own line. |
NestedFieldAnnotationCheck | Bug Prevention | .java | Verifies that `NestedFieldSupport.class` is used in `service` property of `Component` annotation |
NestedIfStatementCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds nested if statements that can be combined. |
NewFileCheck | Bug Prevention | | Finds new files in directories that should not have added files. |
[NoLineWrapCheck](https://checkstyle.sourceforge.io/config_whitespace.html#NoLineWrap) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that chosen statements are not line-wrapped. |
[NoWhitespaceAfterCheck](https://checkstyle.sourceforge.io/config_whitespace.html#NoWhitespaceAfter) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that there is no whitespace after a token. |
[NoWhitespaceBeforeCheck](https://checkstyle.sourceforge.io/config_whitespace.html#NoWhitespaceBefore) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that there is no whitespace before a token. |
NonbreakingSpaceCheck | Styling | | Finds `no break space` (`\u00a0`) characters. |
NotRequireThisCheck | Styling | .java | Finds cases of unnecessary use of `this.`. |
[NullAssertionInIfStatementCheck](checks/null_assertion_in_if_statement_check.markdown#nullassertioninifstatementcheck) | Bug Prevention | .java | Verifies that null check should always be first in if-statement. |
NumberSuffixCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Verifies that uppercase `D`, `F`, or `L` is used when denoting Double/Float/Long. |
OSGiResourceBuilderCheck | Styling | .java | Avoid using *Resource.builder. |
[OneStatementPerLineCheck](https://checkstyle.sourceforge.io/config_coding.html#OneStatementPerLine) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that there is only one statement per line. |
OperatorOperandCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Verifies that operand do not go over too many lines and make the operator hard to read. |
OperatorOrderCheck | Styling | .java | Verifies that when an operator has a literal string or a number as one of the operands, it is always on the right hand side. |
[OperatorWrapCheck](https://checkstyle.sourceforge.io/config_whitespace.html#OperatorWrap) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks the policy on how to wrap lines on operators. |
[PackageNameCheck](https://checkstyle.sourceforge.io/config_naming.html#PackageName) | Naming Conventions | .java | Checks that package names conform to a specified pattern. |
PackageinfoBNDExportPackageCheck | Bug Prevention | packageinfo | Finds legacy `packageinfo` files. |
[ParameterNameCheck](https://checkstyle.sourceforge.io/config_naming.html#ParameterName) | Naming Conventions | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that method parameter names conform to a specified pattern. |
ParsePrimitiveTypeCheck | Performance | .java, .jsp, .jspf, .tag, .tpl or .vm | Verifies that `GetterUtil.parse*` is used to parse primitive types, when possible. |
PersistenceCallCheck | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds illegal persistence calls across component boundaries. |
[PersistenceUpdateCheck](checks/persistence_update_check.markdown#persistenceupdatecheck) | Bug Prevention | .java | Checks that there are no stale references in service code from persistence updates. |
PlusStatementCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Performs several checks to statements where `+` is used for concatenation. |
PortletURLBuilderCheck | Miscellaneous | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that `PortletURLBuilder` is used when possible. |
PoshiDependenciesFileLocationCheck | Bug Prevention | .function, .macro or .testcase | Checks that dependencies files are located in the correct directory. |
PoshiStylingCheck | Styling | .function, .macro or .testcase | Applies rules to enforce consisteny in code style. |
PrimitiveWrapperInstantiationCheck | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds cases where `new Type` is used for primitive types (use `Type.valueOf` instead). |
PrincipalExceptionCheck | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds calls to `PrincipalException.class.getName()` (use `PrincipalException.getNestedClasses()` instead). |
PropertiesArchivedModulesCheck | Bug Prevention | .properties | Finds `test.batch.class.names.includes` property value pointing to archived modules in `test.properties`. |
PropertiesBuildIncludeDirsCheck | Bug Prevention | .properties | Verifies property value of `build.include.dirs` in `build.properties`. |
PropertiesCommentsCheck | Styling | .properties | Validates comments in `.properties` files. |
PropertiesDefinitionKeysCheck | Styling | .properties | Sorts definition keys in `liferay-plugin-package.properties` file. |
PropertiesDependenciesFileCheck | Styling | .properties | Sorts the properties in `dependencies.properties` file. |
PropertiesEmptyLinesCheck | Styling | .properties | Finds missing and unnecessary empty lines. |
PropertiesImportedFilesContentCheck | Bug Prevention | .properties | Performs several checks on `imported-files.properties` file. |
[PropertiesLanguageKeysCheck](checks/properties_language_keys_check.markdown#propertieslanguagekeyscheck) | Bug Prevention | .properties | Checks that there is no HTML markup in language keys. |
PropertiesLanguageKeysOrderCheck | Styling | .properties | Sort language keys in `Language.properties` file. |
PropertiesLiferayPluginPackageFileCheck | Bug Prevention | .properties | Performs several checks on `liferay-plugin-package.properties` file. |
PropertiesLiferayPluginPackageLiferayVersionsCheck | Bug Prevention | .properties | Validates the version in `liferay-plugin-package.properties` file. |
PropertiesLongLinesCheck | Styling | .properties | Finds lines that are longer than the specified maximum line length. |
PropertiesMultiLineValuesOrderCheck | Styling | .properties | Verifies that property with multiple values is not on a single line. |
PropertiesPortalEnvironmentVariablesCheck | Documentation | .properties | Verifies that the environment property in the documentation matches the property name. |
PropertiesPortalFileCheck | Bug Prevention | .properties | Performs several checks on `portal.properties` or `portal-*.properties` file. |
PropertiesPortletFileCheck | Bug Prevention | .properties | Performs several checks on `portlet.properties` file. |
PropertiesReleaseBuildCheck | Bug Prevention | .properties | Verifies that the information in `release.properties` matches the information in `ReleaseInfo.java`. |
PropertiesServiceKeysCheck | Bug Prevention | .properties | Finds usage of legacy properties in `service.properties`. |
PropertiesSourceFormatterContentCheck | Bug Prevention | .properties | Performs several checks on `source-formatter.properties` file. |
PropertiesSourceFormatterFileCheck | Bug Prevention | .properties | Performs several checks on `source-formatter.properties` file. |
PropertiesStylingCheck | Styling | .properties | Applies rules to enforce consisteny in code style. |
PropertiesVerifyPropertiesCheck | Bug Prevention | .properties | Finds usage of legacy properties in `portal.properties` or `system.properties`. |
PropertiesWhitespaceCheck | Styling | .properties | Finds missing and unnecessary whitespace in `.properties` files. |
PythonClassesAndMethodsOrderCheck | Styling | | Checks the order of classes and methods. |
PythonImportsCheck | Styling | | Sorts and groups imports in `.py` files. |
PythonStylingCheck | Styling | | Applies rules to enforce consisteny in code style. |
PythonWhitespaceCheck | Styling | | Finds missing and unnecessary whitespace. |
RedundantBranchingStatementCheck | Performance | .java | Finds unnecessary branching (`break`, `continue` or `return`) statements. |
ReferenceAnnotationCheck | Bug Prevention | .java | Performs several checks on classes with @Reference annotation. |
[RequireThisCheck](https://checkstyle.sourceforge.io/config_coding.html#RequireThis) | Bug Prevention | .java | Checks that references to instance variables and methods of the present object are explicitly of the form 'this.varName' or 'this.methodName(args)' and that those references don't rely on the default behavior when 'this.' is absent. |
[ResourceBundleCheck](checks/resource_bundle_check.markdown#resourcebundlecheck) | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that there are no calls to `java.util.ResourceBundle.getBundle`. |
SQLEmptyLinesCheck | Styling | .sql | Finds missing and unnecessary empty lines. |
[SQLLongNamesCheck](checks/sql_long_names_check.markdown#sqllongnamescheck) | Bug Prevention | .sql | Checks for table and column names that exceed 30 characters. |
SQLStylingCheck | Styling | .sql | Applies rules to enforce consisteny in code style. |
SelfReferenceCheck | Bug Prevention | .java | Finds cases of unnecessary reference to its own class. |
SemiColonCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds cases of unnecessary semicolon. |
SessionKeysCheck | Naming Conventions | .java | Checks that messages send to `SessionsErrors` or `SessionMessages` follow naming conventions. |
SetUtilMethodsCheck | Performance | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds cases of inefficient SetUtil operations. |
SingleStatementClauseCheck | Styling | .java | Verifies that `for`, `if` or `while` statement always uses curly braces. |
SizeIsZeroCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds cases of calls like `list.size() == 0` (use `list.isEmpty()` instead). |
SlantedQuotesCheck | Styling | | Finds `slanted quote` (`\u201a`, `\u201b`, `\u201c`, `\u201d`, `\u201e`, `\u201f`, `\u2018` or `\u2019`) characters. |
SoyEmptyLinesCheck | Styling | .soy | Finds missing and unnecessary empty lines. |
[StaticBlockCheck](checks/static_block_check.markdown#staticblockcheck) | Bug Prevention | .java | Performs several checks on static blocks. |
[StaticVariableNameCheck](https://checkstyle.sourceforge.io/config_naming.html#StaticVariableName) | Naming Conventions | .java, .java, .jsp, .jsp, .jspf, .jspf, .tag, .tag, .tpl, .tpl, .vm or .vm | Checks that static, non-final variable names conform to a specified pattern. |
StringBundlerNamingCheck | Naming Conventions | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks for consistent naming on variables of type 'StringBundler'. |
StringCastCheck | Performance | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds cases where a redundant `toString()` is called on variable type `String`. |
[StringLiteralEqualityCheck](https://checkstyle.sourceforge.io/config_coding.html#StringLiteralEquality) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that string literals are not used with == or !=. |
[StringMethodsCheck](checks/string_methods_check.markdown#stringmethodscheck) | Performance | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks if performance can be improved by using different String operation methods. |
SubstringCheck | Performance | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds cases like `s.substring(1, s.length())` (use `s.substring(1)` instead). |
SystemEventCheck | Bug Prevention | .java | Finds missing or redundant usage of @SystemEvent for delete events. |
TLDElementOrderCheck | Styling | .tld | Checks the order of attributers in `.tld` file. |
TLDTypeCheck | Bug Prevention | .tld | Ensures the fully qualified name is used for types in `.tld` file. |
TXTEmptyLinesCheck | Styling | | Finds missing and unnecessary empty lines. |
TXTStylingCheck | Styling | | Applies rules to enforce consisteny in code style. |
TernaryOperatorCheck | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds use of ternary operator in `java` files (use if statement instead). |
TestClassCheck | Naming Conventions | .java | Checks that names of test classes follow naming conventions. |
TestClassMissingLiferayUnitTestRuleCheck | Bug Prevention | .java | Finds missing LiferayUnitTestRule. |
ThreadLocalUtilCheck | Performance | .java | Finds new instances of `java.lang.Thread` (use `ThreadLocalUtil.create` instead). |
ThreadNameCheck | Naming Conventions | .java | Checks that names of threads follow naming conventions. |
TransactionalTestRuleCheck | Bug Prevention | .java | Finds usage of `TransactionalTestRule` in `*StagedModelDataHandlerTest`. |
TryWithResourcesCheck | Performance | .java | Ensures using Try-With-Resources statement to properly close the resource. |
[TypeNameCheck](https://checkstyle.sourceforge.io/config_naming.html#TypeName) | Naming Conventions | .java | Checks that type names conform to a specified pattern. |
UnicodePropertiesBuilderCheck | Miscellaneous | .java | Checks that `UnicodePropertiesBuilder` is used when possible. |
[UnnecessaryAssignCheck](checks/unnecessary_assign_check.markdown#unnecessaryassigncheck) | Performance | .java | Finds unnecessary assign statements (when it is either reassigned or returned right after). |
UnnecessaryMethodCallCheck | Styling | .java | Finds unnecessary method calls. |
[UnnecessaryParenthesesCheck](https://checkstyle.sourceforge.io/config_coding.html#UnnecessaryParentheses) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks if unnecessary parentheses are used in a statement or expression. |
UnnecessaryTypeCastCheck | Performance | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds unnecessary Type Casting. |
[UnnecessaryVariableDeclarationCheck](checks/unnecessary_variable_declaration_check.markdown#unnecessaryvariabledeclarationcheck) | Performance | .java | Finds unnecessary variable declarations (when it is either reassigned or returned right after). |
UnparameterizedClassCheck | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Finds `Class` instantation without generic type. |
UnprocessedExceptionCheck | Performance | .java | Finds cases where an `Exception` is swallowed without being processed. |
UnusedMethodCheck | Performance | .java | Finds private methods that are not used. |
UnusedParameterCheck | Performance | .java | Finds parameters in private methods that are not used. |
UnusedVariableCheck | Performance | .java | Finds variables that are declared, but not used. |
UnwrappedVariableInfoCheck | Bug Prevention | .java | Finds cases where the variable should be wrapped into an inner class in order to defer array elements initialization. |
UpgradeDeprecatedAPICheck | Upgrade | .java | Finds calls to deprecated classes, constructors, fields or methods after an upgrade |
UpgradeJavaCheck | Upgrade | | Performs upgrade checks for `java` files |
UpgradeRemovedAPICheck | Upgrade | .java | Finds cases where calls are made to removed API after an upgrade. |
[ValidatorEqualsCheck](checks/validator_equals_check.markdown#validatorequalscheck) | Performance | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that there are no calls to `Validator.equals(Object, Object)`. |
ValidatorIsNullCheck | Bug Prevention | .java, .jsp, .jspf, .tag, .tpl or .vm | Ensures that only variable of type `Long`, `Serializable` or `String` is passed to method `com.liferay.portal.kernel.util.Validator.isNull`. |
VariableDeclarationAsUsedCheck | Performance | .java | Finds cases where a variable declaration can be inlined or moved closer to where it is used. |
VariableNameCheck | Naming Conventions | .java | Checks that variable names follow naming conventions. |
[WhitespaceAfterCheck](https://checkstyle.sourceforge.io/config_whitespace.html#WhitespaceAfter) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that a token is followed by whitespace, with the exception that it does not check for whitespace after the semicolon of an empty for iterator. |
[WhitespaceAroundCheck](https://checkstyle.sourceforge.io/config_whitespace.html#WhitespaceAround) | Styling | .java, .jsp, .jspf, .tag, .tpl or .vm | Checks that a token is surrounded by whitespace. |
WhitespaceCheck | Styling | .cfg, .config, .cql, .css, .dtd, .gradle, .groovy, .scss, .sh, .soy, .sql, .tld, .ts, Dockerfile or packageinfo | Finds missing and unnecessary whitespace. |
XMLBuildFileCheck | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Performs several checks on `build.xml`. |
XMLCDATACheck | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Performs several checks on `CDATA` inside `xml`. |
XMLCheckstyleFileCheck | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Performs several checks on `checkstyle.xml` file. |
XMLCustomSQLOrderCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of attributes in `custom-sql` file. |
XMLCustomSQLStylingCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Applies rules to enforce consisteny in code style for `.xml` files in directory `custom-sql`. |
XMLDDLStructuresFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of attributes in `-structures.xml` file. |
XMLDTDVersionCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the DTD version in `*.xml` file. |
XMLEmptyLinesCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Finds missing and unnecessary empty lines. |
XMLFSBExcludeFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of attributes in `fsb-exclude.xml` file. |
XMLFriendlyURLRoutesFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Performs several checks on `*-routes.xml` file. |
XMLHBMFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of imports in `*-hbm.xml` file. |
XMLImportsCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Sorts and groups imports in `.xml` files. |
XMLIndentationCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .tld, .toggle, .wsdl, .xml or .xsd | Finds incorrect indentation in `.xml` files. |
XMLIvyFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of dependcies in `ivy.xml` file. |
XMLLog4jFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of categories in `*-log4j.xml` file. |
XMLLookAndFeelCompatibilityVersionCheck | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Finds missing attribute `version` in `compatibility` element in `*--look-and-feel.xml` file. |
XMLLookAndFeelFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of attributes in `*--look-and-feel.xml` file. |
XMLModelHintsFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of attributes in `*-model-hints.xml` file. |
XMLPomFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of dependencies in `pom.xml` file. |
XMLPortletFileCheck | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Performs several checks on `portlet.xml` file. |
XMLPortletPreferencesFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of elements in files in directory `resource-actions`. |
XMLPoshiFileCheck | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Performs several checks on poshi files. |
XMLProjectElementCheck | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the project name in `.pom` file. |
XMLResourceActionsFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of elements in files in directory `resource-actions`. |
[XMLServiceEntityNameCheck](checks/xml_service_entity_name_check.markdown#xmlserviceentitynamecheck) | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks that the `entity name` in `service.xml` does not equal the `package name`. |
XMLServiceFileCheck | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Performs several checks on `service.xml` file. |
[XMLServiceFinderNameCheck](checks/xml_service_finder_name_check.markdown#xmlservicefindernamecheck) | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks that the `finder name` in `service.xml`. |
XMLServiceReferenceCheck | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks for unused references in `service.xml` file. |
XMLSourcechecksFileCheck | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Performs several checks on `sourcechecks.xml` file. |
XMLSpringFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of elements in `*-spring.xml` file. |
XMLStrutsConfigFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of elements in `struts-config.xml` file. |
XMLStylingCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Applies rules to enforce consisteny in code style. |
XMLSuppressionsFileCheck | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Performs several checks on `source-formatter-suppressions.xml` file. |
XMLTagAttributesCheck | Bug Prevention | .action, .function, .html, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Performs several checks on tag attributes. |
XMLTestIgnorableErrorLinesFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of elements in `test-ignorable-error-lines.xml` file. |
XMLTilesDefsFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of elements in `tiles-defs.xml` file. |
XMLToggleFileCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the order of elements in `.toggle` file. |
XMLUpgradeRemovedDefinitionsCheck | Upgrade | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Finds removed XML definitions when upgrading. |
XMLWebFileCheck | Bug Prevention | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Performs several checks on `web.xml` file. |
XMLWhitespaceCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Finds missing and unnecessary whitespace in `.xml` files. |
XMLWorkflowDefinitionFileNameCheck | Styling | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Checks the file name of workflow definition files. |
YMLDefinitionOrderCheck | Styling | .yaml or .yml | Sorts definitions alphabetically in `.yml` files. |
YMLEmptyLinesCheck | Styling | .yaml or .yml | Finds missing and unnecessary empty lines. |
YMLLongLinesCheck | Styling | .yaml or .yml | Finds lines that are longer than the specified maximum line length. |
YMLStylingCheck | Styling | .yaml or .yml | Applies rules to enforce consisteny in code style. |
YMLWhitespaceCheck | Styling | .yaml or .yml | Finds missing and unnecessary whitespace in `.yml` files. |