(ns lint
  (:require [babashka.classpath :as bbcp]
            [babashka.cli :as cli]
            [babashka.fs :as fs]
            [babashka.tasks :as t]
            [clojure.string :as string]
            [lread.status-line :as status]))

(def clj-kondo-cache ".clj-kondo/.cache")

(defn- cache-exists? []
  (fs/exists? clj-kondo-cache))

(defn- delete-cache []
  (when (cache-exists?)
    (fs/delete-tree clj-kondo-cache)))

(defn- build-cache []
  (when (cache-exists?)
    (delete-cache))
  (let [clj-cp (-> (t/clojure {:out :string}
                              "-Spath -M:test:build")
                   with-out-str
                   string/trim)
        bb-cp (bbcp/get-classpath)]
    (status/line :detail "- copying lib configs and creating cache")
    (t/clojure "-M:clj-kondo --skip-lint --copy-configs --dependencies --parallel --lint" clj-cp bb-cp)))

(defn- check-cache [{:keys [rebuild]}]
  (status/line :head "clj-kondo: cache check")
  (if-let [rebuild-reason (cond
                            rebuild
                            "Rebuild requested"

                            (not (cache-exists?))
                            "Cache not found"

                            :else
                            (let [updated-dep-files (fs/modified-since clj-kondo-cache ["deps.edn" "bb.edn"])]
                              (when (seq updated-dep-files)
                                (format "Found deps files newer than lint cache: %s" (mapv str updated-dep-files)))))]
    (do (status/line :detail rebuild-reason)
        (build-cache))
    (status/line :detail "Using existing cache")))

(defn- lint [opts]
  (check-cache opts)
  (status/line :head "clj-kondo: linting")
  (let [{:keys [exit]}
        (t/clojure {:continue true}
                   "-M:clj-kondo --parallel --lint src test scripts build.clj build_shared.clj deps.edn bb.edn")]
    (cond
      (= 2 exit) (status/die exit "clj-kondo found one or more lint errors")
      (= 3 exit) (status/die exit "clj-kondo found one or more lint warnings")
      (> exit 0) (status/die exit "clj-kondo returned unexpected exit code"))))

(defn -main [& args]
  (when-let [opts (cli/parse-opts args)]
    (lint opts)))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
