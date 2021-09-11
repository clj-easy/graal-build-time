#!/usr/bin/env bb

(ns bump-version
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def version-file (io/file "resources" "clj-easy" "graal-build-time-version.txt"))

(case (first *command-line-args*)
 (let [version-string (str/trim (slurp version-file))
                  [major minor patch] (str/split version-string #"\.")
       new-version (str/join "." [major minor (inc  (Integer/parseInt patch))])]
   (spit version-file new-version)))
