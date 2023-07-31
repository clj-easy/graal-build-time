(ns build
  (:refer-clojure :exclude [compile])
  (:require
   [clojure.string :as str]
   [clojure.tools.build.api :as b]))

(def lib 'hello-world/hello-world)
(def class-dir "target/classes")
(def uber-file "target/hello-world.jar")
(def src-dirs ["src"])
(def basis (b/create-basis))

(defmacro with-err-str
  [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn uber [_]
  (println "Writing pom")
  (->> (b/write-pom {:class-dir class-dir
                     :lib lib
                     :version "1.0.0"
                     :basis basis
                     :src-dirs ["src"]})
       with-err-str
       str/split-lines
       ;; Avoid confusing future me/you: suppress "Skipping coordinate" messages for our jars, we don't care, we are creating an uberjar
       (remove #(re-matches #"^Skipping coordinate: \{:local/root .*target/(lib1|lib2|graal-build-time).jar.*" %))
       (run! println))
  (b/copy-dir {:src-dirs src-dirs
               :target-dir class-dir})
  (println "Compile sources to classes")
  (b/compile-clj {:basis basis
                  :src-dirs src-dirs
                  :class-dir class-dir
                  :ns-compile '[hello-world.main]})
  (println "Building uberjar")
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis basis
           :main 'hello-world.main}))
