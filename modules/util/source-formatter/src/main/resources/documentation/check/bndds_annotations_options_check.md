## BNDDSAnnotationsOptionsCheck

`-dsannotations-options: inherit` makes bnd read the fields and methods that a `@Component` class inherits from its superclasses into the component description. It only matters when a superclass declares a field or method annotated with `@Activate`, `@Deactivate`, `@Modified` or `@Reference` from `org.osgi.service.component.annotations`. A `bnd.bnd` outside a `src` directory must not use `inherit` when no component in `src/main/java` or `src/testIntegration/java` inherits such a member, because the option does not change the bundle.

A superclass that cannot be found in the repository is assumed to declare such a member, unless it is a `java`, `javax` or `jakarta` class.