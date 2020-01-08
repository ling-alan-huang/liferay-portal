# Liferay Gradle Plugins Maven Plugin Builder Change Log

## 1.0.2 - 2015-07-27

### Dependencies
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.14.
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.13.
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.12.
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.11.
- [LPS-51081]: Update the qdox dependency to version 1.12.1.

## 1.0.3 - 2015-11-16

### Dependencies
- [LPS-58467]: Update the com.liferay.gradle.util dependency to version 1.0.19.
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.18.
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.17.
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.16.
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.15.

## 1.0.4 - 2015-12-31

### Dependencies
- [LPS-61672]: Update the com.liferay.gradle.util dependency to version 1.0.23.

## 1.0.11 - 2016-06-16

### Commits
- [LPS-63943]: Replace only fixed versions (1e1c97c315)

### Dependencies
- [LPS-65749]: Update the com.liferay.gradle.util dependency to version 1.0.26.
- [LPS-65086]: Update the com.liferay.gradle.util dependency to version 1.0.25.

## 1.0.12 - 2016-09-02

### Commits
- [LPS-67658]: Need "compileOnly" to keep dependencies out of "compile"
(4a3cd0bc9d)

### Description
- [LPS-67986]: The fully qualified class names in the generated Maven plugin
descriptors are now delimited by dots instead of dollar signs (e.g.,
`java.io.File` instead of `java$io$File`).

## 1.1.0 - 2017-03-08

### Description
- [LPS-71087]: Add task `writeMavenSettings` to configure the Maven invocations
with proxy settings and a mirror URL.
- [LPS-67573]: Make most methods private in order to reduce API surface.
- [LPS-67573]: Move task classes to the
`com.liferay.gradle.plugins.maven.plugin.builder.tasks` package.
- [LPS-67573]: Move utility classes to the
`com.liferay.gradle.plugins.maven.plugin.builder.internal` package.

## 1.1.1 - 2017-03-17

### Description
- [LPS-71264]: Add the ability to configure the Maven invocations with a local
repository. By default, the value is copied from the `maven.repo.local` system
property.

## 1.1.2 - 2017-03-21

### Description
- [LPS-71264]: Avoid throwing a `NullPointerException` if the
`WriteMavenSettingsTask` instance's `localRepositoryDir` property is a closure
that returns `null`.

## 1.1.3 - 2018-03-08

### Dependencies
- [LPS-73584]: Update the com.liferay.gradle.util dependency to version 1.0.29.
- [LPS-73584]: Update the com.liferay.gradle.util dependency to version 1.0.28.
- [LPS-72914]: Update the com.liferay.gradle.util dependency to version 1.0.27.

## 1.2.0 - 2018-03-08

### Description
- [LPS-71264]: Add the ability to attach a remote debugger to the Maven
invocation by setting the `BuildPluginDescriptorTask` instance's `mavenDebug`
property to `true`, or by passing the command line argument
`-DbuildPluginDescriptor.maven.debug=true`.
- [LPS-71264]: Synchronize the Gradle and Maven log levels.
- [LPS-71264]: Fix `pom.xml` generation in case project dependencies are
present.
- [LPS-71264]: Fix the `WriteMavenSettingsTask` instance's `localRepositoryDir`
property usage when running on Windows.

## 1.2.1 - 2018-08-06

### Commits
- [LPS-77425]: Partial revert of d25f48516a9ad080bcbd50e228979853d3f2dda5
(60d3a950d6)
- [LPS-77425]: Partial revert of b739c8fcdc5d1546bd642ca931476c71bbaef1fb
(02ca75b1da)
- [LPS-77425]: Auto SF (b739c8fcdc)
- [LPS-77425]: Increment all major versions (d25f48516a)

### Dependencies
- [LPS-77425]: Update the com.liferay.gradle.util dependency to version 1.0.29.
- [LPS-77425]: Update the qdox dependency to version 1.12.1.

### Description
- [LPS-84213]: Fix the `buildPluginDescriptor` task by updating the
`maven-plugin-plugin` version to `3.5.2`.

## 1.2.2 - 2018-11-16

### Dependencies
- [LPS-87466]: Update the com.liferay.gradle.util dependency to version 1.0.32.
- [LPS-84094]: Update the com.liferay.gradle.util dependency to version 1.0.31.
- [LPS-84094]: Update the com.liferay.gradle.util dependency to version 1.0.30.

## 1.2.3 - 2018-11-19

### Dependencies
- [LPS-87466]: Update the com.liferay.gradle.util dependency to version 1.0.33.

## 1.2.4 - 2019-06-21

### Dependencies
- [LPS-96247]: Update the com.liferay.gradle.util dependency to version 1.0.34.

## 1.2.5 - 2019-10-14

### Commits
- [LPS-101026]: Format xml after generating em (65a76bbba9)

### Dependencies
- [LPS-101026]: Update the dom4j dependency to version 2.0.0.