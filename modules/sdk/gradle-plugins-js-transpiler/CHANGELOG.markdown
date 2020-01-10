# Liferay Gradle Plugins JS Transpiler Change Log

## 1.0.1 - 2015-07-23

### Commits
- [LPS-51081]: Remove quotes from Node module versions (24c7233ee0)

### Dependencies
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.14.

## 1.0.3 - 2015-07-31

### Commits
- [LPS-51081]: Porting
liferay/liferay-plugins@6f66d880e7c1bd3ed54d7dc7ecc1a5180082909f from Ant to
Gradle (40a33d5c1f)
- [LPS-51081]: Porting
liferay/liferay-plugins@8887bc074ce7fbbc0d1e1115312c22d099e28d4e from Ant to
Gradle (2bc0205a18)

### Dependencies
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.15.

## 1.0.4 - 2015-08-04

### Commits
- [LPS-51081]: Porting
liferay/liferay-plugins@43e020fd91046b8cf928c26a68423aa52072287f from Ant to
Gradle (ce450553fd)

## 1.0.5 - 2015-08-18

### Commits
- [LPS-51081]: Configure the output dir at the end of the configuration phase,
because in the meantime the sourceSet dirs may have been changed (14c91d852d)

### Dependencies
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.17.
- [LPS-51081]: Update the com.liferay.gradle.util dependency to version 1.0.16.

## 1.0.6 - 2015-08-25

### Commits
- [LPS-51081]: Update "gradle-plugins-js-transpiler" to use "gradle-plugins-node"
(7c701ce12e)
- [LPS-51081]: Ivy cache (3148254a3c)

### Dependencies
- [LPS-51081]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.0.

## 1.0.7 - 2015-08-31

### Commits
- [LPS-51081]: Remove modules' Eclipse project files (b3f19f9012)
- [LPS-51081]: Replace modules' Ant files with Gradle alternatives (9e60160a85)
- [LPS-51081]: Remove modules' Ivy files (076b384eef)

### Dependencies
- [LPS-58260]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.1.

## 1.0.8 - 2015-09-08

### Dependencies
- [LPS-58467]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.2.

## 1.0.9 - 2015-09-09

### Dependencies
- [LPS-58467]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.3.

## 1.0.10 - 2015-09-14

### Dependencies
- [LPS-58609]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.4.

## 1.0.11 - 2015-09-15

### Dependencies
- [LPS-58655]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.5.

## 1.0.12 - 2015-09-17

### Dependencies
- [LPS-58655]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.6.

## 1.0.13 - 2015-09-23

### Commits
- [LPS-57645]: Add argument to change the Babel script file location
(2c27b96869)
- [LPS-57645]: Add option to enable the Source Maps generation (330754fc7c)
- [LPS-57645]: Add option to set the Babel stage (48c0bee5e7)
- [LPS-57645]: Update Babel version (802813f150)

## 1.0.14 - 2015-11-03

### Commits
- [LPS-60148]: Completely disable "transpileJS" if there is nothing to do
(2b1676dd63)
- [LPS-59564]: Update directory layout for "sdk" modules (ea19635556)

## 1.0.16 - 2015-12-21

### Commits
- [LPS-61527]: Update sample (d0ca737e83)
- [LPS-61527]: Configure the task when the "java" plugin is applied (24962481b6)
- [LPS-61527]: Add default source files filter (c8fedc848e)
- [LPS-60243]: SF (6d516dbaa9)
- [LPS-60317]: Remove (d987ce5aa0)

### Dependencies
- [LPS-61527]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.9.

## 1.0.17 - 2016-01-12

### Dependencies
- [LPS-61754]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.10.

## 1.0.18 - 2016-01-26

### Commits
- [LPS-62504]: Make workingDir read-only (373e7df643)
- [LPS-62504]: Reuse new base task class (e8ad590cf7)
- [LPS-61088]: Remove classes and resources dir from Include-Resource
(1b0e1275bc)

### Dependencies
- [LPS-62504]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.11.

## 1.0.19 - 2016-01-26

