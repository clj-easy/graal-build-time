# Changelog

<!-- Our publish process updates Unreleased header appropriately, do not update by hand -->
## Unreleased

- Our output lines during a `native-image` build are now identified with prefix `[clj-easy/graal-build-time]` [#18](https://github.com/clj-easy/graal-build-time/pull/18)
- Child package `hello.world` from classpath element x is now properly excluded when parent package `hello` occurs in classpath element y [#17](https://github.com/clj-easy/graal-build-time/pull/17)
- Add source control management (scm) info within our jar so that clojars and others can point back to our github repo [#19](https://github.com/clj-easy/graal-build-time/pull/19)

## v0.1.3

- Package `hello_world` is no longer excluded due to erroneously being considered a child of package `hello` [#12](https://github.com/clj-easy/graal-build-time/pull/12)

## v0.1.2

- Stop asking `native-image` to register blank packages, it ascribes this to mean a request to register all packages [#11](https://github.com/clj-easy/graal-build-time/pull/11)
- Properly analyze `clojure.*` packages on Windows when working from jar file [#11](https://github.com/clj-easy/graal-build-time/pull/11)

## v0.1.0

- First 0.1.x release!

## v0.0.x

- Initial test releases
