## BNDIncludeResourceCheck

Checks the `-includeresource` instructions of every `bnd.bnd` outside a `src` directory:

* A `*-test` module must not include `test-classes/integration`, because its test bundle is built from the integration test classes anyway. The only exception is a test module whose `build.gradle` enables its `jar` task again.
* `package.json` must not be included, because the Gradle plugin already includes it.
* An included path must exist in the repository, and must not include again a file that `src/main/resources` (`src/testIntegration/resources` in a `*-test` module) already ships. Paths with a macro, optional paths and the module's own build output are not checked: `build`, `classes`, `tmp`, the jar of a core module, and a `lib` that the module generates from `wsdl`, `xsd` or a `Copy` task. The build output of another module counts as missing, because git does not track it.