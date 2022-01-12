# Checks for .gradle

Check | Category | Description
----- | -------- | -----------
GradleBlockOrderCheck | Styling | Sorts logic in gradle build files. |
GradleBodyCheck | Styling | Applies rules to enforce consisteny in the body of gradle build files. |
[GradleDependenciesCheck](checks/gradle_dependencies_check.markdown#gradledependenciescheck) | Performance | Checks that modules are not depending on other modules. |
[GradleDependencyArtifactsCheck](checks/gradle_dependency_artifacts_check.markdown#gradledependencyartifactscheck) | Bug Prevention | Checks that value `default` is not used for attribute `version`. |
GradleDependencyConfigurationCheck | Bug Prevention | Validates the scope of dependencies in build gradle files. |
GradleDependencyVersionCheck | Bug Prevention | Checks the version for dependencies in gradle build files. |
GradleExportedPackageDependenciesCheck | Bug Prevention | Validates dependencies in gradle build files. |
GradleImportsCheck | Styling | Sorts and groups imports in `.gradle` files. |
GradleIndentationCheck | Styling | Finds incorrect indentation in gradle build files. |
GradleJavaVersionCheck | Bug Prevention | Checks values of properties `sourceCompatibility` and `targetCompatibility` in gradle build files. |
GradlePropertiesCheck | Bug Prevention | Validates property values in gradle build files. |
GradleProvidedDependenciesCheck | Bug Prevention | Validates the scope of dependencies in build gradle files. |
[GradleRequiredDependenciesCheck](checks/gradle_required_dependencies_check.markdown#gradlerequireddependenciescheck) | Bug Prevention | Validates the dependencies in `/required-dependencies/required-dependencies/build.gradle`. |
GradleStylingCheck | Styling | Applies rules to enforce consisteny in code style. |
[GradleTaskCreationCheck](checks/gradle_task_creation_check.markdown#gradletaskcreationcheck) | Styling | Checks that a task is declared on a separate line before the closure. |
GradleTestDependencyVersionCheck | Bug Prevention | Checks the version for dependencies in gradle build files. |
WhitespaceCheck | Styling | Finds missing and unnecessary whitespace. |