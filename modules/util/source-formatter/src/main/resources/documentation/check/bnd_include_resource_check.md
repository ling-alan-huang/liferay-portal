## BNDIncludeResourceCheck

A `*-test` module must not include `test-classes/integration` in its `bnd.bnd`, because the test bundle is built from the integration test classes anyway. The only exception is a test module whose `build.gradle` enables its `jar` task again.