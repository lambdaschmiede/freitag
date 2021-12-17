(ns freitag.core
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (java.nio.file Path Files FileSystems Paths)))

(def root-path "com/lambdaschmiede/freitag")

(defn- is-edn-file?
  "Checks if the path points to an EDN file. There might be a better way to do this"
  [^Path path]
  (.endsWith (.toString path) ".edn"))

(defn- list-files-for-path [^Path path]
  (filter is-edn-file? (iterator-seq (.iterator (Files/walk path (into-array java.nio.file.FileVisitOption []))))))

(defn- read-file
  "Reads the EDN from the file at the given path. Extracts country and year from the path structure"
  [^Path path]
  (let [[country year] (->> path
                            (.iterator)
                            (iterator-seq)
                            (take-last 2))]
    {(keyword (.toString country))
     {(Integer/parseInt (subs (.toString year) 0 4))
      (clojure.edn/read-string (slurp (Files/newInputStream path (into-array java.nio.file.OpenOption []))))}}))

(defn- load-from-path [^Path path]
  (apply (partial merge-with merge)
         (->> path
              (list-files-for-path)
              (map read-file))))

(defn- load-all []
  (let [uri (.toURI (io/resource root-path))]
    (if (= "jar" (.getScheme uri))
      (with-open [fs (FileSystems/newFileSystem uri {})]
        (load-from-path (.getPath fs root-path (into-array java.lang.String []))))
      (load-from-path (Paths/get uri))  )))

(defn- cond-filter
  "Applies the filter-fn to the coll if the condition is true"
  [condition filter-fn coll]
  (filter (fn [elem]
            (if condition (filter-fn elem) true)) coll))

(def vacations (delay (load-all)))

(defn query
  "Queries for vacation days for the given country, year, month, and (optional) state"
  [{:keys [country year month state]}]
  (let [vacations (select-keys (get @vacations country)
                               (if (vector? year) year [year]))]

    (->> (mapcat (fn [[y dates]]
                   (map #(assoc % :year y) dates)) vacations)
         (cond-filter (some? month) #(= month (:month %)))
         (filter #(if-let [states (:states %)]
                    (contains? states state)
                    true))
         (sort-by (juxt :year :month :day)))))

(comment

  ;; Single Year, month, state
  (query {:country :de
          :year 2021
          :month 1
          :state :bw})

  ;; Single Year, month, no state
  (query {:country :de
          :year 2021
          :month 1})

  ;; Single Year, no month or state
  (query {:country :de
          :year 2021})

  ;; Multiple Years, month
  (query {:country :de
          :month 1
          :year [2021 2022 2023]})

  ;; Multiple Years, no month
  (query {:country :de
          :year [2021 2022 2023]})
  )
