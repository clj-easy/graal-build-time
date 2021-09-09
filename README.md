# graal-build-time

From GraalVM 22 onwards, the `native-image` global `--initialize-at-build-time`
option will be deprecated. This means you will have to list every package that
you want to initialize at build time separately, like
`initialize-at-build-time=clojure,my_library`. Classes created by Clojure
(currently) need to be initialized at build time. This library automatically
registers classes created by Clojure as such, so you don't have to.

Run `bb tasks` for all relevant project tasks:

```
$ bb tasks
The following tasks are available:

clean
compile
jar
uber
graalvm           Checks GRAALVM_HOME env var
native-image      Builds native image
native-image-test
```

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

Copyright © 2021 Michiel Borkent, Eric Dallo and contributors.
