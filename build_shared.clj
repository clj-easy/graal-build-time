(ns build-shared
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(def target "target")
(def lib 'com.github.clj-easy/graal-build-time)
(def version (str/trim (slurp "resources/clj-easy/graal-build-time-version.txt")))
(def jar-file (format "target/%s-%s.jar" (name lib) version))
(def uberjar "target/test.jar")
(def sources ["src"])

(defn clean [_]
  (fs/delete-tree "target"))

(defn needs-compile? []
  (seq (fs/modified-since target sources)))

(defn needs-jar? []
  (or (seq (fs/modified-since target jar-file))
      (needs-compile?)))
