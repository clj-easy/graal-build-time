(ns clj-easy.graal-build-time.packages-test
  (:require [babashka.process :as p]
            [clj-easy.graal-build-time.packages :as packages]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing]]))

(deftest package-list-test
  (-> (p/process ["bb" "build-hello-world"] {:inherit true})
      (p/check))
  (let [expected-packages "clojure, clj_easy.graal_build_time, gbt_test_org, hello, hello_world"
        expected-warning #"^\[clj-easy/graal-build-time\] WARN: Single segment .* single_segment_example__init.class"]
    (testing "packages from directory"
      (is (re-find
            expected-warning
            (with-out-str
              (is (= expected-packages
                     (-> (packages/-list (->> ["test-hello-world/target/classes"
                                                "test-hello-world/lib1/target/lib1.jar"
                                                "test-hello-world/lib2/target/lib2.jar"
                                                "test-hello-world/target/graal-build-time.jar"]
                                              (mapv #(.toPath (io/file %)))))
                         (packages/-listStr))))))))
    (testing "packages from jar"
      (is (re-find
            expected-warning
            (with-out-str
              (is (= expected-packages
                     (-> (packages/-list [(.toPath (io/file "test-hello-world/target/hello-world.jar"))])
                         (packages/-listStr))))))))))
