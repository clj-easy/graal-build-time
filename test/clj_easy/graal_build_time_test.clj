(ns clj-easy.graal-build-time-test
  (:require [babashka.process :as p]
            [clj-easy.graal-build-time :as bt]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing]]))

(deftest package-list-test
  (-> (p/process ["bb" "uber"] {:inherit true})
      (p/check))
  (testing "packages from directory"
    (is (= "clojure, clj_easy"
           (-> (bt/-packageList [(.toPath (io/file "target/classes"))])
               (bt/-packageListStr)))))
  (testing "packages from jar"
    (is (= "clojure, clj_easy"
           (-> (bt/-packageList [(.toPath (io/file "target/test.jar"))])
               (bt/-packageListStr))))))
