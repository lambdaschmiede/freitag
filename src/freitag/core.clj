(ns freitag.core
  (:require [clojure.java.io :refer [resource file]]
            [clojure.edn :as edn]))


(def root-path "com/lambdaschmiede/freitag")

(defn path-from-fs [uri]
  (.getPath (java.nio.file.FileSystems/newFileSystem uri {}) root-path (into-array java.lang.String [])))

(defn path-from-uri [uri]
  (java.nio.file.Paths/get uri))

(defn is-edn-file [path]
  (.endsWith (.toString path) ".edn"))

(defn read-file [path]
  (let [country (.toString (.getName path 3))
        year (Integer/parseInt (subs (.toString (.getName path 4)) 0 4))]
    {(keyword country)
     {year (clojure.edn/read-string (slurp (java.nio.file.Files/newInputStream path (into-array java.nio.file.OpenOption []))))}}))

(defn load-all []
  (let [uri (.toURI (clojure.java.io/resource root-path))
        path (if (= "jar" (.getScheme uri))
               (path-from-fs uri)
               (path-from-uri uri))
        all-seq (iterator-seq (.iterator (java.nio.file.Files/walk path (into-array java.nio.file.FileVisitOption []))))
        all-edn-files (filter is-edn-file all-seq)]
    (apply (partial merge-with merge)
           (map read-file all-edn-files))))

(defonce vacations (load-all))

(defn query
  "Queries for vacation days for the given country, year, month, and (optional) state"
  [{:keys [country year month state]}]
  (->> (-> vacations
           (get country)
           (get year))
       (filter #(= month (:month %)))
       (filter  #(if-let [states (:states %)]
                   (contains? states state)
                   true)))) 

(comment
  (query {:country :de
          :year 2021
          :month 1
          :state :bw}))
