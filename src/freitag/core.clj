(ns freitag.core
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (java.nio.file Path Files FileSystems Paths)
           (java.net URI)))

(def root-path "com/lambdaschmiede/freitag")

(defn- is-edn-file
  "Checks if the path points to an EDN file. There might be a better way to do this"
  [^Path path]
  (.endsWith (.toString path) ".edn"))

(defn- path-from-uri
  "When working on the library, the resource files are treated as files"
  [^URI uri]
  (Paths/get uri))

(defn- path-from-fs
  "When referencing the packed JAR, the resources need to be handled from inside the zip
  which requires special treatment"
  [^URI uri]
  (.getPath (FileSystems/newFileSystem uri {})
            root-path (into-array java.lang.String [])))

(defn read-file
  "Reads the EDN from the file at the given path. Extracts country and year from the path structure"
  [^Path path]
  (let [[country year] (->> path
                            (.iterator)
                            (iterator-seq)
                            (take-last 2))]
    {(keyword (.toString country))
     {(Integer/parseInt (subs (.toString year) 0 4))
      (clojure.edn/read-string (slurp (Files/newInputStream path (into-array java.nio.file.OpenOption []))))}}))

(defn- load-all []
  (let [uri (.toURI (io/resource root-path))
        path (if (= "jar" (.getScheme uri))
               (path-from-fs uri)
               (path-from-uri uri))
        all-edn-files (filter is-edn-file (iterator-seq (.iterator (Files/walk path (into-array java.nio.file.FileVisitOption [])))))]
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
                   true))))

(comment
  (query {:country :de
          :year 2021
          :month 1
          :state :bw}))
