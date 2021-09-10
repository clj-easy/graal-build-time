(ns build-shared
  (:require [babashka.fs :as fs]
            [babashka.process :as p]
            [clojure.string :as str]))

(defn git-count-revs
  [_]
  (-> (p/process ["git" "rev-list" "HEAD" "--count"]
                 {:err :inherit
                  :out :string})
      (p/check)
      :out
      str/trim))

(def target "target")
(def lib 'com.github.clj-easy/graal-build-time)
(def version (delay (format "0.0.%s" (git-count-revs nil))))
(def jar-file (delay (format "target/%s-%s.jar" (name lib) @version)))
(def uberjar "target/test.jar")
(def sources ["src"])

(defn clean [_]
  (fs/delete-tree "target"))

(defn needs-compile? []
  (seq (fs/modified-since target sources)))

(defn needs-jar? []
  (or (seq (fs/modified-since target @jar-file))
      (needs-compile?)))
