(ns build-shared
  (:require [clojure.string :as str]))

(def version-file "resources/clj-easy/graal-build-time-version.txt")
(def version nil)
(defn refresh-version []
  (alter-var-root #'version (constantly (str/trim (slurp version-file)))))
(refresh-version)

(def target "target")
(def lib 'com.github.clj-easy/graal-build-time)
(def jar-file (format "%s/%s-%s.jar" target (name lib) version))
(def sources ["src"])
(def class-dir (str target "/classes"))

(defn change-log-check []
  (let [log (slurp "CHANGELOG.md")
        new-changes (last (re-find #"(?ms)^## Unreleased *$(.*?)^##|\z" log))]
    (if (not (and new-changes (re-find #"(?ims)[a-z]" new-changes)))
      (do
        (println "FAIL: Change log must contain Unreleased section with some text.")
        (System/exit 1))
      (println "PASS: Change log Unreleased section found with some text."))))

(defn change-log-update []
  (println "Updating change log")
  (refresh-version)
  (let [log (slurp "CHANGELOG.md")
        new-log (str/replace-first
                 log
                 #"(?ms)^## Unreleased *$"
                 (str "## Unreleased\n\n-\n\n## v" version))]
    (spit "CHANGELOG.md" new-log)
    (println (str "Change log updated for v" version))))
