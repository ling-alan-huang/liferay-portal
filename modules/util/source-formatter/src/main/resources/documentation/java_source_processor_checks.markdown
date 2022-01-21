# Checks for .java

Check | Category | Description
----- | -------- | -----------
[AnnotationUseStyleCheck](https://checkstyle.sourceforge.io/config_annotation.html#AnnotationUseStyle) | Styling | Checks the style of elements in annotations. |
[AnonymousClassCheck](checks/anonymous_class_check.markdown#anonymousclasscheck) | Bug Prevention | Checks for serialization issue when using anonymous class. |
AppendCheck | Styling | Checks instances where literal Strings are appended. |
ArquillianCheck | Bug Prevention | Checks for correct use of `com.liferay.arquillian.extension.junit.bridge.junit.Arquillian`. |
[ArrayCheck](checks/array_check.markdown#arraycheck) | Performance | Checks if performance can be improved by using different mehods that can be used by collections. |
[ArrayTypeStyleCheck](https://checkstyle.sourceforge.io/config_misc.html#ArrayTypeStyle) | Styling | Checks the style of array type definitions. |
[AssertEqualsCheck](checks/assert_equals_check.markdown#assertequalscheck) | Styling | Checks that additional information is provided when calling `Assert.assertEquals`. |
AssignAsUsedCheck | Performance | Finds cases where an assign statement can be inlined or moved closer to where it is used. |
[AttributeOrderCheck](checks/attribute_order_check.markdown#attributeordercheck) | Styling | Checks that attributes in anonymous classes are ordered alphabetically. |
[AvoidNestedBlocksCheck](https://checkstyle.sourceforge.io/config_blocks.html#AvoidNestedBlocks) | Styling | Finds nested blocks (blocks that are used freely in the code). |
[AvoidStarImportCheck](https://checkstyle.sourceforge.io/config_imports.html#AvoidStarImport) | Bug Prevention | Checks that there are no import statements that use the * notation. |
[CamelCaseNameCheck](checks/camel_case_name_check.markdown#camelcasenamecheck) | Naming Conventions | Checks variable names for correct use of `CamelCase`. |
[ChainingCheck](checks/chaining_check.markdown#chainingcheck) | Styling | Checks that chaining is only applied on certain types and methods. |
[CompanyIterationCheck](checks/company_iteration_check.markdown#companyiterationcheck) | Bug Prevention | Checks that `CompanyLocalService.forEachCompany` or `CompanyLocalService.forEachCompanyId` is used when iterating over companies |
CompatClassImportsCheck | Bug Prevention | Checks that classes are imported from `compat` modules, when possible. |
ConcatCheck | Performance | Checks for correct use of `StringBundler.concat`. |
ConstantNameCheck | Naming Conventions | Checks that variable names of constants follow correct naming rules. |
ConstructorGlobalVariableDeclarationCheck | Performance | Checks that initial values of global variables are not set in the constructor. |
[ConstructorMissingEmptyLineCheck](checks/constructor_missing_empty_line_check.markdown#constructormissingemptylinecheck) | Styling | Checks for line breaks when assiging variables in constructor. |
ConsumerTypeAnnotationCheck | Bug Prevention | Performs several checks on classes with @ConsumerType annotation. |
ContractionsCheck | Styling | Finds contractions in Strings (such as `can't` or `you're`). |
[CopyrightCheck](checks/copyright_check.markdown#copyrightcheck) | Styling | Validates `copyright` header. |
CreationMenuBuilderCheck | Miscellaneous | Checks that `CreationMenuBuilder` is used when possible. |
DTOEnumCreationCheck | Bug Prevention | Checks the creation of DTO enum. |
[DefaultComesLastCheck](https://checkstyle.sourceforge.io/config_coding.html#DefaultComesLast) | Styling | Checks that the `default` is after all the cases in a `switch` statement. |
DeprecatedAPICheck | Bug Prevention | Finds calls to deprecated classes, constructors, fields or methods. |
EmptyCollectionCheck | Styling | Checks that there are no calls to `Collections.EMPTY_LIST`, `Collections.EMPTY_MAP` or `Collections.EMPTY_SET`. |
EmptyConstructorCheck | Bug Prevention | Finds unnecessary empty constructors. |
EnumConstantDividerCheck | Styling | Find unnecessary empty lines between enum constants. |
EnumConstantOrderCheck | Styling | Checks the order of enum constants. |
EqualClauseIfStatementsCheck | Styling | Finds consecutive if-statements with identical clauses. |
[ExceptionCheck](checks/exception_check.markdown#exceptioncheck) | Performance | Finds private methods that throw unnecessary exception. |
ExceptionMessageCheck | Styling | Validates messages that are passed to exceptions. |
ExceptionVariableNameCheck | Naming Conventions | Validates variable names that have type `*Exception`. |
FactoryCheck | Bug Prevention | Finds cases where `*Factory` should be used when creating new instances of an object. |
FilterStringWhitespaceCheck | Bug Prevention | Finds missing and unnecessary whitespace in the value of the filter string in `ServiceTrackerFactory.open` or `WaiterUtil.waitForFilter`. |
[FrameworkBundleCheck](checks/framework_bundle_check.markdown#frameworkbundlecheck) | Performance | Checks that `org.osgi.framework.Bundle.getHeaders()` is not used. |
FullyQualifiedNameCheck | Miscellaneous | Finds cases where a Fully Qualified Name is used instead of importing a class. |
[GenericTypeCheck](checks/generic_type_check.markdown#generictypecheck) | Bug Prevention | Checks that generics are always specified to provide compile-time checking and removing the risk of `ClassCastException` during runtime. |
[GetterUtilCheck](checks/getter_util_check.markdown#getterutilcheck) | Styling | Finds cases where the default value is passed to `GetterUtil.get*` or `ParamUtil.get*`. |
[IfStatementCheck](checks/if_statement_check.markdown#ifstatementcheck) | Styling | Finds empty if-statements and consecutive if-statements with identical bodies |
InstanceofOrderCheck | Styling | Check the order of `instanceof` calls. |
ItemBuilderCheck | Miscellaneous | Checks that `DropdownItemBuilder`, `LabelItemBuilder` or `NavigationItemBuilder` is used when possible. |
ItemListBuilderCheck | Miscellaneous | Checks that `DropdownItemListBuilder`, `LabelItemListBuilder` or `NavigationItemListBuilder` is used when possible. |
JSONNamingCheck | Naming Conventions | Checks if variable names follow naming conventions. |
[JSONUtilCheck](checks/json_util_check.markdown#jsonutilcheck) | Styling | Checks for utilization of class `JSONUtil`. |
Java2HTMLCheck | Miscellaneous | Finds incorrect use of `.java.html` in `.jsp` files. |
[JavaAPISignatureCheck](checks/java_api_signature_check.markdown#javaapisignaturecheck) | Bug Prevention | Checks that types `HttpServletRequest`, `HttpServletResponse`, `ThemeDisplay`, and `ServiceContext` are not used in API method signatures. |
JavaAbstractMethodCheck | Bug Prevention | Finds incorrect `abstract` methods in `interface`. |
JavaAggregateTestRuleParameterOrderCheck | Styling | Checks the order of parameters in `new AggregateTestRule` calls. |
JavaAnnotationDefaultAttributeCheck | Styling | Finds cases where the default value is passed to annotations in package `*.bnd.annotations` or `*.bind.annotations`. |
JavaAnnotationsCheck | Bug Prevention | Performs several checks on annotations. |
[JavaAnonymousInnerClassCheck](checks/java_anonymous_inner_class_check.markdown#javaanonymousinnerclasscheck) | Bug Prevention | Performs several checks on anonymous classes. |
JavaAssertEqualsCheck | Styling | Validates `Assert.assertEquals` calls. |
[JavaBaseUpgradeCallableCheck](checks/java_base_upgrade_callable_check.markdown#javabaseupgradecallablecheck) | Bug Prevention | Checks that BaseUpgradeCallable is used instead of Callable or Runnable in Upgrade and Verify classes. |
JavaBooleanStatementCheck | Bug Prevention | Performs several checks on variable declaration of type `Boolean`. |
JavaBooleanUsageCheck | Bug Prevention | Finds incorrect use of passing boolean values in `setAttribute` calls. |
JavaClassNameCheck | Naming Conventions | Checks if class names follow naming conventions. |
JavaCleanUpMethodSuperCleanUpCheck | Bug Prevention | Checks that `cleanUp` method in `*Tag` class with `@Override` annotation calls the `cleanUp` method of the superclass. |
[JavaCleanUpMethodVariablesCheck](checks/java_clean_up_method_variables_check.markdown#javacleanupmethodvariablescheck) | Bug Prevention | Checks that variables in `Tag` classes get cleaned up properly. |
JavaCollapseImportsCheck | Performance | Collapses imports that use wildcard |
[JavaCollatorUtilCheck](checks/java_collator_util_check.markdown#javacollatorutilcheck) | Bug Prevention | Checks for correct use of `Collator`. |
[JavaComponentActivateCheck](checks/java_component_activate_check.markdown#javacomponentactivatecheck) | Naming Conventions | Checks if methods with annotation `@Activate` or `@Deactivate` follow naming conventions. |
JavaComponentAnnotationsCheck | Bug Prevention | Performs several checks on classes with `@Component` annotation. |
[JavaConfigurationAdminCheck](checks/java_configuration_admin_check.markdown#javaconfigurationadmincheck) | Bug Prevention | Checks for correct use of `location == ?` when calling `org.osgi.service.cm.ConfigurationAdmin#createFactoryConfiguration`. |
[JavaConfigurationCategoryCheck](checks/java_configuration_category_check.markdown#javaconfigurationcategorycheck) | Bug Prevention | Checks that the value of `category` in `@ExtendedObjectClassDefinition` matches the `categoryKey` of the corresponding class in `configuration-admin-web`. |
[JavaConstructorParametersCheck](checks/java_constructor_parameters_check.markdown#javaconstructorparameterscheck) | Styling | Checks that the order of variable assignments matches the order of the parameters in the constructor signature. |
JavaConstructorSuperCallCheck | Styling | Finds unnecessary call to no-argument constructor of the superclass. |
JavaDeprecatedJavadocCheck | Javadoc | Checks if the `@deprecated` javadoc is pointing to the correct version. |
JavaDeprecatedKernelClassesCheck | Bug Prevention | Finds calls to deprecated classes `com.liferay.portal.kernel.util.CharPool` and `com.liferay.portal.kernel.util.StringPool`. |
JavaDeserializationSecurityCheck | Security | Finds Java serialization vulnerabilities. |
JavaDiamondOperatorCheck | Miscellaneous | Finds cases where Diamond Operator is not used. |
JavaDuplicateVariableCheck | Miscellaneous | Finds variables where a variable with the same name already exists in an extended class. |
[JavaElseStatementCheck](checks/java_else_statement_check.markdown#javaelsestatementcheck) | Miscellaneous | Finds unnecessary `else` statements (when the `if` statement ends with a `return` statement). |
JavaEmptyLineAfterSuperCallCheck | Miscellaneous | Finds missing emptly line after a `super` call. |
JavaEmptyLinesCheck | Styling | Finds missing and unnecessary empty lines. |
JavaExceptionCheck | Naming Conventions | Checks that variable names of exceptions in `catch` statements follow naming conventions. |
JavaFinalVariableCheck | Styling | Finds cases of unneeded `final` modifiers for variables and parameters. |
[JavaFinderCacheCheck](checks/java_finder_cache_check.markdown#javafindercachecheck) | Bug Prevention | Checks that the method `BasePersistenceImpl.fetchByPrimaryKey` is overridden, when using `FinderPath`. |
JavaFinderImplCustomSQLCheck | Bug Prevention | Checks that hardcoded SQL values in `*FinderImpl` classes match the SQL in the `.xml` file in the `custom-sql` directory. |
[JavaForLoopCheck](checks/java_for_loop_check.markdown#javaforloopcheck) | Styling | Checks if a Enhanced For Loop can be used instead of a Simple For Loop. |
[JavaHelperUtilCheck](checks/java_helper_util_check.markdown#javahelperutilcheck) | Naming Conventions | Finds incorrect use of `*Helper` or `*Util` classes. |
JavaHibernateSQLCheck | Performance | Finds calls to `com.liferay.portal.kernel.dao.orm.Session.createSQLQuery` (use `Session.createSynchronizedSQLQuery` instead). |
JavaIOExceptionCheck | Styling | Validates use of `IOException`. |
JavaIgnoreAnnotationCheck | Bug Prevention | Finds methods with `@Ignore` annotation in test classes. |
JavaIllegalImportsCheck | Bug Prevention | Finds cases of incorrect use of certain classes. |
JavaImportsCheck | Styling | Sorts and groups imports in `.java` files. |
[JavaIndexableCheck](checks/java_indexable_check.markdown#javaindexablecheck) | Bug Prevention | Checks that the type gets returned when using annotation `@Indexable`. |
JavaInnerClassImportsCheck | Styling | Finds cases where inner classes are imported. |
JavaInterfaceCheck | Bug Prevention | Checks that `interface` is not `static`. |
JavaInternalPackageCheck | Bug Prevention | Performs several checks on class in `internal` package. |
JavaJSPDynamicIncludeCheck | Bug Prevention | Performs several checks on `*JSPDynamicInclude` class. |
[JavaLocalSensitiveComparisonCheck](checks/java_local_sensitive_comparison_check.markdown#javalocalsensitivecomparisoncheck) | Bug Prevention | Checks that `java.text.Collator` is used when comparing localized values. |
JavaLogClassNameCheck | Bug Prevention | Checks the name of the class that is passed in `LogFactoryUtil.getLog`. |
[JavaLogLevelCheck](checks/java_log_level_check.markdown#javaloglevelcheck) | Bug Prevention | Checks that the correct log messages are printed. |
JavaLongLinesCheck | Styling | Finds lines that are longer than the specified maximum line length. |
JavaMapBuilderGenericsCheck | Bug Prevention | Finds missing or unnecessary generics on `*MapBuilder.put` calls. |
[JavaMetaAnnotationsCheck](checks/java_meta_annotations_check.markdown#javametaannotationscheck) | Bug Prevention | Checks for correct use of attributes `description` and `name` in annotation `@aQute.bnd.annotation.metatype.Meta`. |
JavaMissingOverrideCheck | Bug Prevention | Finds missing @Override annotations. |
JavaMissingXMLPublicIdsCheck | Bug Prevention | Finds missing public IDs for check XML files. |
JavaModifiedServiceMethodCheck | Bug Prevention | Finds missing empty lines before `removedService` or `addingService` calls. |
[JavaModuleComponentCheck](checks/java_module_component_check.markdown#javamodulecomponentcheck) | Bug Prevention | Checks for use of `@Component` in `-api` or `-spi` modules. |
[JavaModuleExposureCheck](checks/java_module_exposure_check.markdown#javamoduleexposurecheck) | Bug Prevention | Checks for exposure of `SPI` types in `API`. |
JavaModuleIllegalImportsCheck | Bug Prevention | Finds cases of incorrect use of certain classes in modules. |
JavaModuleInternalImportsCheck | Bug Prevention | Finds cases where a module imports an `internal` class from another class. |
JavaModuleJavaxPortletInitParamTemplatePathCheck | Bug Prevention | Validates the value of `javax.portlet.init-param.template-path`. |
JavaModuleServiceProxyFactoryCheck | Bug Prevention | Finds cases of `ServiceProxyFactory.newServiceTrackedInstance`. |
JavaModuleServiceReferenceCheck | Bug Prevention | Finds cases where `@BeanReference` annotation should be used instead of `@ServiceReference` annotation. |
[JavaModuleTestCheck](checks/java_module_test_check.markdown#javamoduletestcheck) | Bug Prevention | Checks package names in tests. |
[JavaMultiPlusConcatCheck](checks/java_multi_plus_concat_check.markdown#javamultiplusconcatcheck) | Performance | Checks that we do not concatenate more than 3 String objects. |
JavaOSGiReferenceCheck | Bug Prevention | Performs several checks on classes with `@Component` annotation. |
[JavaPackagePathCheck](checks/java_package_path_check.markdown#javapackagepathcheck) | Bug Prevention | Checks that the package name matches the file location. |
[JavaProcessCallableCheck](checks/java_process_callable_check.markdown#javaprocesscallablecheck) | Bug Prevention | Checks that a class implementing `ProcessCallable` assigns a `serialVersionUID`. |
JavaProviderTypeAnnotationCheck | Bug Prevention | Performs several checks on classes with `@ProviderType` annotation. |
JavaRedundantConstructorCheck | Bug Prevention | Finds unnecessary empty constructor. |
JavaReleaseInfoCheck | Bug Prevention | Validates information in `ReleaseInfo.java`. |
[JavaResultSetCheck](checks/java_result_set_check.markdown#javaresultsetcheck) | Bug Prevention | Checks for correct use `java.sql.ResultSet.getInt(int)`. |
JavaReturnStatementCheck | Styling | Finds unnecessary `else` statement (when `if` and `else` statement both end with `return` statement). |
[JavaSeeAnnotationCheck](checks/java_see_annotation_check.markdown#javaseeannotationcheck) | Bug Prevention | Checks for nested annotations inside `@see`. |
JavaServiceImplCheck | Bug Prevention | Ensures that `afterPropertiesSet` and `destroy` methods in `*ServiceImpl` always call the method with the same name in the superclass. |
JavaServiceObjectCheck | Styling | Checks for correct use of `*.is*` instead of `*.get*` when calling methods generated by ServiceBuilder. |
[JavaServiceTrackerFactoryCheck](checks/java_service_tracker_factory_check.markdown#javaservicetrackerfactorycheck) | Performance | Checks that there are no calls to deprecatred method `ServiceTrackerFactory.open(java.lang.Class)`. |
[JavaServiceUtilCheck](checks/java_service_util_check.markdown#javaserviceutilcheck) | Bug Prevention | Checks that there are no calls to `*ServiceImpl` from a `*ServiceUtil` class. |
JavaSessionCheck | Performance | Finds unnecessary calls to `Session.flush()` (calls that are followed by `Session.clear()`). |
[JavaSignatureParametersCheck](checks/java_signature_parameters_check.markdown#javasignatureparameterscheck) | Styling | Checks the order of parameters. |
JavaSourceFormatterDocumentationCheck | Documentation | Finds SourceFormatter checks that have no documentation. |
JavaStagedModelDataHandlerCheck | Bug Prevention | Finds missing method `setMvccVersion` in class extending `BaseStagedModelDataHandler` in module that has `mvcc-enabled=true` in `service.xml`. |
JavaStaticBlockCheck | Bug Prevention | Performs several checks on `static` blocks. |
[JavaStaticImportsCheck](checks/java_static_imports_check.markdown#javastaticimportscheck) | Styling | Checks that there are no static imports. |
JavaStaticMethodCheck | Bug Prevention | Finds cases where methods are unncessarily declared static. |
JavaStaticVariableDependencyCheck | Bug Prevention | Checks that static variables in the same class that depend on each other are correctly defined. |
[JavaStopWatchCheck](checks/java_stop_watch_check.markdown#javastopwatchcheck) | Bug Prevention | Checks for potential NullPointerException when using `StopWatch`. |
[JavaStringBundlerConcatCheck](checks/java_string_bundler_concat_check.markdown#javastringbundlerconcatcheck) | Performance | Finds calls to `StringBundler.concat` with less than 3 parameters. |
JavaStringBundlerInitialCapacityCheck | Performance | Checks the initial capacity of new instances of `StringBundler`. |
JavaStringStartsWithSubstringCheck | Bug Prevention | Checks for uses of `contains` followed by `substring`, which should be `startsWith` instead. |
JavaStylingCheck | Styling | Applies rules to enforce consisteny in code style. |
[JavaSwitchCheck](checks/java_switch_check.markdown#javaswitchcheck) | Styling | Checks that `if/else` statement is used instead of `switch` statement. |
JavaSystemEventAnnotationCheck | Bug Prevention | Finds missing method `setDeletionSystemEventStagedModelTypes` in class with annotation @SystemEvent. |
JavaSystemExceptionCheck | Bug Prevention | Finds unnecessary SystemExceptions. |
JavaTaglibMethodCheck | Bug Prevention | Checks that a `*Tag` class has a `set*` and `get*` or `is*` method for each attribute. |
JavaTermDividersCheck | Styling | Finds missing or unnecessary empty lines between javaterms. |
JavaTermOrderCheck | Styling | Checks the order of javaterms. |
JavaTermStylingCheck | Styling | Applies rules to enforce consisteny in code style. |
[JavaTestMethodAnnotationsCheck](checks/java_test_method_annotations_check.markdown#javatestmethodannotationscheck) | Naming Conventions | Checks if methods with test annotations follow the naming conventions. |
JavaTransactionBoundaryCheck | Bug Prevention | Finds direct `add*` or `get*` calls in `*ServiceImpl` (those should use the `*service` global variable instead). |
[JavaUnsafeCastingCheck](checks/java_unsafe_casting_check.markdown#javaunsafecastingcheck) | Bug Prevention | Checks for potential ClassCastException. |
[JavaUnusedSourceFormatterChecksCheck](checks/java_unused_source_formatter_checks_check.markdown#javaunusedsourceformattercheckscheck) | Miscellaneous | Finds `*Check` classes that are not configured. |
[JavaUpgradeAlterCheck](checks/java_upgrade_alter_check.markdown#javaupgradealtercheck) | Bug Prevention | Performs several checks on `alter` calls in Upgrade classes. |
[JavaUpgradeClassCheck](checks/java_upgrade_class_check.markdown#javaupgradeclasscheck) | Bug Prevention | Performs several checks on Upgrade classes. |
JavaUpgradeConnectionCheck | Bug Prevention | Finds cases where `DataAccess.getConnection` is used (instead of using the availabe global variable `connection`). |
[JavaUpgradeIndexCheck](checks/java_upgrade_index_check.markdown#javaupgradeindexcheck) | Bug Prevention | Finds cases where the service builder indexes are updated manually in Upgrade classes. This is not needed because Liferay takes care of it. |
JavaUpgradeVersionCheck | Bug Prevention | Verifies that the correct upgrade versions are used in classes that implement `UpgradeStepRegistrator`. |
JavaVariableTypeCheck | Bug Prevention | Performs several checks on the modifiers on variables. |
JavaVerifyUpgradeConnectionCheck | Bug Prevention | Finds cases where `DataAccess.getConnection` is used (instead of using the availabe global variable `connection`). |
JavaXMLSecurityCheck | Security | Finds possible XXE or Quadratic Blowup security vulnerabilities. |
JavadocCheck | Javadoc | Performs several checks on javadoc. |
[JavadocStyleCheck](https://checkstyle.sourceforge.io/config_javadoc.html#JavadocStyle) | Javadoc | Validates Javadoc comments to help ensure they are well formed. |
LPS42924Check | Bug Prevention | Finds cases where `PortalUtil.getClassName*` (instead of calling `classNameLocalService` directly). |
[LambdaCheck](checks/lambda_check.markdown#lambdacheck) | Styling | Checks that `lambda` statements are as simple as possible. |
LanguageKeysCheck | Bug Prevention | Finds missing language keys in `Language.properties`. |
[ListUtilCheck](checks/list_util_check.markdown#listutilcheck) | Styling | Checks for utilization of class `ListUtil`. |
LiteralStringEqualsCheck | Styling | Finds cases where `Objects.equals` should be used. |
[LocalFinalVariableNameCheck](https://checkstyle.sourceforge.io/config_naming.html#LocalFinalVariableName) | Naming Conventions | Checks that local final variable names conform to a specified pattern. |
LocalPatternCheck | Performance | Checks that a `java.util.Pattern` variable is declared globally, so that it is initiated only once. |
[LocalVariableNameCheck](https://checkstyle.sourceforge.io/config_naming.html#LocalVariableName) | Naming Conventions | Checks that local, non-final variable names conform to a specified pattern. |
LocaleUtilCheck | Bug Prevention | Finds cases where `com.liferay.portal.kernel.util.LocaleUtil` should be used (instead of `java.util.Locale`). |
LogMessageCheck | Styling | Validates messages that are passed to `log.*` calls. |
LogParametersCheck | Bug Prevention | Validates the values of parameters passed to `_log.*` calls. |
[MVCCommandNameCheck](checks/mvc_command_name_check.markdown#mvccommandnamecheck) | Naming Conventions | Checks for consistent naming for values of `mvc.command.name`. |
MapBuilderCheck | Miscellaneous | Checks that `ConcurrentHashMapBuilder`, `HashMapBuilder`, `LinkedHashMapBuilder` or `TreeMapBuilder` is used when possible. |
[MapIterationCheck](checks/map_iteration_check.markdown#mapiterationcheck) | Performance | Checks that there are no unnecessary map iterations. |
[MemberNameCheck](https://checkstyle.sourceforge.io/config_naming.html#MemberName) | Naming Conventions | Checks that instance variable names conform to a specified pattern. |
MethodCallsOrderCheck | Styling | Sorts method calls for certain object (for example, `put` calls in `java.util.HashMap`). |
[MethodNameCheck](https://checkstyle.sourceforge.io/config_naming.html#MethodName) | Naming Conventions | Checks that method names conform to a specified pattern. |
MethodNamingCheck | Naming Conventions | Checks that method names follow naming conventions. |
[MethodParamPadCheck](https://checkstyle.sourceforge.io/config_whitespace.html#MethodParamPad) | Styling | Checks the padding between the identifier of a method definition, constructor definition, method call, or constructor invocation; and the left parenthesis of the parameter list. |
MissingAuthorCheck | Javadoc | Finds classes that have no `@author` specified. |
[MissingDeprecatedCheck](https://checkstyle.sourceforge.io/config_annotation.html#MissingDeprecated) | Bug Prevention | Verifies that the annotation @Deprecated and the Javadoc tag @deprecated are both present when either of them is present. |
MissingDeprecatedJavadocCheck | Javadoc | Verifies that the annotation @Deprecated and the Javadoc tag @deprecated are both present when either of them is present. |
MissingDiamondOperatorCheck | Bug Prevention | Checks for missing diamond operator for types that require diamond operator. |
[MissingEmptyLineCheck](checks/missing_empty_line_check.markdown#missingemptylinecheck) | Styling | Checks for missing line breaks around variable declarations. |
MissingModifierCheck | Bug Prevention | Verifies that a method or global variable has a modifier specified. |
MissingParenthesesCheck | Styling | Finds missing parentheses in conditional statement. |
[ModifierOrderCheck](https://checkstyle.sourceforge.io/config_modifier.html#ModifierOrder) | Styling | Checks that the order of modifiers conforms to the suggestions in the Java Language specification, § 8.1.1, 8.3.1, 8.4.3 and 9.4. |
[MultipleVariableDeclarationsCheck](https://checkstyle.sourceforge.io/config_coding.html#MultipleVariableDeclarations) | Styling | Checks that each variable declaration is in its own statement and on its own line. |
NestedFieldAnnotationCheck | Bug Prevention | Verifies that `NestedFieldSupport.class` is used in `service` property of `Component` annotation |
NestedIfStatementCheck | Styling | Finds nested if statements that can be combined. |
[NoLineWrapCheck](https://checkstyle.sourceforge.io/config_whitespace.html#NoLineWrap) | Styling | Checks that chosen statements are not line-wrapped. |
[NoWhitespaceAfterCheck](https://checkstyle.sourceforge.io/config_whitespace.html#NoWhitespaceAfter) | Styling | Checks that there is no whitespace after a token. |
[NoWhitespaceBeforeCheck](https://checkstyle.sourceforge.io/config_whitespace.html#NoWhitespaceBefore) | Styling | Checks that there is no whitespace before a token. |
NotRequireThisCheck | Styling | Finds cases of unnecessary use of `this.`. |
[NullAssertionInIfStatementCheck](checks/null_assertion_in_if_statement_check.markdown#nullassertioninifstatementcheck) | Bug Prevention | Verifies that null check should always be first in if-statement. |
NumberSuffixCheck | Styling | Verifies that uppercase `D`, `F`, or `L` is used when denoting Double/Float/Long. |
OSGiResourceBuilderCheck | Styling | Avoid using *Resource.builder. |
[OneStatementPerLineCheck](https://checkstyle.sourceforge.io/config_coding.html#OneStatementPerLine) | Styling | Checks that there is only one statement per line. |
OperatorOperandCheck | Styling | Verifies that operand do not go over too many lines and make the operator hard to read. |
OperatorOrderCheck | Styling | Verifies that when an operator has a literal string or a number as one of the operands, it is always on the right hand side. |
[OperatorWrapCheck](https://checkstyle.sourceforge.io/config_whitespace.html#OperatorWrap) | Styling | Checks the policy on how to wrap lines on operators. |
[PackageNameCheck](https://checkstyle.sourceforge.io/config_naming.html#PackageName) | Naming Conventions | Checks that package names conform to a specified pattern. |
[ParameterNameCheck](https://checkstyle.sourceforge.io/config_naming.html#ParameterName) | Naming Conventions | Checks that method parameter names conform to a specified pattern. |
ParsePrimitiveTypeCheck | Performance | Verifies that `GetterUtil.parse*` is used to parse primitive types, when possible. |
PersistenceCallCheck | Bug Prevention | Finds illegal persistence calls across component boundaries. |
[PersistenceUpdateCheck](checks/persistence_update_check.markdown#persistenceupdatecheck) | Bug Prevention | Checks that there are no stale references in service code from persistence updates. |
PlusStatementCheck | Styling | Performs several checks to statements where `+` is used for concatenation. |
PortletURLBuilderCheck | Miscellaneous | Checks that `PortletURLBuilder` is used when possible. |
PrimitiveWrapperInstantiationCheck | Bug Prevention | Finds cases where `new Type` is used for primitive types (use `Type.valueOf` instead). |
PrincipalExceptionCheck | Bug Prevention | Finds calls to `PrincipalException.class.getName()` (use `PrincipalException.getNestedClasses()` instead). |
RedundantBranchingStatementCheck | Performance | Finds unnecessary branching (`break`, `continue` or `return`) statements. |
ReferenceAnnotationCheck | Bug Prevention | Performs several checks on classes with @Reference annotation. |
[RequireThisCheck](https://checkstyle.sourceforge.io/config_coding.html#RequireThis) | Bug Prevention | Checks that references to instance variables and methods of the present object are explicitly of the form 'this.varName' or 'this.methodName(args)' and that those references don't rely on the default behavior when 'this.' is absent. |
[ResourceBundleCheck](checks/resource_bundle_check.markdown#resourcebundlecheck) | Bug Prevention | Checks that there are no calls to `java.util.ResourceBundle.getBundle`. |
SelfReferenceCheck | Bug Prevention | Finds cases of unnecessary reference to its own class. |
SemiColonCheck | Styling | Finds cases of unnecessary semicolon. |
SessionKeysCheck | Naming Conventions | Checks that messages send to `SessionsErrors` or `SessionMessages` follow naming conventions. |
SetUtilMethodsCheck | Performance | Finds cases of inefficient SetUtil operations. |
SingleStatementClauseCheck | Styling | Verifies that `for`, `if` or `while` statement always uses curly braces. |
SizeIsZeroCheck | Styling | Finds cases of calls like `list.size() == 0` (use `list.isEmpty()` instead). |
[StaticBlockCheck](checks/static_block_check.markdown#staticblockcheck) | Bug Prevention | Performs several checks on static blocks. |
[StaticVariableNameCheck](https://checkstyle.sourceforge.io/config_naming.html#StaticVariableName) | Naming Conventions | Checks that static, non-final variable names conform to a specified pattern. |
StringBundlerNamingCheck | Naming Conventions | Checks for consistent naming on variables of type 'StringBundler'. |
StringCastCheck | Performance | Finds cases where a redundant `toString()` is called on variable type `String`. |
[StringLiteralEqualityCheck](https://checkstyle.sourceforge.io/config_coding.html#StringLiteralEquality) | Styling | Checks that string literals are not used with == or !=. |
[StringMethodsCheck](checks/string_methods_check.markdown#stringmethodscheck) | Performance | Checks if performance can be improved by using different String operation methods. |
SubstringCheck | Performance | Finds cases like `s.substring(1, s.length())` (use `s.substring(1)` instead). |
SystemEventCheck | Bug Prevention | Finds missing or redundant usage of @SystemEvent for delete events. |
TernaryOperatorCheck | Styling | Finds use of ternary operator in `java` files (use if statement instead). |
TestClassCheck | Naming Conventions | Checks that names of test classes follow naming conventions. |
TestClassMissingLiferayUnitTestRuleCheck | Bug Prevention | Finds missing LiferayUnitTestRule. |
ThreadLocalUtilCheck | Performance | Finds new instances of `java.lang.Thread` (use `ThreadLocalUtil.create` instead). |
ThreadNameCheck | Naming Conventions | Checks that names of threads follow naming conventions. |
TransactionalTestRuleCheck | Bug Prevention | Finds usage of `TransactionalTestRule` in `*StagedModelDataHandlerTest`. |
TryWithResourcesCheck | Performance | Ensures using Try-With-Resources statement to properly close the resource. |
[TypeNameCheck](https://checkstyle.sourceforge.io/config_naming.html#TypeName) | Naming Conventions | Checks that type names conform to a specified pattern. |
UnicodePropertiesBuilderCheck | Miscellaneous | Checks that `UnicodePropertiesBuilder` is used when possible. |
[UnnecessaryAssignCheck](checks/unnecessary_assign_check.markdown#unnecessaryassigncheck) | Performance | Finds unnecessary assign statements (when it is either reassigned or returned right after). |
UnnecessaryMethodCallCheck | Styling | Finds unnecessary method calls. |
[UnnecessaryParenthesesCheck](https://checkstyle.sourceforge.io/config_coding.html#UnnecessaryParentheses) | Styling | Checks if unnecessary parentheses are used in a statement or expression. |
UnnecessaryTypeCastCheck | Performance | Finds unnecessary Type Casting. |
[UnnecessaryVariableDeclarationCheck](checks/unnecessary_variable_declaration_check.markdown#unnecessaryvariabledeclarationcheck) | Performance | Finds unnecessary variable declarations (when it is either reassigned or returned right after). |
UnparameterizedClassCheck | Bug Prevention | Finds `Class` instantation without generic type. |
UnprocessedExceptionCheck | Performance | Finds cases where an `Exception` is swallowed without being processed. |
UnusedMethodCheck | Performance | Finds private methods that are not used. |
UnusedParameterCheck | Performance | Finds parameters in private methods that are not used. |
UnusedVariableCheck | Performance | Finds variables that are declared, but not used. |
UnwrappedVariableInfoCheck | Bug Prevention | Finds cases where the variable should be wrapped into an inner class in order to defer array elements initialization. |
UpgradeDeprecatedAPICheck | Upgrade | Finds calls to deprecated classes, constructors, fields or methods after an upgrade |
UpgradeRemovedAPICheck | Upgrade | Finds cases where calls are made to removed API after an upgrade. |
[ValidatorEqualsCheck](checks/validator_equals_check.markdown#validatorequalscheck) | Performance | Checks that there are no calls to `Validator.equals(Object, Object)`. |
ValidatorIsNullCheck | Bug Prevention | Ensures that only variable of type `Long`, `Serializable` or `String` is passed to method `com.liferay.portal.kernel.util.Validator.isNull`. |
VariableDeclarationAsUsedCheck | Performance | Finds cases where a variable declaration can be inlined or moved closer to where it is used. |
VariableNameCheck | Naming Conventions | Checks that variable names follow naming conventions. |
[WhitespaceAfterCheck](https://checkstyle.sourceforge.io/config_whitespace.html#WhitespaceAfter) | Styling | Checks that a token is followed by whitespace, with the exception that it does not check for whitespace after the semicolon of an empty for iterator. |
[WhitespaceAroundCheck](https://checkstyle.sourceforge.io/config_whitespace.html#WhitespaceAround) | Styling | Checks that a token is surrounded by whitespace. |