### Commits
- [LPS-61848]: An empty settings.gradle is enough (2e5eb90e23)
- [LPS-61848]: Update to metal-cli 0.3.0 (7b49ad6000)
- [LPS-61848]: Move in the default value assignment (a511db4a29)
- [LPS-61848]: Sort (e2f1c22bc7)
- [LPS-61848]: SF (7740c27700)
- [LPS-61848]: Use metal-cli instead of Babel as js build tool (0a82ea3626)

## 1.0.20 - 2016-01-28

### Dependencies
- [LPS-62671]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.12.

## 1.0.21 - 2016-02-03

### Commits
- [LPS-62826]: Implement interfaces for clarity (a2be52c3d6)

## 1.0.22 - 2016-03-18

### Commits
- [LPS-64281]: Sort (70994cec57)
- [LPS-64281]: Fix include methods (04b1e61e17)
- [LPS-64281]: Not needed, sourceFiles is the real input (ed42082e7b)
- [LPS-64281]: Copy from sourceDir to workingDir, then run metal-cli
(ea2b0a8a7d)
- [LPS-64281]: Use sets to avoid duplicates (7a308874ef)
- [LPS-64281]: This is already the default value (1003266358)
- [LPS-64281]: SF, I don't think the ternary operator is allowed (a3d0d98eb9)
- [LPS-64281]: SF (a136dee450)
- [LPS-64281]: Allow soySkipMetalGeneration to be configured (df3eb128a6)
- [LPS-64281]: Use globs for src and soySrc (86da5728b2)
- [LPS-64281]: Allow bundleFileName to be configured (fb0881bb33)
- [LPS-64281]: Allow globalName to be configured (e3e9ff5683)
- [LPS-64281]: Allow moduleName to be configured (acc7b3f211)
- [LPS-64281]: SourceMaps are enabled by default (29423be08a)
- [LPS-64281]: Stage attribute is no longer valid neither in babel nor in
metal-cli (261aa5a6cb)
- [LPS-64281]: Update metal-cli version to 0.5.3 (920a44b14f)
- [LPS-63943]: This is done automatically now (f1e42382d9)
- [LPS-62883]: Update gradle-plugins/build.gradle (20fc2457e6)

## 1.0.23 - 2016-03-21

### Commits
- [LPS-64407]: SF (5d3498ec4f)
- [LPS-64407]: Handle soy files as well (55bbe2d1ca)

## 1.0.24 - 2016-04-11

### Commits
- [LPS-64875]: Update metal-cli version (f6295e9e8a)
- [LPS-61099]: Delete build.xml in modules (c9a7e1d370)

## 1.0.25 - 2016-04-11

### Commits
- [LPS-64875]: Remove unnecessary loops (7f3c4be0c8)
- [LPS-64875]: Make Soy dependencies configurable (664bf9b832)
- [LPS-64875]: Configure soyDeps for metal-cli (498703b4ea)

## 1.0.26 - 2016-04-21

### Commits
- [LPS-65245]: Configuration of "transpileJS" depends on download tasks
(a366512ea8)
- [LPS-61420]: Auto SF (2d3fe01dfa)

### Dependencies
- [LPS-65245]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.13.

## 1.0.27 - 2016-06-07

### Commits
- [LPS-66410]: Fix method signature (e63f77f155)
- [LPS-61420]: Auto SF (2488715278)
- [LPS-64816]: Update Gradle plugin samples (3331002e5d)

### Dependencies
- [LPS-66410]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.14.

## 1.0.28 - 2016-06-09

### Dependencies
- [LPS-66410]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.15.

## 1.0.29 - 2016-07-01

### Commits
- [LPS-65976]: Keep tasks disabled if they already are (ff4bd61794)

### Dependencies
- [LPS-65749]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.16.

## 1.0.30 - 2016-08-01

### Dependencies
- [LPS-66906]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.17.

## 1.0.31 - 2016-08-05

### Dependencies
- [LPS-66906]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.18.

## 1.0.32 - 2016-08-09

### Dependencies
- [LPS-66906]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.19.

## 1.0.33 - 2016-08-11

### Dependencies
- [LPS-67544]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.20.

## 1.0.34 - 2016-08-15

### Dependencies
- [LPS-66906]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.21.

## 1.0.35 - 2016-08-27

