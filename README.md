[![Clojars Project](https://img.shields.io/clojars/v/com.github.clj-easy/graal-build-time.svg)](https://clojars.org/com.github.clj-easy/graal-build-time)
[![Slack community](https://img.shields.io/badge/Slack-chat-blue?style=flat-square)](https://clojurians.slack.com/archives/C02DQFVS0MC)

# graal-build-time

Automatically detect and initialize Clojure classes during `native-image` compilation.

> ⬆️ **Upgrading to from v0 to v1?** <br>
> Add `--features=clj_easy.graal_build_time.InitClojureClasses` to your `native-image` command line.

We compile our Clojure sources to `.class` files so that GraalVM `native-image` can in turn compile them into a binary executable.
Due to their nature, Clojure `.class` files typically must be initialized at build time by GraalVM `native-image`.
If they are not initialized at build time, GraalVM will still create your binary executable, but when you run it, it probably won't work. 
You will likely see it fail with an error that includes:
```
java.io.FileNotFoundException: Could not locate clojure/core__init.class, clojure/core.clj or clojure/core.cljc on classpath
```

In the early days, our solution was to use `native-image`'s `--initialize-at-build-time` to globally initialize all classes at build time.
Because global initialization of all classes can be problematic, GraalVM deprecated this usage.
Instead, you must be explicit and specify the packages of the classes you need to initialize at build time.
For example: `--initialize-at-build-time=clojure,my_library,etc...`
But, this can be tedious and error-prone.

Hence, `graal-build-time`.
This library automatically detects `.class` files created by Clojure and asks `native-image` to initialize them at build time. 

## Usage

We assume you are using a [current stable release of GraalVM](https://github.com/graalvm/graalvm-ce-builds/releases/).
We don't support older releases.

For your `native-image` build:
1. If you are using `--initialize-at-build-time`, remove it.
2. Add `--features=clj_easy.graal_build_time.InitClojureClasses`
3. Include the `graal-build-time` library on your classpath.
This is typically done by adding this library to your project dependencies (see the clojars link above).

During the `native-image` build process, you will see a line of output from `graal-build-time` describing the packages it has detected:

```
[clj-easy/graal-build-time] Registering packages for build time initialization: clojure, clj_easy.graal_build_time
```

## How it works

`graal-build-time` hooks into the GraalVM `native-image` build process via a GraalVM `Feature` class.
It inspects the classpath. 
Each class file that ends with `__init.class` is assumed to have been created by Clojure. 
The packages of these classes are then added to the list of packages to be initialized at build time.

## Overriding classes

If there are classes in packages that you would like to initialize at runtime, you can 
use the `--initialize-at-run-time=my.org.MyClass` `native-image` argument. 

For example, when using http-kit, the `org.httpkit` package will be included for
build-time initialization. You will need to override one class via `native-image` argument:
```
--initialize-at-run-time=org.httpkit.client.ClientSslEngineFactory$SSLHolder
```

## Single segment namespaces

This library doesn't work with single segment namespaces.
A single segment namespace is one without any `.` characters in it, for example: `(ns digest)`.

A single segment namespace, becomes, in the eyes of the JVM, package-less and ends up in the JVM default package.
Single segment namespaces are problematic in general in Clojure and, because they are package-less, will not be initialized by `graal-build-time`.

`graal-build-time` will emit a warning when it detects `.class` files generated from a single segment namespace, for example:

```
[clj-easy/graal-build-time] WARN: Single segment namespace found for class: digest__init.class. Because this class has no package, it cannot be registered for initialization at build time. 
```

Starting with GraalVM v22, because it enables`--strict-image-heap` by default, you'll also see `native-image` fail your build when single segment namespaces are present.
See https://github.com/clj-easy/graal-build-time/issues/35[#35] for details if you are curious.

## Develop

Run `bb tasks` for all relevant project tasks.

Tasks attempt to avoid unnecessary work by comparing source and target file dates.
If you want to skip this optimization, run `bb clean` before running your task.

Use `bb native-image-test` to run our integration tests.
- This task builds native images for a hello world app, and then runs them.
- The hello world Clojure sources are compiled to Java classes.
- We use GraalVM's `native-image` with `graal-build-time` on the classpath to create 2 variants of the same app:
  - one built from the uberjar
  - the other built directly from the classes dir
- Note that we omit `--initialize-at-build-time` when creating the native images.
The work that this deprecated option carried out is now taken care of by `graal-build-time`.
- During native image creation, you'll see output that looks like:

    ```
    [clj-easy/graal-build-time] WARN: Single segment namespace found for class: single_segment_example__init.class. Because this class has no package, it cannot be registered for initialization at build time.
    [clj-easy/graal-build-time] Registering packages for build time initialization: clojure, clj_easy.graal_build_time, gbt_test_org, hello, hello_world
    ```
    Note:
    - `[clj-easy/graal-build-time] Registering packages for build time initialization...` will always appear
    - `[clj-easy/graal-build-time] WARN: Single segment namespace found...` will appear to warn you of the repercussions of a compiled Clojure single segment namespace found in your build.

# License

Licensed under the MIT license, see LICENSE.

Copyright © 2021-2023 Michiel Borkent, Eric Dallo, Rahul Dé, Lee Read and contributors.
