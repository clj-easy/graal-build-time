(ns build
  (:require [build-helper.core :as bh]))

(defn jar [_]
  (bh/jar {:lib 'gbt-test-org/lib2
           :jar-file "target/lib2.jar"}))