### Commits
- [LPS-67023]: Add changelogs for Node plugins (85cea37250)
- [LPS-67023]: Move metal-cli default version to the download task (19b70b7d3e)
- [LPS-67023]: No need to download "lfr-amd-loader" anymore (dc578a2a37)
- [LPS-67658]: Convert gradle-plugins-js-transpiler sample into a smoke test
(2e68af4a68)
- [LPS-67658]: Configure GradleTest in gradle-plugins-js-transpiler (a9016420d1)
- [LPS-67658]: Need "compileOnly" to keep dependencies out of "compile"
(4a3cd0bc9d)
- [LPS-67658]: These plugins must work with Gradle 2.5+ (5b963e363d)

### Dependencies
- [LPS-67023]: Update the com.liferay.gradle.plugins.node dependency to version
1.0.22.

### Description
- [LPS-67023]: Remove the `downloadLfrAmdLoader` task.
- [LPS-67023]: Remove the `jsTranspiler` extension object.

## 1.0.36 - 2016-09-20

### Commits
- [LPS-67653]: Semver gradle-plugins-js-transpiler (9c90a0cd86)
- [LPS-66906]: Update changelog (40fb9dd977)
- [LPS-67573]: Make methods private to reduce API surface (0bf4bdb787)

## 2.0.0 - 2016-09-20

### Dependencies
- [LPS-67653]: Update the com.liferay.gradle.plugins.node dependency to version
1.1.0.

### Description
- [LPS-67573]: Make most methods private in order to reduce API surface.

## 2.0.1 - 2016-10-06

### Commits
- [LPS-68564]: Update changelog (2bcd5d5680)
- [LPS-66709]: Fix changelog dates (9f84cdb45f)
- [LPS-68231]: Test plugins with Gradle 3.1 (49ec4cdbd8)

### Dependencies
- [LPS-68564]: Update the com.liferay.gradle.plugins.node dependency to version
1.2.0.

## 2.0.2 - 2016-10-21

### Commits
- [LPS-66906]: Update changelog (455666bb31)
- [LPS-66709]: Wordsmithing READMEs for consistency (a3cc8c4c6b)
- [LPS-66709]: Edit gradle-plugins-js-transpiler README (68e82bb85c)
- [LPS-66709]: Add README for gradle-plugins-js-transpiler (d90cfaffcb)

### Dependencies
- [LPS-66906]: Update the com.liferay.gradle.plugins.node dependency to version
1.3.0.

## 2.0.3 - 2016-10-24

### Commits
- [LPS-68917]: Semver gradle-plugins-js-transpiler (3b90a12352)
- [LPS-68917]: Add "soyCompile" configuration for additional Soy dependencies
(f07fa7bafc)
- [LPS-68917]: Add default Lexicon Soy dependency (d15c4fa8a3)

## 2.1.0 - 2016-10-24

### Commits
- [LPS-68917]: Update readme (ebaac1c800)
- [LPS-68917]: Update changelog (73839ec763)

### Description
- [LPS-68917]: Add configuration `soyCompile` to provide additional Soy
dependencies for the `transpileJS` task.
- [LPS-68917]: Add default Lexicon Soy dependency to all `TranspileJSTask`
instances.

## 2.1.1 - 2016-10-26

### Commits
- [LPS-68917]: Update changelog (a490c37718)
- [LPS-68917]: Fix search pattern for Soy dependencies in "soyCompile"
(c776b50e0f)

### Description
- [LPS-68917]: Fixed search pattern for the additional Soy dependencies in the
`soyCompile` configuration.

## 2.1.2 - 2016-10-28

### Commits
- [LPS-68979]: Semver gradle-plugins-js-transpiler (e462d28c96)
- [LPS-68979]: Add option to keep a TranspileJSTask enabled with no files
(f41fcb4961)
- [LPS-68979]: Exclude empty directories while copying (730f4e19e7)

## 2.2.0 - 2016-10-28

### Commits
- [LPS-68979]: Update readme (0e84b83fb1)
- [LPS-68979]: Update changelog (3bdca0562b)

### Description
- [LPS-68979]: Add property `skipWhenEmpty` to all tasks that extend
`TranspileJSTask`. If `true`, the task is disabled if it has no source files
at the end of the project evaluation.
- [LPS-68979]: Exclude empty directories while `TranspileJSTask` instances copy
source files to `workingDir`.

## 2.2.1 - 2016-11-01

