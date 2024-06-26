(ns build
  (:require [build-helper.core :as bh]))

(defn jar [_]
  (bh/jar {:lib 'gbt-test-org/lib1
           :jar-file "target/lib1.jar"}))
