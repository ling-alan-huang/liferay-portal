## JavaServiceTrackerCollectionsCheck

`ServiceTrackerMapFactory.openMultiValueMap(BundleContext, Class, String)` and `ServiceTrackerMapFactory.openSingleValueMap(BundleContext, Class, String)` map services by the value of one property. They build the `(key=*)` filter, a `PropertyServiceReferenceMapper` for the key, the default customizer and the default order, so a call that spells out the same filter and mapper by hand should use them instead.

The check reports a call with a filter and a mapper when the filter is `null` or the `(key=*)` filter of the mapped key, the mapper is a `PropertyServiceReferenceMapper` or a lambda that only emits the property as a `String`, and the map it is assigned to is declared with `String` keys.

### Example

Incorrect:

```java
_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
	bundleContext, SharingPermissionChecker.class, "(model.class.name=*)",
	new PropertyServiceReferenceMapper<>("model.class.name"));
```

Correct:

```java
_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
	bundleContext, SharingPermissionChecker.class, "model.class.name");
```