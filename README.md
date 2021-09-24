[![Clojars Project](https://img.shields.io/clojars/v/com.github.clj-easy/graal-build-time.svg)](https://clojars.org/com.github.clj-easy/graal-build-time)
[![Slack community](https://img.shields.io/badge/Slack-chat-blue?style=flat-square)](https://clojurians.slack.com/archives/C02DQFVS0MC)

# graal-build-time

From GraalVM 22 onwards, the `native-image` global `--initialize-at-build-time`
option will be deprecated. This means you will have to list every package that
you want to initialize at build time separately, like
`initialize-at-build-time=clojure,my_library`. Classes created by Clojure
(currently) need to be initialized at build time. This library automatically
registers classes created by Clojure as such, so you don't have to.

## Usage

For you `native-image` build:
1. If you are using a global `--intialize-at-build-time`, remove it. 
2. Include this library on your classpath.
This is typically done by simply adding this library to your project dependencies (see clojars link above).

During the image build, you will a line of output like:

    Registering packages for build time initialization: clojure, clj_easy

## How does it work

This library inspects the classpath during a native image build. All classes
that end with `__init.class` are assumed to be created by Clojure. The package
of that class is then added to the results.

## Overriding classes

If there are classes in packages that you would like to override, you can still
use the `--initialize-at-run-time=my.org.MyClass` argument in your
build. E.g. when using http-kit, the `org.httpkit` package is included for
build-time initialization, but you will still need to override one class:
`--initialize-at-run-time=org.httpkit.client.ClientSslEngineFactory$SSLHolder`.

## Single segment namespaces

This library doesn't work with single segment namespaces like `digest` in a
file like `src/digest.clj`. We recommend not using single segment namespaces in
libraries.

## Develop

Run `bb tasks` for all relevant project tasks.

Tasks attempt to avoid unnecessary work by comparing source and target file dates.
If you want to skip this optimization, run `bb clean` before running your task.

Use `bb native-image-test` to run our integration tests.
- This task builds native images for a hello world  app, and then runs them.
- The hello world sources are compiled to Java classes and then jarred to an uberjar.
- We use GraalVM's `native-image` with `graal-build-time` on the classpath to create 2 variants of the same app:
  - one built from the uberjar
  - the other built directly from the classes dir
- Note that we omit `--initialize-at-build-time` when creating the native images.
The the work that this deprecated option carried out is now taken care of by `graal-build-time`.
- During native image creation, you'll see output that looks like this:

    ```
    [target/native-test-classes:34725]    classlist:   1,627.53 ms,  0.96 GB
    [target/native-test-classes:34725]        (cap):   1,892.39 ms,  0.96 GB
    WARN: Single segment package found for class: single_segment_example__init.class. This class has no package and it will not be added to the result packages.
    Registering packages for build time initialization: clojure, graal_build_time_test, graal_build_time_test_app, clj_easy
    [target/native-test-classes:34725]        setup:   3,885.01 ms,  0.96 GB
    [target/native-test-classes:34725]     (clinit):     219.91 ms,  1.74 GB
    ```
    The following lines are coming from graal-build-time:
    - `Registering packages for build time initialization...` will always appear
    - `WARN: Single segment package found...` will appear to warn you of the repercussions of a compiled Clojure single segment found in your build.

# License

Licensed under the MIT license, see LICENSE.

Copyright © 2021 Michiel Borkent, Eric Dallo, Rahul Dé, Lee Read and contributors.
