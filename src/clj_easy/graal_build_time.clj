(ns clj-easy.graal-build-time
  (:require [clojure.string :as str])
  (:gen-class
   :methods [^{:static true} [packageList [] "[Ljava.lang.String;"]
             ^{:static true} [packageListStr ["[Ljava.lang.String;"] String]]))

(defn -packageList []
  (->> (map ns-name (all-ns))
       (remove #(str/starts-with? % "clojure"))
       (map #(str/split (str %) #"\."))
       (keep butlast)
       (map #(str/join "." %))
       distinct
       (map munge)
       (cons "clojure")
       into-array))

(defn -packageListStr [pl]
  (str/join ", " pl))