### Commits
- [LPS-69026]: Update changelog (53f66f31f3)
- [LPS-69026]: Set the "--logLevel" argument based on the Gradle log level
(766dd0805c)

### Description
- [LPS-69026]: Set the `--logLevel` argument of `metal-cli` based on the Gradle
log level.

## 2.2.2 - 2016-11-21

### Commits
- [LPS-69248]: Semver gradle-plugins-js-transpiler (ef490c6241)
- [LPS-69248]: Fix Gradle test (523c77e743)
- [LPS-69248]: Update Gradle smoke test (c7c5064a00)
- [LPS-69248]: Add "jsCompile" configuration for additional JS dependencies
(c3bd6aea32)
- [LPS-69259]: Test plugins with Gradle 3.2 (dec6105d3d)

## 2.3.0 - 2016-11-21

### Commits
- [LPS-69248]: Update changelog (db56e069e1)
- [LPS-69248]: Update readme (e6e0c31976)

### Description
- [LPS-69248]: Add the `jsCompile` configuration to provide additional
JavaScript dependencies for the `transpileJS` task.

## 2.3.1 - 2016-11-29

### Commits
- [LPS-69445]: Update changelog (45fc2e0dac)
- [LPS-69248]: Fix changelog (8e26853daa)
- [LPS-69259]: Test plugins with Gradle 3.2.1 (72873ed836)
- [LPS-69288]: Edit Change Logs (dbf1bdfe1c)

### Dependencies
- [LPS-69445]: Update the com.liferay.gradle.plugins.node dependency to version
1.4.0.

## 2.3.2 - 2016-12-08

### Commits
- [LPS-69618]: Update changelog (cbcfd1a31e)
- [LPS-66709]: README typo (283446e516)
- [LPS-66709]: Add supported Gradle versions in READMEs (e0d9458520)

### Dependencies
- [LPS-69618]: Update the com.liferay.gradle.plugins.node dependency to version
1.4.1.

## 2.3.3 - 2016-12-14

### Commits
- [LPS-69677]: Update changelog (60368c89e0)

### Dependencies
- [LPS-69677]: Update the com.liferay.gradle.plugins.node dependency to version
1.4.2.

## 2.3.4 - 2016-12-21

### Commits
- [LPS-69802]: Update changelog (1809dce37d)

### Dependencies
- [LPS-69802]: Update the com.liferay.gradle.plugins.node dependency to version
1.5.0.

## 2.3.5 - 2016-12-29

### Commits
- [LPS-69802]: Fix changelog (649e7436ec)

### Dependencies
- [LPS-69920]: Update the com.liferay.gradle.plugins.node dependency to version
1.5.1.

## 2.3.6 - 2017-02-09

### Commits
- [LPS-69920]: Update changelog (ee113aec42)
- [LPS-70060]: Test plugins with Gradle 3.3 (09bed59a42)
- [LPS-69920]: Update changelog (a96c97f1d7)

### Dependencies
- [LPS-69920]: Update the com.liferay.gradle.plugins.node dependency to version
1.5.2.

## 2.3.7 - 2017-02-23

### Commits
- [LPS-70870]: Update changelog (ca2986fa8d)
- [LPS-70677]: No need to look into the local Maven repository during testing
(452be84220)
- [LPS-69920]: Fix changelog (c0bf4fe101)

### Dependencies
- [LPS-70819]: Update the com.liferay.gradle.plugins.node dependency to version
2.0.0.

## 2.3.8 - 2017-03-09

### Commits
- [LPS-70634]: Update changelog (1d0008d19b)
- [LPS-66709]: Update supported Gradle versions in READMEs (06e315582b)
- [LPS-67573]: Enable semantic versioning check on CI (63d7f4993f)
- [LPS-70870]: Fix changelog (5387c5447d)

### Dependencies
- [LPS-70634]: Update the com.liferay.gradle.plugins.node dependency to version
2.0.1.

## 2.3.9 - 2017-03-13

### Commits
- [LPS-71222]: Update changelog (6dd8440d55)
- [LPS-70634]: Fix changelog (4c204b4265)

### Dependencies
- [LPS-71222]: Update the com.liferay.gradle.plugins.node dependency to version
2.0.2.

## 2.3.10 - 2017-04-11

