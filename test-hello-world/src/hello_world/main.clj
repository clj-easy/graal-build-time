(ns hello-world.main
  (:require [single-segment-example]
            [hello.core]
            [gbt-test-org.core]
            [gbt-test-org.p2.core])
  (:gen-class))

(defn -main []
  ;; sanity for test nses loaded and included
  (single-segment-example/dummy)
  (hello.core/dummy)
  (gbt-test-org.core/dummy)
  (gbt-test-org.p2.core/dummy)
  (println "Hello world"))
