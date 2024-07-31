(ns build
  (:refer-clojure :exclude [compile])
  (:require
   [build-shared :as bs :refer [lib]]
   [clojure.tools.build.api :as b]
   [deps-deploy.deps-deploy :as dd]))

(def class-dir bs/class-dir)
(def basis (b/create-basis {:project "deps.edn"}))
(def with-svm-basis (b/create-basis {:project "deps.edn"
                                     :aliases [:svm]}))
(def version bs/version)
(def jar-file bs/jar-file)

(defn compile-sources [{:keys [class-dir] :or {class-dir class-dir}}]
  (println "Compiling Clojure sources to:" class-dir)
  (b/compile-clj {:basis basis
                  :src-dirs bs/sources
                  :class-dir class-dir})
  (println "Done compiling Clojure sources.")
  (println "Compiling java sources.")
  (b/javac {:src-dirs bs/sources
            :class-dir class-dir
            :basis with-svm-basis
            :javac-opts ["--release" "8" ;; technically current Graal jdk version is our current jdk min,
                                         ;; but clojure produces jdk 8 compatible classes, so we'll arbitrarily
                                         ;; match that
                                         ;; --release was introduce after jdk8, so we'll fail
                                         ;; if compiling with <= jdk8, which is fine.
                         "-Xlint:-options" "-Werror"]})
  (println "Done compiling java sources."))

(defn jar [_]
  (println "Producing jar:" jar-file)
  (let [gh-coords "github.com/clj-easy/graal-build-time"
        project-url (str "https://" gh-coords)]
    (b/write-pom {:class-dir class-dir
                  :lib lib
                  :version version
                  :scm {:connection (format "scm:git:git://%s.git" gh-coords)
                        :developerConnection (format "scm:git:ssh://git@%s.git" gh-coords)
                        :tag (format "v%s" version)
                        :url project-url}
                  :basis basis
                  :src-dirs ["src"]
                  :pom-data [[:description "Initialize Clojure classes at build time with GraalVM native-image"]
                             [:url project-url]
                             [:licenses
                              [:license
                               [:name "The MIT License"]
                               [:url "https://opensource.org/licenses/MIT"]]]
                             [:properties
                              [:project.build.sourceEncoding "UTF-8"]]]}))
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file})
  (println "Done building jar."))

(defn install
  [_]
  (b/install {:basis basis
              :lib lib
              :version version
              :jar-file jar-file
              :class-dir class-dir})
  (println "Installed" lib version "in local maven repo."))

(defn deploy [opts]
  (println "Deploying version" jar-file "to Clojars.")
  (dd/deploy (merge {:installer :remote
                     :artifact jar-file
                     :pom-file (b/pom-path {:lib lib :class-dir class-dir})}
                    opts))
  opts)
