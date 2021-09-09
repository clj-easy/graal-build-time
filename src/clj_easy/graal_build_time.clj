(ns clj-easy.graal-build-time
  (:require [clojure.string :as str])
  (:import [java.nio.file Path]
           [java.util.jar JarFile JarFile$JarFileEntry])
  (:gen-class
   :methods [^{:static true} [packageList [java.util.List] "[Ljava.lang.String;"]
             ^{:static true} [packageListStr ["[Ljava.lang.String;"] String]]))

(defn keep-fn [nm]
  (when (and
         (not (str/starts-with? nm "clojure"))
         (str/ends-with? nm "__init.class"))
    (-> nm
        (str/replace "__init.class" "")
        (str/replace "/" "."))))

(defn packages-from-jar
  [^Path jar-file]
  (with-open [jar (JarFile. (.toFile jar-file))]
    (let [entries (enumeration-seq (.entries jar))
          packages (keep (fn [^JarFile$JarFileEntry x]
                           (let [nm (.getName x)]
                             (keep-fn nm))) entries)]
      (vec packages))))

(defn packages-from-dir [^Path dir]
  ;; TODO: this needs unit tests as it's not exercises in integration test
  (let [f (.toFile dir)
        files (file-seq f)
        relatives (map (fn [^java.io.File f]
                         (let [path (.toPath f)]
                           (.relativize dir path)))
                       files)
        names (map str relatives)
        packages (keep keep-fn names)]
    packages))

(defn -packageList [paths]
  (into-array (cons "clojure"
                    (mapcat (fn [path]
                              (if (str/ends-with? (str path) ".jar")
                                (packages-from-jar path)
                                (packages-from-dir path))) paths))))

(defn -packageListStr [pl]
  (str/join ", " pl))