### Dependencies
- [LPS-71826]: Update the com.liferay.gradle.plugins.node dependency to version
2.1.0.

## 2.3.11 - 2017-04-25

### Commits
- [LPS-71826]: Update changelog (ebe6cdda61)

### Dependencies
- [LPS-72152]: Update the com.liferay.gradle.plugins.node dependency to version
2.2.0.

## 2.3.12 - 2017-05-03

### Commits
- [LPS-72340]: Update changelog (58b70ad19a)
- [LPS-72152]: Fix changelog (9c7a14af00)
- [LPS-72152]: Update changelog (75cc9efbbe)

### Dependencies
- [LPS-72340]: Update the com.liferay.gradle.plugins.node dependency to version
2.2.1.

## 2.3.13 - 2017-05-23

### Commits
- [LPS-72723]: Update changelog (d5bca19afb)
- [LPS-72723]: Avoid "npmInstall" to remove dirs created from "jsCompile"
(dfa6f3011a)

### Description
- [LPS-72723]: Avoid the `npmInstall` task from deleting the `node_modules`
subdirectories created from the dependencies in the `jsCompile` configuration.

## 2.3.14 - 2017-05-31

### Commits
- [LPS-72851]: Update changelog (be41f7d6e7)
- [LPS-72851]: Bypass Gradle bug and force project dependencies build
(84541aca4d)

### Description
- [LPS-72851]: Fix `InvalidUserDataException` in parallel builds when the
`jsCompile` or `soyCompile` configurations include project dependencies.

## 2.3.15 - 2017-07-07

### Dependencies
- [LPS-73472]: Update the com.liferay.gradle.plugins.node dependency to version
2.3.0.

## 2.3.16 - 2017-07-17

### Commits
- [LPS-73472]: Update changelog (d853d7415f)

### Dependencies
- [LPS-73472]: Update the com.liferay.gradle.plugins.node dependency to version
3.0.0.

## 2.3.17 - 2017-08-24

### Commits
- [LPS-74343]: Semver gradle-plugins-js-transpiler (bf366fa7c3)
- [LPS-74343]: Make "sourceDir" property mandatory in TranspileJSTask
(bddb2229ed)
- [LPS-73472]: Update changelog (46c603a6e4)

### Description
- [LPS-74343]: Explicitly set the `TranspileJSTask`'s `sourceDir` property as
required.

## 2.3.18 - 2017-08-29

### Commits
- [LPS-71285]: Edit Changelogs (8d092ac0cb)
- [LPS-74343]: Update changelog (c43d4bfef7)

### Dependencies
- [LPS-73070]: Update the com.liferay.gradle.plugins.node dependency to version
3.1.0.

## 2.3.19 - 2017-09-18

### Commits
- [LPS-73472]: Update changelog (3e3cbd6be6)
- [LPS-73070]: Update changelog (718c7a329b)

### Dependencies
- [LPS-74770]: Update the com.liferay.gradle.plugins.node dependency to version
3.1.1.

## 2.3.20 - 2017-09-28

### Commits
- [LPS-74933]: Update changelog (9ba2a105aa)
- [LPS-74770]: Update changelog (a103f673aa)

### Dependencies
- [LPS-74933]: Update the com.liferay.gradle.plugins.node dependency to version
3.2.0.

## 2.3.21 - 2017-10-10

### Commits
- [LPS-75175]: Update changelog (4c955d1c1c)
- [LPS-74933]: Fix changelog (0f7059acca)

### Dependencies
- [LPS-75175]: Update the com.liferay.gradle.plugins.node dependency to version
3.2.1.

## 2.3.22 - 2017-11-13

### Commits
- [LPS-75829]: Semver gradle-plugins-js-transpiler (8e5f1ad2ea)
- [LPS-75829]: Extract duplicate methods to new util class (2859605b38)
- [LPS-75829]: Reuse new base plugin (012f4fde3d)
- [LPS-75829]: Copy "jsCompile" logic to new plugin (847aa53a29)

## 2.4.0 - 2017-11-13

### Commits
- [LPS-75829]: Update readme (2d2c00cf6a)
- [LPS-75829]: Add Gradle test (4ecb59a971)
- [LPS-75829]: Expand "jsCompile" dependencies before running any NPM script
(02bbf12ffc)

