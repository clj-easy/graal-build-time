(ns build
  (:refer-clojure :exclude [compile])
  (:require [clojure.tools.build.api :as b]))

(def lib 'com.github.clj-easy/graal-build-time)
(def version (format "0.0.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn compile-clojure [_]
  (println "Compiling Clojure.")
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir class-dir}))

(defn compile-java
  [_]
  (println "Compiling Java.")
  (compile-clojure {:basis basis})
  (b/javac {:src-dirs ["src"]
            :class-dir class-dir
            :basis basis
            :javac-opts ["-source" "8" "-target" "8"]}))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src"]})
  (compile-java {})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

(def uber-basis
  (b/create-basis {:project "deps.edn"
                   :aliases [:test]}))

(defn uber [_]
  (println "Building test uberjar.")
  (compile-java {:basis uber-basis})
  (b/compile-clj {:basis uber-basis
                  :src-dirs ["test"]
                  :class-dir class-dir})
  (println "Building uberjar.")
  (b/uber {:class-dir class-dir
           :uber-file "target/test.jar"
           :basis uber-basis
           :main 'clj-easy.graal-build-time.main}))
