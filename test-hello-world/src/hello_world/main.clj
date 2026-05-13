(ns hello-world.main
  (:require [hello.core]
            [gbt-test-org.core]
            [gbt-test-org.p2.core]
            [gbt+test.core])
  (:gen-class))

(defn -main []
  ;; sanity for test nses loaded and included
  (hello.core/dummy)
  (gbt-test-org.core/dummy)
  (gbt-test-org.p2.core/dummy)
  (gbt+test.core/dummy)
  (println "Hello world"))