### Description
- [LPS-75829]: Add the new `com.liferay.js.transpiler.base` plugin to apply the
`jsCompile` configuration expansion logic.

## 2.4.1 - 2017-11-20

### Commits
- [LPS-74526]: Wordsmithing (85dc494e0b)
- [LPS-74526]: Edit Changelogs (705b732972)
- [LPS-75829]: Edit JS Transpiler Gradle Readme (2259829096)
- [LPS-75829]: Update changelog (dd9b631e1d)

### Dependencies
- [LPS-75965]: Update the com.liferay.gradle.plugins.node dependency to version
4.0.0.

## 2.4.2 - 2018-01-02

### Commits
- [LPS-76644]: Add description to Gradle plugins (5cb7b30e6f)
- [LPS-74544]: Auto SF (493f0d529b)
- [LPS-75965]: Update changelog (efeb8afb13)

### Dependencies
- [LPS-74904]: Update the com.liferay.gradle.plugins.node dependency to version
4.0.1.

## 2.4.3 - 2018-01-17

### Commits
- [LPS-76644]: Fix plugin publishing configuration (849d7c0408)
- [LPS-76644]: Update changelog (6688090fdc)
- [LPS-77250]: Update changelog (eafed42438)
- [LPS-77250]: Update readme (5f5209dc37)
- [LPS-77250]: Renames lexicon soy dependency to clay (134789a1f1)
- [LPS-76644]: Enable Gradle plugins publishing (8bfdfd53d7)
- [LPS-74904]: Update changelog (f5d0e0e1f5)

### Dependencies
- [LPS-76644]: Update the com.liferay.gradle.plugins.node dependency to version
4.0.2.

### Description
- [LPS-77250]: Update the default value of the `soyDependencies` for
`TranspileJSTask` instances from
`"${npmInstall.workingDir}/node_modules/lexicon*/src/**/*.soy"` to
`"${npmInstall.workingDir}/node_modules/clay*/src/**/*.soy"`.

## 2.4.4 - 2018-02-08

### Commits
- [LPS-69802]: Update changelog (43fbca7384)
- [LRDOCS-4129]: Fix Gradle plugin README links (4592b9f829)
- [LRDOCS-4319]: Update Gradle plugin README intro descriptions for consistency
(72104bde58)
- [LRDOCS-4319]: Update Gradle plugin BND descriptions for consistency
(e1495e8e8d)

### Dependencies
- [LPS-69802]: Update the com.liferay.gradle.plugins.node dependency to version
4.1.0.

## 2.4.5 - 2018-02-13

### Commits
- [LPS-77996]: Fix changelog (f6fa73d808)
- [LPS-69802]: Remove duplicated tickets (0b478820fb)
- [LPS-69802]: Fix changelog (c418710063)

### Dependencies
- [LPS-77996]: Update the com.liferay.gradle.plugins.node dependency to version
4.2.0.

## 2.4.6 - 2018-03-15

### Commits
- [LPS-78741]: Fix changelog (9eda473f9a)
- [LPS-77425]: Partial revert of d25f48516a9ad080bcbd50e228979853d3f2dda5
(60d3a950d6)
- [LPS-77425]: Increment all major versions (d25f48516a)

### Dependencies
- [LPS-78741]: Update the com.liferay.gradle.plugins.node dependency to version
4.3.0.
- [LPS-77425]: Update the com.liferay.gradle.plugins.node dependency to version
4.2.0.

## 2.4.7 - 2018-03-22

### Commits
- [LPS-78741]: Fix changelogs (d70ec7bc15)

### Dependencies
- [LPS-78741]: Update the com.liferay.gradle.plugins.node dependency to version
4.3.1.

## 2.4.8 - 2018-03-30

### Dependencies
- [LPS-78741]: Update the com.liferay.gradle.plugins.node dependency to version
4.3.2.

## 2.4.9 - 2018-04-05

### Commits
- [LPS-78741]: Update changelog (68557c5fbe)
- [LPS-78741]: Fix changelog (718691496c)

### Dependencies
- [LPS-78741]: Update the com.liferay.gradle.plugins.node dependency to version
4.3.3.

## 2.4.10 - 2018-05-07

### Commits
- [LPS-75530]: Update changelog (cc642b7591)
- [LPS-78741]: Fix changelog (f0ca5baeff)

