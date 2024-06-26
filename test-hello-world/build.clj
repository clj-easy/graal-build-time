(ns build
  (:require [build-helper.core :as bh]))

(defn uber [_]
  (bh/uber {:lib 'hello-world/hello-world
            :main 'hello-world.main
            :uber-file "target/hello-world.jar"}))
