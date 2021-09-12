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

Just include this library on your classpath in your native-image build, that's
it. During the image build you will see a line of output like:

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

This library doesn't work with single segmenet namespaces like `digest` in a
file like `src/digest.clj`. We recommend not using single segment namespaces in
libraries.

## Develop

Run `bb tasks` for all relevant project tasks.

Running `native-image-test` will:

- Build this library
- Produce an uberjar with a main which prints `"Hello world"`
- Make a native image named `native-test` out of it. Notice in the output:

    ```
    [native-test:73977]    classlist:   1,082.78 ms,  0.96 GB
    [native-test:73977]        (cap):   1,431.59 ms,  0.96 GB
    Registering packages for build time initialization: clojure, clj_easy
    [native-test:73977]        setup:   2,882.60 ms,  0.96 GB
    [native-test:73977]     (clinit):     183.20 ms,  1.74 GB
    ```

    The `Registering packages for build time initialization` line is coming from
    this library.  Note that the native image build omits
    `--initialize-at-build-time`, that is taken care of by this library.

- Run the binary `native-test` as a test. A zero exit code means that the native
  image runs well (and hello world was printed as another way to convince
  yourself).

# License

Licensed under the MIT license, see LICENSE.

Copyright © 2021 Michiel Borkent, Eric Dallo, Rahul Dé and contributors.
