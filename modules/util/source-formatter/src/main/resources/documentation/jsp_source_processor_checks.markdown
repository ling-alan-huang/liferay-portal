# Checks for .jsp, .jspf, .tag, .tpl or .vm

Check | Category | Description
----- | -------- | -----------
AppendCheck | Styling | Checks instances where literal Strings are appended. |
[ArrayCheck](checks/array_check.markdown#arraycheck) | Performance | Checks if performance can be improved by using different mehods that can be used by collections. |
[ArrayTypeStyleCheck](https://checkstyle.sourceforge.io/config_misc.html#ArrayTypeStyle) | Styling | Checks the style of array type definitions. |
[AvoidNestedBlocksCheck](https://checkstyle.sourceforge.io/config_blocks.html#AvoidNestedBlocks) | Styling | Finds nested blocks (blocks that are used freely in the code). |
[CamelCaseNameCheck](checks/camel_case_name_check.markdown#camelcasenamecheck) | Naming Conventions | Checks variable names for correct use of `CamelCase`. |
[ChainingCheck](checks/chaining_check.markdown#chainingcheck) | Styling | Checks that chaining is only applied on certain types and methods. |
[CompanyIterationCheck](checks/company_iteration_check.markdown#companyiterationcheck) | Bug Prevention | Checks that `CompanyLocalService.forEachCompany` or `CompanyLocalService.forEachCompanyId` is used when iterating over companies |
CompatClassImportsCheck | Bug Prevention | Checks that classes are imported from `compat` modules, when possible. |
ConcatCheck | Performance | Checks for correct use of `StringBundler.concat`. |
ConstantNameCheck | Naming Conventions | Checks that variable names of constants follow correct naming rules. |
ContractionsCheck | Styling | Finds contractions in Strings (such as `can't` or `you're`). |
[CopyrightCheck](checks/copyright_check.markdown#copyrightcheck) | Styling | Validates `copyright` header. |
[DefaultComesLastCheck](https://checkstyle.sourceforge.io/config_coding.html#DefaultComesLast) | Styling | Checks that the `default` is after all the cases in a `switch` statement. |
EmptyCollectionCheck | Styling | Checks that there are no calls to `Collections.EMPTY_LIST`, `Collections.EMPTY_MAP` or `Collections.EMPTY_SET`. |
ExceptionMessageCheck | Styling | Validates messages that are passed to exceptions. |
FactoryCheck | Bug Prevention | Finds cases where `*Factory` should be used when creating new instances of an object. |
[GenericTypeCheck](checks/generic_type_check.markdown#generictypecheck) | Bug Prevention | Checks that generics are always specified to provide compile-time checking and removing the risk of `ClassCastException` during runtime. |
[GetterUtilCheck](checks/getter_util_check.markdown#getterutilcheck) | Styling | Finds cases where the default value is passed to `GetterUtil.get*` or `ParamUtil.get*`. |
[IfStatementCheck](checks/if_statement_check.markdown#ifstatementcheck) | Styling | Finds empty if-statements and consecutive if-statements with identical bodies |
InstanceofOrderCheck | Styling | Check the order of `instanceof` calls. |
JSONNamingCheck | Naming Conventions | Checks if variable names follow naming conventions. |
[JSONUtilCheck](checks/json_util_check.markdown#jsonutilcheck) | Styling | Checks for utilization of class `JSONUtil`. |
[JSPArrowFunctionCheck](checks/jsp_arrow_function_check.markdown#jsparrowfunctioncheck) | Bug Prevention | Checks that there are no array functions. |
JSPCoreTaglibCheck | Styling | Finds cases where a `c:choose` or `c:if` tag can be used instead of an if-statement. |
[JSPDefineObjectsCheck](checks/jsp_define_objects_check.markdown#jspdefineobjectscheck) | Performance | Checks for unnesecarry duplication of code that already exists in `defineObjects`. |
JSPEmptyLinesCheck | Styling | Finds missing and unnecessary empty lines. |
JSPExceptionOrderCheck | Styling | Checks the order of exceptions in `.jsp` files. |
[JSPFileNameCheck](checks/jsp_file_name_check.markdown#jspfilenamecheck) | Naming Conventions | Checks if the file name of `.jsp` or `.jspf` follows the naming conventions. |
[JSPFunctionNameCheck](checks/jsp_function_name_check.markdown#jspfunctionnamecheck) | Naming Conventions | Check if the names of functions in `.jsp` files follow naming conventions. |
[JSPIllegalSyntaxCheck](checks/jsp_illegal_syntax_check.markdown#jspillegalsyntaxcheck) | Bug Prevention | Finds incorrect use of `System.out.print`, `console.log` or `debugger.*` in `.jsp` files. |
[JSPImportsCheck](checks/jsp_imports_check.markdown#jspimportscheck) | Styling | Sorts and groups imports in `.jsp` files. |
[JSPIncludeCheck](checks/jsp_include_check.markdown#jspincludecheck) | Bug Prevention | Validates values of `include` in `.jsp` files. |
JSPIndentationCheck | Styling | Finds incorrect indentation in `.jsp` files. |
JSPInlineVariableCheck | Styling | Finds cases where variables can be inlined. |
JSPJavaParserCheck | Styling | Performs JavaParser on `.java` files. |
JSPLanguageKeysCheck | Bug Prevention | Finds missing language keys in `Language.properties`. |
JSPLanguageUtilCheck | Bug Prevention | Finds cases where Locale is passed to `LanguageUtil.get` instead of `HttpServletRequest`. |
JSPLineBreakCheck | Styling | Finds missing and unnecessary line breaks in `.jsp` lines. |
JSPLogFileNameCheck | Bug Prevention | Validates the value that is passed to `LogFactoryUtil.getLog` in `.jsp`. |
[JSPMethodCallsCheck](checks/jsp_method_calls_check.markdown#jspmethodcallscheck) | Bug Prevention | Checks that type `LiferayPortletResponse` is used to call `getNamespace()`. |
[JSPMissingTaglibsCheck](checks/jsp_missing_taglibs_check.markdown#jspmissingtaglibscheck) | Bug Prevention | Checks for missing taglibs. |
[JSPModuleIllegalImportsCheck](checks/jsp_module_illegal_imports_check.markdown#jspmoduleillegalimportscheck) | Miscellaneous | Finds incorrect use of `com.liferay.registry.Registry` or `com.liferay.util.ContentUtil`. |
JSPParenthesesCheck | Miscellaneous | Finds incorrect use of parentheses in statement. |
JSPRedirectBackURLCheck | Miscellaneous | Validates values of variable `redirect`. |
[JSPSendRedirectCheck](checks/jsp_send_redirect_check.markdown#jspsendredirectcheck) | Bug Prevention | Checks that there are no calls to `HttpServletResponse.sendRedirect` from `jsp` files. |
[JSPServiceUtilCheck](checks/jsp_service_util_check.markdown#jspserviceutilcheck) | Miscellaneous | Finds incorrect use of `*ServiceUtil` in `.jsp` files in modules. |
JSPSessionKeysCheck | Bug Prevention | Checks that messages send to `SessionsErrors` or `SessionMessages` follow naming conventions. |
JSPStylingCheck | Styling | Applies rules to enforce consisteny in code style. |
JSPTagAttributesCheck | Bug Prevention | Performs several checks on tag attributes. |
[JSPTaglibVariableCheck](checks/jsp_taglib_variable_check.markdown#jsptaglibvariablecheck) | Naming Conventions | Checks if variable names follow naming conventions. |
[JSPUnusedJSPFCheck](checks/jsp_unused_jspf_check.markdown#jspunusedjspfcheck) | Performance | Finds `.jspf` files that are not used. |
JSPUnusedTermsCheck | Performance | Finds taglibs, variables and imports that are unused. |
JSPUpgradeRemovedTagsCheck | Upgrade | Finds removed tags when upgrading. |
JSPVarNameCheck | Styling | Checks that values of attribute `var` follow naming conventions. |
JSPVariableOrderCheck | Styling | Checks if variable names are in alphabetical order. |
JSPWhitespaceCheck | Styling | Finds missing and unnecessary whitespace in `.jsp` files. |
JSPXSSVulnerabilitiesCheck | Security | Finds xss vulnerabilities. |
[LambdaCheck](checks/lambda_check.markdown#lambdacheck) | Styling | Checks that `lambda` statements are as simple as possible. |
[ListUtilCheck](checks/list_util_check.markdown#listutilcheck) | Styling | Checks for utilization of class `ListUtil`. |
LiteralStringEqualsCheck | Styling | Finds cases where `Objects.equals` should be used. |
[LocalFinalVariableNameCheck](https://checkstyle.sourceforge.io/config_naming.html#LocalFinalVariableName) | Naming Conventions | Checks that local final variable names conform to a specified pattern. |
[LocalVariableNameCheck](https://checkstyle.sourceforge.io/config_naming.html#LocalVariableName) | Naming Conventions | Checks that local, non-final variable names conform to a specified pattern. |
LocaleUtilCheck | Bug Prevention | Finds cases where `com.liferay.portal.kernel.util.LocaleUtil` should be used (instead of `java.util.Locale`). |
LogParametersCheck | Bug Prevention | Validates the values of parameters passed to `_log.*` calls. |
MapBuilderCheck | Miscellaneous | Checks that `ConcurrentHashMapBuilder`, `HashMapBuilder`, `LinkedHashMapBuilder` or `TreeMapBuilder` is used when possible. |
[MapIterationCheck](checks/map_iteration_check.markdown#mapiterationcheck) | Performance | Checks that there are no unnecessary map iterations. |
[MemberNameCheck](https://checkstyle.sourceforge.io/config_naming.html#MemberName) | Naming Conventions | Checks that instance variable names conform to a specified pattern. |
MethodCallsOrderCheck | Styling | Sorts method calls for certain object (for example, `put` calls in `java.util.HashMap`). |
[MethodNameCheck](https://checkstyle.sourceforge.io/config_naming.html#MethodName) | Naming Conventions | Checks that method names conform to a specified pattern. |
MethodNamingCheck | Naming Conventions | Checks that method names follow naming conventions. |
[MethodParamPadCheck](https://checkstyle.sourceforge.io/config_whitespace.html#MethodParamPad) | Styling | Checks the padding between the identifier of a method definition, constructor definition, method call, or constructor invocation; and the left parenthesis of the parameter list. |
MissingDiamondOperatorCheck | Bug Prevention | Checks for missing diamond operator for types that require diamond operator. |
[MissingEmptyLineCheck](checks/missing_empty_line_check.markdown#missingemptylinecheck) | Styling | Checks for missing line breaks around variable declarations. |
MissingParenthesesCheck | Styling | Finds missing parentheses in conditional statement. |
[ModifierOrderCheck](https://checkstyle.sourceforge.io/config_modifier.html#ModifierOrder) | Styling | Checks that the order of modifiers conforms to the suggestions in the Java Language specification, § 8.1.1, 8.3.1, 8.4.3 and 9.4. |
[MultipleVariableDeclarationsCheck](https://checkstyle.sourceforge.io/config_coding.html#MultipleVariableDeclarations) | Styling | Checks that each variable declaration is in its own statement and on its own line. |
NestedIfStatementCheck | Styling | Finds nested if statements that can be combined. |
[NoLineWrapCheck](https://checkstyle.sourceforge.io/config_whitespace.html#NoLineWrap) | Styling | Checks that chosen statements are not line-wrapped. |
[NoWhitespaceAfterCheck](https://checkstyle.sourceforge.io/config_whitespace.html#NoWhitespaceAfter) | Styling | Checks that there is no whitespace after a token. |
[NoWhitespaceBeforeCheck](https://checkstyle.sourceforge.io/config_whitespace.html#NoWhitespaceBefore) | Styling | Checks that there is no whitespace before a token. |
NumberSuffixCheck | Styling | Verifies that uppercase `D`, `F`, or `L` is used when denoting Double/Float/Long. |
[OneStatementPerLineCheck](https://checkstyle.sourceforge.io/config_coding.html#OneStatementPerLine) | Styling | Checks that there is only one statement per line. |
OperatorOperandCheck | Styling | Verifies that operand do not go over too many lines and make the operator hard to read. |
[OperatorWrapCheck](https://checkstyle.sourceforge.io/config_whitespace.html#OperatorWrap) | Styling | Checks the policy on how to wrap lines on operators. |
[ParameterNameCheck](https://checkstyle.sourceforge.io/config_naming.html#ParameterName) | Naming Conventions | Checks that method parameter names conform to a specified pattern. |
ParsePrimitiveTypeCheck | Performance | Verifies that `GetterUtil.parse*` is used to parse primitive types, when possible. |
PersistenceCallCheck | Bug Prevention | Finds illegal persistence calls across component boundaries. |
PlusStatementCheck | Styling | Performs several checks to statements where `+` is used for concatenation. |
PortletURLBuilderCheck | Miscellaneous | Checks that `PortletURLBuilder` is used when possible. |
PrimitiveWrapperInstantiationCheck | Bug Prevention | Finds cases where `new Type` is used for primitive types (use `Type.valueOf` instead). |
PrincipalExceptionCheck | Bug Prevention | Finds calls to `PrincipalException.class.getName()` (use `PrincipalException.getNestedClasses()` instead). |
[ResourceBundleCheck](checks/resource_bundle_check.markdown#resourcebundlecheck) | Bug Prevention | Checks that there are no calls to `java.util.ResourceBundle.getBundle`. |
SemiColonCheck | Styling | Finds cases of unnecessary semicolon. |
SetUtilMethodsCheck | Performance | Finds cases of inefficient SetUtil operations. |
SizeIsZeroCheck | Styling | Finds cases of calls like `list.size() == 0` (use `list.isEmpty()` instead). |
[StaticVariableNameCheck](https://checkstyle.sourceforge.io/config_naming.html#StaticVariableName) | Naming Conventions | Checks that static, non-final variable names conform to a specified pattern. |
StringBundlerNamingCheck | Naming Conventions | Checks for consistent naming on variables of type 'StringBundler'. |
StringCastCheck | Performance | Finds cases where a redundant `toString()` is called on variable type `String`. |
[StringLiteralEqualityCheck](https://checkstyle.sourceforge.io/config_coding.html#StringLiteralEquality) | Styling | Checks that string literals are not used with == or !=. |
[StringMethodsCheck](checks/string_methods_check.markdown#stringmethodscheck) | Performance | Checks if performance can be improved by using different String operation methods. |
SubstringCheck | Performance | Finds cases like `s.substring(1, s.length())` (use `s.substring(1)` instead). |
TernaryOperatorCheck | Styling | Finds use of ternary operator in `java` files (use if statement instead). |
[UnnecessaryParenthesesCheck](https://checkstyle.sourceforge.io/config_coding.html#UnnecessaryParentheses) | Styling | Checks if unnecessary parentheses are used in a statement or expression. |
UnnecessaryTypeCastCheck | Performance | Finds unnecessary Type Casting. |
UnparameterizedClassCheck | Bug Prevention | Finds `Class` instantation without generic type. |
[ValidatorEqualsCheck](checks/validator_equals_check.markdown#validatorequalscheck) | Performance | Checks that there are no calls to `Validator.equals(Object, Object)`. |
ValidatorIsNullCheck | Bug Prevention | Ensures that only variable of type `Long`, `Serializable` or `String` is passed to method `com.liferay.portal.kernel.util.Validator.isNull`. |
[WhitespaceAfterCheck](https://checkstyle.sourceforge.io/config_whitespace.html#WhitespaceAfter) | Styling | Checks that a token is followed by whitespace, with the exception that it does not check for whitespace after the semicolon of an empty for iterator. |
[WhitespaceAroundCheck](https://checkstyle.sourceforge.io/config_whitespace.html#WhitespaceAround) | Styling | Checks that a token is surrounded by whitespace. |