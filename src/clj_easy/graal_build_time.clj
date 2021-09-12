(ns clj-easy.graal-build-time
  (:require [clojure.string :as str])
  (:import [java.nio.file Path]
           [java.util.jar JarFile JarFile$JarFileEntry])
  (:gen-class
   :methods [^{:static true} [packageList [java.util.List] "[Ljava.lang.String;"]
             ^{:static true} [packageListStr ["[Ljava.lang.String;"] String]]))

(defn ^:private init-classes [nm]
  (when (str/ends-with? nm "__init.class")
    (str/replace nm "__init.class" "")))

(defn packages-from-jar
  [^Path jar-file]
  (with-open [jar (JarFile. (.toFile jar-file))]
    (let [entries (enumeration-seq (.entries jar))
          names (->> entries
                     (map #(.getName ^JarFile$JarFileEntry %)))
          init-classes (->> names
                            (keep init-classes))
          classes (->> names
                       (keep (fn [nm]
                               (when (some #(str/starts-with? nm %) init-classes)
                                 nm)))
                       doall)
          classes (map #(str/replace % "/" ".") classes)]
      classes)))

(defn packages-from-dir [^Path dir]
  ;; TODO: this needs unit tests as it's not exercises in integration test
  (let [f (.toFile dir)
        files (file-seq f)
        relatives (map (fn [^java.io.File f]
                         (let [path (.toPath f)]
                           (.relativize dir path)))
                       files)
        names (map str relatives)
        init-classes (->> names
                          (keep init-classes))
        classes (->> names
                     (keep (fn [nm]
                             (when (some #(str/starts-with? nm %) init-classes)
                               nm))))
        classes (map #(str/replace % (re-pattern
                                      (str/re-quote-replacement
                                       (System/getProperty "file.separator"))) ".") classes)]
    classes))

(defn -packageList [paths]
  (into-array (distinct
               (cons "clojure"
                     (mapcat (fn [path]
                               (if (str/ends-with? (str path) ".jar")
                                 (packages-from-jar path)
                                 (packages-from-dir path))) paths)))))

(defn -packageListStr [pl]
  (str/join ", " pl))
