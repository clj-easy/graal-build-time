# Changelog

<!-- Our publish process updates Unreleased header appropriately, do not update by hand -->
## Unreleased

- Add license (and other common entries) to library pom file [#44](https://github.com/clj-easy/graal-build-time/issues/44)

## v1.0.5

- ⚠️ _**You'll need to update your `native-image` command line**_ ⚠️ - Adapt to GraalVM deprecation
  - The Graal team has deprecated automatic discovery of GraalVM `native-image` `Feature` classes. They feel it is safer for you to explicitly opt in when enabling features. 
  - Since `graal-build-time` was implemented as an auto-discovered Feature class, we have adapted to this change.
  - After upgrading, you need to add `--features=clj_easy.graal_build_time.InitClojureClasses` to your `native-image` command line.

## v0.1.4

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
