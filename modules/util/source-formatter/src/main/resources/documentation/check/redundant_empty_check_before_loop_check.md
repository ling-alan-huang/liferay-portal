## RedundantEmptyCheckBeforeLoopCheck

Do not wrap a for-each loop in an if-statement that only checks whether the iterated array or collection is empty. A for-each loop over an empty array or collection runs zero times, so the check adds nothing.

### Example

Incorrect:

```java
long[] ids = StringUtil.split(
	ParamUtil.getString(actionRequest, "ids"), 0L);

if (ArrayUtil.isNotEmpty(ids)) {
	for (long id : ids) {
		_deleteEntry(id);
	}
}
```

Correct:

```java
long[] ids = StringUtil.split(
	ParamUtil.getString(actionRequest, "ids"), 0L);

for (long id : ids) {
	_deleteEntry(id);
}
```

The check flags an if-statement without an `else` whose body is only a for-each loop over the same variable, when the condition is one of the following:

- `!list.isEmpty()` or `array.length > 0`. The condition already dereferences the variable, so the variable is never `null` inside the if-statement.
- `ArrayUtil.isNotEmpty(array)`, `ListUtil.isNotEmpty(list)` or `SetUtil.isNotEmpty(set)`, where the variable is a local variable that is never reassigned and is initialized with `new`, `StringUtil.split(...)`, or a `ParamUtil.get*Values(...)` call without a default value.

`ArrayUtil.isNotEmpty`, `ListUtil.isNotEmpty` and `SetUtil.isNotEmpty` also return `false` for `null`. When the variable may be `null`, such as a method parameter, a field, or the return value of another method, the if-statement also prevents a `NullPointerException` and is not flagged.