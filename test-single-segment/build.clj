(ns build
  (:require [build-helper.core :as bh]))

(defn uber [_]
  (bh/uber {:lib 'single-segment/single-segment
            :main 'single-segment.main
            :uber-file "target/single-segment.jar"}))