### Dependencies
- [LPS-75530]: Update the com.liferay.gradle.plugins.node dependency to version
4.3.4.

## 2.4.11 - 2018-06-08

### Commits
- [LPS-82130]: Update changelog (4ee17d888e)

### Dependencies
- [LPS-82310]: Update the com.liferay.gradle.plugins.node dependency to version
4.3.5.

## 2.4.12 - 2018-06-22

### Commits
- [LPS-77250]: Update readme with default values for 'TranspileJSTask'
(e20db78a8d)
- []: Revert "LPS-77875 Auto SF" (82e5e335e9)
- [LPS-77875]: Auto SF (57de739400)
- [LPS-77875]: Auto SF (8a7421985a)

### Dependencies
- [LPS-82568]: Update the com.liferay.gradle.plugins.node dependency to version
4.4.0.

## 2.4.13 - 2018-10-03

### Commits
- [LPS-71117]: Test plugins with Gradle up to 3.5.1 (c3e12d1cf3)
- [LPS-71117]: Update supported Gradle versions in READMEs (fdcc16c0d4)
- []: Revert "LPS-82828 Deprecated as of 7.1.0" (470150b661)
- []: Revert "LPS-82828 Auto SF" (a9b34aabb0)
- []: Revert "LPS-74544 Auto SF" (3fd95d3696)
- [LPS-74544]: Auto SF (98cf49a673)
- [LPS-82828]: Auto SF (53037b261c)
- [LPS-82828]: Deprecated as of 7.1.0 (69573bff7e)
- [LPS-82568]: Update changelog (b33e660338)

### Dependencies
- [LPS-85959]: Update the com.liferay.gradle.plugins.node dependency to version
4.4.1.

## 2.4.14 - 2018-10-09

### Commits
- [LPS-85959]: Update changelog (2945b5ba97)

### Dependencies
- [LPS-85959]: Update the com.liferay.gradle.plugins.node dependency to version
4.4.2.

## 2.4.15 - 2018-10-22

### Commits
- [LPS-85959]: Update changelog (302bb2aba9)

### Dependencies
- [LPS-86576]: Update the com.liferay.gradle.plugins.node dependency to version
4.4.3.

## 2.4.16 - 2018-11-16

### Commits
- [LPS-87192]: Set the Eclipse task property gradleVersion (040b2abdee)
- [LPS-87192]: Add variable gradleVersion (no logic changes) (2f7c0b2fe4)
- [LPS-85609]: Fix for CI (test only 4.10.2) (4eed005731)
- [LPS-85609]: Test plugins up to Gradle 4.10.2 (60905bc960)
- [LPS-85609]: Update supported Gradle versions (d79b89682b)
- [LPS-86589]: Fix gradle tests (9acc287650)
- [LPS-86576]: Update changelog (8e35daa7ff)
- [LPS-86589]: Fix gradle test (f7b3173c49)
- [LPS-86589]: Update readme (4280a3d596)
- [LPS-86589]: Test Gradle plugins from Gradle 2.14.1 to 3.5.1 (6df521a506)

### Dependencies
- [LPS-87465]: Update the com.liferay.gradle.plugins.node dependency to version
4.5.0.
- [LPS-87466]: Update the com.liferay.gradle.plugins.node dependency to version
4.4.4.

## 2.4.17 - 2019-01-07

### Commits
- [LPS-85609]: Update readme (c182ff396d)
- [LPS-85609]: Simplify gradleTest (a8b0feff31)
- [LPS-87465]: Update changelog (56dd62b4ae)
- [LPS-85609]: Use Gradle 4.10.2 (9aa90f8961)

### Dependencies
- [LPS-87479]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.0.
- [LPS-87466]: Update the com.liferay.gradle.plugins.node dependency to version
4.5.1.

## 2.4.18 - 2019-01-09

### Dependencies
- [LPS-88909]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.1.

## 2.4.19 - 2019-01-09

### Dependencies
- [LPS-87479]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.2.

## 2.4.20 - 2019-01-14

### Commits
- [LPS-87479]: Update changelog (1168dac565)
- [LPS-88909]: Update changelog (9716de3954)
- [LPS-87479]: Update changelog (6b9b7f6b36)

