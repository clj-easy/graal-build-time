# graal-build-time

From GraalVM 22 onwards, the `native-image` `--initialize-at-build-time` option
will deprecated. This means you will have to list every package that you want to
initialize at build time separately. Classes created by Clojure (currently) need
to be initialized at build time. This library automatically registers classes
created by Clojure as such, so you don't have to.

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
    [native-test:67818]    classlist:   1,173.31 ms,  0.96 GB
    [native-test:67818]        (cap):   2,913.21 ms,  0.96 GB
    Registering build time packages.
    [native-test:67818]        setup:   4,250.75 ms,  0.96 GB
    [native-test:67818]     (clinit):     183.05 ms,  1.74 GB
    ```

    The `Registering build time packages.` part is coming from this library.  Note
    that the native image build omits `--initialize-at-build-time`, that is taken
    care of by this library.

- Run the binary `native-test` as a test. A zero exit code means that the native
  image runs well (and hello world was printed as another way to convince
  yourself).

# License

Licensed under the MIT license, see LICENSE.

Copyright © 2021 Michiel Borkent, Eric Dallo and contributors.
