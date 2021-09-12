(ns clj-easy.graal-build-time
  (:require [clojure.string :as str])
  (:import [java.nio.file Path]
           [java.util.jar JarFile JarFile$JarFileEntry])
  (:gen-class
   :methods [^{:static true} [packageList [java.util.List] "[Ljava.lang.String;"]
             ^{:static true} [packageListStr ["[Ljava.lang.String;"] String]]))

(def file-sep-pattern )

(defn entry->package [nm split]
  (let [package (->> (str/split nm (re-pattern (str/re-quote-replacement split)))
                     drop-last
                     (str/join "."))]
    (when (str/blank? package)
      (println "WARN: Single segment package found for class:" nm ".This class has no package and it will not be added to the result packages."))
    package))

(defn ^:private consider-jar-file-entry? [nm]
  (and (not (str/starts-with? nm (str "clojure" (System/getProperty "file.separator"))))
       (str/ends-with? nm "__init.class")))

(defn ^:private contains-parent? [packages package]
  (some #(and (not= % package)
              (str/starts-with? package %))
        packages))

(defn packages-from-jar
  [^Path jar-file]
  (with-open [jar (JarFile. (.toFile jar-file))]
    (let [entries (enumeration-seq (.entries jar))
          packages (->> entries
                        (map #(.getName ^JarFile$JarFileEntry %))
                        (filter consider-jar-file-entry?)
                        ;; always split jar entries on "/"
                        (map #(entry->package % "/"))
                        (remove str/blank?))
          unique-packages (->> packages
                               (remove (partial contains-parent? packages))
                               set)]
      unique-packages)))

(defn packages-from-dir [^Path dir]
  ;; TODO: this needs unit tests as it's not exercises in integration test
  (let [f (.toFile dir)
        files (file-seq f)
        relatives (map (fn [^java.io.File f]
                         (let [path (.toPath f)]
                           (.relativize dir path)))
                       files)
        names (map str relatives)
        packages (->> names
                      (filter consider-jar-file-entry?)
                      ;; split file-names on OS-specific file.separator
                      (map #(entry->package % (System/getProperty "file.separator"))))]
    packages))

(defn -packageList [paths]
  (into-array (distinct
               (cons "clojure"
                     (mapcat (fn [path]
                               (if (str/ends-with? (str path) ".jar")
                                 (packages-from-jar path)
                                 (packages-from-dir path))) paths)))))

(defn -packageListStr [pl]
  (str/join ", " pl))
