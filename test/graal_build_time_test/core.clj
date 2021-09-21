(ns graal-build-time-test.core
  "This package will be included in build init registration.
  It is included to test that:
   graal_build_time_test_app is not excluded because it happens to start with this package's name:
   graal_build_time_test")

(defn dummy [])
