(ns single-segment.main
  "Exists to test our emitting of warning message for single segement namespaces"
  (:require [digest]) ;; this deprecated ns is, as you can see, single segment
  (:gen-class))

(defn -main []
  (println "md5 digest for clojure" (digest/md5 "clojure")))