### Dependencies
- [LPS-89126]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.3.

## 2.4.21 - 2019-01-16

### Commits
- [LPS-89126]: Update changelog (9cc7c1a98b)

### Dependencies
- [LPS-88909]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.4.

## 2.4.22 - 2019-01-24

### Commits
- [LPS-88909]: Update changelog (8d610504e2)

### Dependencies
- [LPS-89436]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.5.

## 2.4.23 - 2019-02-04

### Dependencies
- [LPS-89916]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.6.

## 2.4.24 - 2019-02-20

### Dependencies
- [LPS-90945]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.7.

## 2.4.25 - 2019-03-20

### Dependencies
- [LPS-91967]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.8.

## 2.4.26 - 2019-04-03

### Dependencies
- [LPS-93258]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.9.

## 2.4.27 - 2019-04-10

### Dependencies
- [LRDOCS-6412]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.10.

## 2.4.28 - 2019-04-11

### Dependencies
- [LPS-91967]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.11.

## 2.4.29 - 2019-04-25

### Dependencies
- [LPS-77425]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.12.

## 2.4.30 - 2019-05-01

### Dependencies
- [LPS-91967]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.13.

## 2.4.31 - 2019-05-06

### Commits
- [LPS-89369]: Update changelog (448d0758e1)

### Dependencies
- [LPS-91967]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.14.

## 2.4.32 - 2019-05-06

### Dependencies
- [LPS-94947]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.15.

## 2.4.33 - 2019-05-24

### Dependencies
- [LPS-88909]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.16.

## 2.4.35 - 2019-06-10

### Commits
- [LPS-96376]: Update to liferay-npm-scripts v2.1.0 (prettier) (7930ab3625)
- [LPS-0]: SF. Space character correction for build scripts & READMEs
(9dd5d12c9a)

### Dependencies
- [LPS-93220]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.17.

## 2.4.36 - 2019-06-21

### Commits
- [LPS-0]: Fix gradleTest (9ba9be80a8)

### Dependencies
- [LPS-96247]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.18.

## 2.4.37 - 2019-08-13

### Dependencies
- [LPS-99740]: Update the com.liferay.gradle.plugins.node dependency to version
4.6.19.

## 2.4.38 - 2019-08-14

### Dependencies
- [LPS-99774]: Update the com.liferay.gradle.plugins.node dependency to version
4.7.0.

## 2.4.39 - 2019-08-19

### Commits
- [LPS-99977]: Update Gradle plugins (f125baba8a)

### Dependencies
- [LPS-99977]: Update the com.liferay.gradle.plugins.node dependency to version
4.7.1.

## 2.4.40 - 2019-08-21

### Dependencies
- [LPS-100168]: Update the com.liferay.gradle.plugins.node dependency to version
4.8.0.

## 2.4.41 - 2019-08-24

### Commits
- [LPS-100168]: Fix gradleTest (55ca3f97c1)

### Dependencies
- [LPS-100168]: Update the com.liferay.gradle.plugins.node dependency to version
4.9.0.

## 2.4.42 - 2019-08-28

### Dependencies
- [LPS-100163]: Update the com.liferay.gradle.plugins.node dependency to version
5.0.0.

## 2.4.43 - 2019-09-16

### Dependencies
- [LRQA-52072]: Update the com.liferay.gradle.plugins.node dependency to version
5.1.0.

## 2.4.44 - 2019-09-18

### Dependencies
- [LPS-101470]: Update the com.liferay.gradle.plugins.node dependency to version
5.1.1.

## 2.4.45 - 2019-09-19

### Dependencies
- [LPS-101470]: Update the com.liferay.gradle.plugins.node dependency to version
5.1.2.

## 2.4.46 - 2019-10-16

### Commits
- [LPS-102367]: Deprecate unused Gradle plugins (8a7245d0c3)

### Dependencies
- [LPS-102367]: Update the com.liferay.gradle.plugins.node dependency to version
6.0.0.

## 2.4.47 - 2019-10-21

### Dependencies
- [LPS-102367]: Update the com.liferay.gradle.plugins.node dependency to version
6.0.1.

## 2.4.48 - 2019-10-23

### Dependencies
- [LPS-103580]: Update the com.liferay.gradle.plugins.node dependency to version
7.0.0.