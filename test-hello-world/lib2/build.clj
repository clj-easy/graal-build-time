(ns build
  (:refer-clojure :exclude [compile])
  (:require [clojure.tools.build.api :as b]))

(def lib 'gbt-test-org/lib2)
(def class-dir "target/classes")
(def jar-file "target/lib2.jar")
(def src-dirs ["src"])
(def basis (b/create-basis))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version "1.0.0"
                :basis basis
                :src-dirs ["src"]})
  (b/copy-dir {:src-dirs src-dirs
               :target-dir class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs src-dirs
                  :class-dir class-dir})
  (b/jar {:class-dir class-dir
           :jar-file jar-file
           :basis basis}))
