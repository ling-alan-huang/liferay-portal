# Liferay Gradle Utilities Change Log

## 1.0.26 - 2016-06-16

### Commits
- [LPS-65749]: Closures with null owners don't work in Gradle 2.14 (b42316699d)
- [LPS-65749]: No need to use closures here (749344ac88)
- [LPS-65810]: Gradle plugins aren't used in OSGi, no need to export anything
(83cdd8ddcd)

## 1.0.25 - 2016-04-17

### Commits
- [LPS-65086]: Logging (a636aa9a90)
- [LPS-65086]: Copy 998ede01af55abed36bb8a5f1d2bd78d604a2216 to Gradle
(dce2286b8b)
- [LPS-61099]: Delete build.xml in modules (c9a7e1d370)
- [LPS-63943]: This is done automatically now (f1e42382d9)
- [LPS-62883]: Update build.gradle in plugins that use gradle-util (9ab64f3eb7)
- [LPS-62942]: Explicitly list exported packages for correctness (f095a51e25)
- [LPS-61088]: Remove classes and resources dir from Include-Resource
(1b0e1275bc)

## 1.0.23 - 2015-11-03

### Commits
- [LPS-60153]: First download to a tmp file, then rename (76ce795917)
- [LPS-60153]: "verbose" does not work when Ant is called from Gradle
(c6891c0886)
- [LPS-59564]: Update directory layout for "sdk" modules (ea19635556)

## 1.0.22 - 2015-10-07

### Commits
- [LPS-58516]: Rename (c29a9d7fa9)
- [LPS-58516]: Add util method to create one or more "classpath jars" from a
files list (abe9fbb7a2)

## 1.0.21 - 2015-09-15

### Commits
- [LPS-58587]: Find the last modified time by looking at the whole subtree
(468bdf4522)