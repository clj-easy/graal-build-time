(ns clj-easy.graal-build-time-test
  (:require [babashka.process :as p]
            [clj-easy.graal-build-time :as bt]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing]]))

(deftest package-list-test
  (-> (p/process ["bb" "uber"] {:inherit true})
      (p/check))
  (let [expected-packages "clojure, graal_build_time_test, graal_build_time_test_app, clj_easy"
        expected-warning #"WARN: Single segment .* single_segment_example__init.class"]
    (testing "packages from directory"
      (is (re-find
            expected-warning
            (with-out-str
              (is (= expected-packages
                     (-> (bt/-packageList [(.toPath (io/file "target/classes"))])
                         (bt/-packageListStr))))))))
    (testing "packages from jar"
      (is (re-find
            expected-warning
            (with-out-str
              (is (= expected-packages
                     (-> (bt/-packageList [(.toPath (io/file "target/test.jar"))])
                         (bt/-packageListStr))))))))))
