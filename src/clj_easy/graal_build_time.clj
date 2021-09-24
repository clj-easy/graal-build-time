(ns clj-easy.graal-build-time
  (:require [clojure.string :as str])
  (:import [java.nio.file Path]
           [java.util.jar JarFile JarFile$JarFileEntry])
  (:gen-class
   :methods [^{:static true} [packageList [java.util.List] "[Ljava.lang.String;"]
             ^{:static true} [packageListStr ["[Ljava.lang.String;"] String]]))

(def jar-entry-file-separator "/")

(defn entry->package [nm split]
  (let [package (->> (str/split nm (re-pattern (str/re-quote-replacement split)))
                     drop-last
                     (str/join "."))]
    (when (str/blank? package)
      (println (str "[clj-easy/graal-build-time] WARN: Single segment package found for class: " nm ". This class has no package and it will not be added to the result packages.")))
    package))

(defn ^:private consider-entry? [nm file-sep]
  (and (not (str/starts-with? nm (str "clojure" file-sep)))
       (str/ends-with? nm "__init.class")))

(defn ^:private contains-parent? [packages package]
  (some #(and (not= % package)
              (str/starts-with? (str package ".")  (str % ".")))
        packages))

(defn unique-packages [packages]
  (->> packages
       (remove (partial contains-parent? packages))
       set))

(defn packages-from-jar
  [^Path jar-file]
  (with-open [jar (JarFile. (.toFile jar-file))]
    (let [entries (enumeration-seq (.entries jar))
          packages (->> entries
                        (map #(.getName ^JarFile$JarFileEntry %))
                        (filter #(consider-entry? % jar-entry-file-separator))
                        (map #(entry->package % jar-entry-file-separator))
                        (remove str/blank?)
                        vec)]
      packages)))

(defn packages-from-dir [^Path dir]
  (let [f (.toFile dir)
        files (rest (file-seq f))
        relatives (map (fn [^java.io.File f]
                         (let [path (.toPath f)]
                           (.relativize dir path)))
                       files)
        names (map str relatives)
        packages (->> names
                      (filter #(consider-entry? % (System/getProperty "file.separator")))
                      (map #(entry->package % (System/getProperty "file.separator")))
                      (remove str/blank?))]
    packages))

(defn -packageList [paths]
  (->> paths
       (mapcat (fn [path]
                 (if (str/ends-with? (str path) ".jar")
                   (packages-from-jar path)
                   (packages-from-dir path))))
       unique-packages
       sort
       (cons "clojure")
       distinct
       into-array))

(defn -packageListStr [pl]
  (str/join ", " pl))
