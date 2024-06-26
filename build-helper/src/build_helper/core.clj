(ns build-helper.core
  "Reusable build support for internal test projects"
  (:require
   [clojure.string :as str]
   [clojure.tools.build.api :as b]))

(defmacro with-err-str
  [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn uber [{:keys [lib main uber-file]}]
  (let [class-dir "target/classes"
        src-dirs ["src"]
        basis (b/create-basis)]
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
                    :ns-compile [main]})
    (println "Building uberjar" uber-file)
    (b/uber {:class-dir class-dir
             :uber-file uber-file
             :basis basis
             :main main})))

(defn jar [{:keys [lib jar-file]}]
  (let [class-dir "target/classes"
        src-dirs ["src"]
        basis (b/create-basis)]
    (println "Writing pom")
    (b/write-pom {:class-dir class-dir
                  :lib lib
                  :version "1.0.0"
                  :basis basis
                  :src-dirs ["src"]})
    (b/copy-dir {:src-dirs src-dirs
                 :target-dir class-dir})
    (println "Compile sources to classes")
    (b/compile-clj {:basis basis
                    :src-dirs src-dirs
                    :class-dir class-dir})
    (println "Building jar" jar-file)
    (b/jar {:class-dir class-dir
            :jar-file jar-file
            :basis basis})))
