# Liferay Gradle Plugins CSS Builder Change Log

## 1.0.1 - 2015-07-13

### Dependencies
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.13.
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.12.
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.11.

## 1.0.3 - 2015-07-27

### Dependencies
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.14.

## 1.0.4 - 2015-08-07

### Dependencies
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.15.

## 1.0.5 - 2015-08-10

### Dependencies
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.16.

## 1.0.6 - 2015-08-13

### Dependencies
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.17.

## 1.0.7 - 2015-11-05

### Commits
- [LPS-58330]: The portal tool dependencies are only used to embed single *Args
classes, so they should be considered "provided" (da7c77ffbc)

### Dependencies
- [LPS-58467]: Update the com.liferay.gradle.util dependency to version 1.0.19.
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.18.

## 1.0.8 - 2015-11-13

### Commits
- [LPS-60234]: Remove dependency (ead6958605)

### Dependencies
- [LPS-60234]: Update the com.liferay.gradle.util dependency to version 1.0.23.

## 1.0.11 - 2016-04-30

### Commits
- [LPS-63943]: This is done automatically now (f1e42382d9)

### Dependencies
- [LPS-62570]: Update the com.liferay.css.builder dependency to version 1.0.17.
- [LPS-62570]: Update the com.liferay.css.builder dependency to version 1.0.16.
- [LPS-65086]: Update the com.liferay.gradle.util dependency to version 1.0.25.

## 1.0.13 - 2016-06-03

### Dependencies
- [LPS-66281]: Update the com.liferay.css.builder dependency to version 1.0.18.

## 1.0.14 - 2016-06-16

### Dependencies
- [LPS-65749]: Update the com.liferay.gradle.util dependency to version 1.0.26.

## 1.0.15 - 2016-09-13

### Commits
- [LPS-67658]: Need "compileOnly" to keep dependencies out of "compile"
(4a3cd0bc9d)

### Dependencies
- [LPS-67986]: Update the com.liferay.css.builder dependency to version 1.0.20.
- [LPS-67986]: Update the com.liferay.css.builder dependency to version 1.0.19.
- [LPS-67658]: Update the com.liferay.css.builder dependency to version 1.0.18.

## 2.0.0 - 2016-11-17

### Dependencies
- [LPS-69223]: Update the com.liferay.css.builder dependency to version 1.0.21.

### Description
- [LPS-67573]: Make most methods private in order to reduce API surface.
- [LPS-69223]: Update default value of the `precision` property for
`BuildCSSTask` from `5` to `9`.

## 2.0.1 - 2017-07-10

### Dependencies
- [LPS-73495]: Update the com.liferay.css.builder dependency to version 1.0.28.
- [LPS-73495]: Update the com.liferay.css.builder dependency to version 1.0.27.
- [LPS-72914]: Update the com.liferay.gradle.util dependency to version 1.0.27.
- [LPS-70890]: Update the com.liferay.css.builder dependency to version 1.0.26.
- [LPS-71331]: Update the com.liferay.css.builder dependency to version 1.0.25.
- [LPS-70890]: Update the com.liferay.css.builder dependency to version 1.0.24.
- [LPS-69706]: Update the com.liferay.css.builder dependency to version 1.0.23.
- [LPS-69706]: Update the com.liferay.css.builder dependency to version 1.0.22.

## 2.0.2 - 2017-08-12

### Dependencies
- [LPS-73584]: Update the com.liferay.gradle.util dependency to version 1.0.29.
- [LPS-73584]: Update the com.liferay.gradle.util dependency to version 1.0.28.

## 2.1.0 - 2017-08-12

### Dependencies
- [LPS-74126]: Update the com.liferay.css.builder dependency to version 1.1.0.

### Description
- [LPS-74126]: Add the `appendCssImportTimestamps` property to `BuildCSSTask`.

## 2.1.1 - 2017-08-15

### Dependencies
- [LPS-74126]: Update the com.liferay.css.builder dependency to version 1.1.1.

## 2.1.2 - 2017-09-18

### Dependencies
- [LPS-74315]: Update the com.liferay.css.builder dependency to version 1.1.2.

## 2.1.3 - 2017-09-19

### Dependencies
- [LPS-74789]: Update the com.liferay.css.builder dependency to version 1.1.3.

## 2.1.4 - 2017-10-06

### Dependencies
- [LPS-74426]: Update the com.liferay.css.builder dependency to version 1.1.4.

## 2.1.5 - 2017-10-11

### Dependencies
- [LPS-74449]: Update the com.liferay.css.builder dependency to version 2.0.0.

## 2.1.6 - 2017-11-01

### Dependencies
- [LPS-75589]: Update the com.liferay.css.builder dependency to version 2.0.1.

## 2.1.7 - 2017-11-07

### Dependencies
- [LPS-75633]: Update the com.liferay.css.builder dependency to version 2.0.2.

## 2.2.0 - 2017-12-19

### Dependencies
- [LPS-76475]: Update the com.liferay.css.builder dependency to version 2.1.0.

### Description
- [LPS-76475]: Replace the `BuildCSSTask`'s `docrootDir`, `portalCommonDir`,
`portalCommonFile`, and `portalCommonPath` properties with `baseDir`,
`importDir`, `importFile`, and `importPath`. The previous properties are still
available, but they are deprecated.
- [LPS-76475]: Fix invocation of the [Liferay CSS Builder] if the
`BuildCSSTask`'s `dirNames` property contains more than one value.

## 2.2.1 - 2018-08-02

### Commits
- [LPS-83755]: Update File Versions (c80a286058)
- [LPS-77425]: Partial revert of d25f48516a9ad080bcbd50e228979853d3f2dda5
(60d3a950d6)
- [LPS-77425]: Increment all major versions (d25f48516a)

### Dependencies
- [LPS-83755]: Update the com.liferay.css.builder dependency to version 2.1.1.
- [LPS-77425]: Update the com.liferay.gradle.util dependency to version 1.0.29.
- [LPS-77425]: Update the com.liferay.css.builder dependency to version 2.1.0.

## 2.2.2 - 2018-08-15

### Commits
- [LPS-84473]:  (6659d6f32b)

### Dependencies
- [LPS-84473]: Update the com.liferay.css.builder dependency to version 2.1.2.

## 2.2.3 - 2018-08-22

### Dependencies
- [LPS-84218]: Update the com.liferay.css.builder dependency to version 2.1.3.

## 2.2.4 - 2018-10-22

### Dependencies
- [LPS-84094]: Update the com.liferay.gradle.util dependency to version 1.0.31.
- [LPS-84094]: Update the com.liferay.gradle.util dependency to version 1.0.30.

## 2.2.5 - 2018-11-16

### Dependencies
- [LPS-87466]: Update the com.liferay.gradle.util dependency to version 1.0.32.

## 2.2.6 - 2018-11-19

### Dependencies
- [LPS-87466]: Update the com.liferay.gradle.util dependency to version 1.0.33.

## 2.2.7 - 2019-05-23

### Dependencies
- [LPS-94999]: Update the com.liferay.css.builder dependency to version 3.0.0.

## 3.0.1 - 2019-10-13

### Dependencies
- [LPS-103051]: Update the com.liferay.css.builder dependency to version 3.0.1.
- [LPS-96247]: Update the com.liferay.gradle.util dependency to version 1.0.34.

## 3.0.2 - 2019-10-31

### Dependencies
- [LPS-103169]: Update the com.liferay.css.builder dependency to version 3.0.2.