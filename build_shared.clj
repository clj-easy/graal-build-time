(ns build-shared
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(def version-file "resources/clj-easy/graal-build-time-version.txt")
(def version nil)
(defn refresh-version []
  (alter-var-root #'version (constantly (str/trim (slurp version-file)))))
(refresh-version)

(def target "target")
(def lib 'com.github.clj-easy/graal-build-time)
(def jar-file (format "target/%s-%s.jar" (name lib) version))
(def uberjar "target/test.jar")
(def sources ["src"])
(def class-dir "target/classes")

(defn clean [_]
  (fs/delete-tree "target"))
