# Upgrade Checks

Check | File Extensions | Description
----- | --------------- | -----------
JSPUpgradeRemovedTagsCheck | .jsp, .jspf, .tag, .tpl or .vm | Finds removed tags when upgrading. |
UpgradeDeprecatedAPICheck | .java | Finds calls to deprecated classes, constructors, fields or methods after an upgrade |
UpgradeJavaCheck | | Performs upgrade checks for `java` files |
UpgradeRemovedAPICheck | .java | Finds cases where calls are made to removed API after an upgrade. |
XMLUpgradeRemovedDefinitionsCheck | .action, .function, .jrxml, .macro, .pom, .testcase, .toggle, .wsdl, .xml or .xsd | Finds removed XML definitions when upgrading. |