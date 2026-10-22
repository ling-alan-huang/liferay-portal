## BNDIncludeResourceCheck

Checks the `-includeresource` instructions of every `bnd.bnd` outside a `src` directory:

* A `*-test` module must not include `test-classes/integration`, because its test bundle is built from the integration test classes anyway. The only exception is a test module whose `build.gradle` enables its `jar` task again.
* `package.json` must not be included, because the Gradle plugin already includes it.