(ns clj-easy.graal-build-time-test
  (:require [babashka.process :as p]
            [clj-easy.graal-build-time :as bt]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing]]))

(deftest package-list-test
  (-> (p/process ["bb" "uber"] {:inherit true})
      (p/check))
  (let [expected "clojure, graal_build_time_test, graal_build_time_test_app, clj_easy"]
    (testing "packages from directory"
      (is (= expected
             (-> (bt/-packageList [(.toPath (io/file "target/classes"))])
                 (bt/-packageListStr)))))
    (testing "packages from jar"
      (is (= expected
             (-> (bt/-packageList [(.toPath (io/file "target/test.jar"))])
                 (bt/-packageListStr)))))))
