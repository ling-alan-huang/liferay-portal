## GradleUnusedProjectDependenciesCheck

A `project(":...")` dependency in the `compileOnly`, `testCompileOnly`, `testIntegrationCompileOnly`, or `testIntegrationImplementation` configuration of a `build.gradle` file must be used by the module that declares it.

An unused project dependency adds the provider to the module's build order and compile classpath, so every change to the provider recompiles the module for nothing. Most of these lines are dead on the day they are added, copied from a neighboring module or added ahead of a use that never comes.

The check reads the module's source files and the provider's source files from the portal tree. A dependency is used when any of the following applies in the source sets the configuration feeds (`compileOnly` feeds all of them, `testCompileOnly` feeds `src/test`, and the `testIntegration*` configurations feed `src/testIntegration` only):

- A Java, Groovy, Kotlin, or JSP file imports or names a class of the provider, uses a class of its own package that the provider declares, or declares a package the provider exports.
- A JSP file declares a taglib URI that a TLD of the provider defines, or uses a tag whose class, a supertype of that class, or a class referenced by them belongs to the provider.
- The module's `bnd.bnd` names the provider in `-includeresource`, `Include-Resource`, `Bundle-ClassPath`, or `-classpath`; lists one of its packages in a package or import instruction; names it as `Fragment-Host`; or names one of its classes in any other instruction.
- A supertype of a referenced class belongs to the provider.
- `javac` may need a class of the provider to compile the module: a parameter, return, or exception type of a method or constructor the module calls, overrides, or inherits from an abstract supertype, or of an overloaded method of a generic superclass.
- A string literal or a resource file of the module names a class of the provider.

Modules that generate Java sources at build time (`buildWSDL`, `buildXSD`) and configurations the `build.gradle` file reads as files (`configurations.<name>`) are skipped. External dependencies, `compileInclude`, `testImplementation`, and `testRuntimeOnly` are out of scope. Unit tests run on a flat classpath that also holds the main compile classpath, so a `testImplementation` line can be needed only at test runtime, which no source reference shows. Integration tests run inside OSGi, so the `testIntegration*` configurations are compile needs only.

When a line is reported although `javac` needs the provider in a way the check cannot see, add a suppression to the module's `source-formatter-suppressions.xml`.

### Example

Incorrect:

```gradle
dependencies {
	compileOnly project(":apps:blogs:blogs-api")
	compileOnly project(":core:petra:petra-lang")
}
```

The module never references `com.liferay.petra.lang`.

Correct:

```gradle
dependencies {
	compileOnly project(":apps:blogs:blogs-api")
}
```