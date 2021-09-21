;; see https://ask.clojure.org/index.php/10905/control-transient-deps-that-compiled-assembled-into-uberjar?show=10913#c10913
(require 'clojure.tools.deps.alpha.util.s3-transporter)

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

(defn clean [_]
  (bs/clean {}))

(defn compile-sources [_]
  (println "Compiling Clojure sources.")
  (b/compile-clj {:basis basis
                  :src-dirs bs/sources
                  :class-dir class-dir
                  :ns-compile '[clj-easy.graal-build-time]})
  (println "Done compiling Clojure sources.")
  (println "Compiling java sources.")
  (b/javac {:src-dirs bs/sources
            :class-dir class-dir
            :basis with-svm-basis
            :javac-opts ["-source" "8" "-target" "8"]})
  (println "Done compiling java sources."))

(defn jar [_]
  (println "Producing jar:" jar-file)
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src"]})
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

(def uber-basis
  (b/create-basis {:project "deps.edn"
                   :aliases [:uber]}))

(defn uber [_]
  (println "Building test uberjar.")
  (compile-sources {})
  (println "Compiling extra sources.")
  (b/compile-clj {:basis uber-basis
                  :src-dirs ["test"]
                  :class-dir class-dir
                  :ns-compile '[graal-build-time-test-app.main
                                graal-build-time-test.core]})
  (println "Building uberjar.")
  (b/uber {:class-dir class-dir
           :uber-file "target/test.jar"
           :basis uber-basis
           :main 'graal-build-time-test-app.main}))

(defn deploy [opts]
  (println "Deploying version" jar-file "to Clojars.")
  (dd/deploy (merge {:installer :remote
                     :artifact jar-file
                     :pom-file (b/pom-path {:lib lib :class-dir class-dir})}
                    opts))
  opts)
