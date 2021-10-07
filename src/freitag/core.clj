(ns freitag.core
  (:require [clojure.java.io :refer [resource file]]
            [clojure.edn :as edn]))

(defn year-structure [year]
    {(Integer/parseInt (subs (.getName year) 0 4))
     (edn/read-string (slurp year))})

(defn country-structure [country]
  {(keyword (.getName country))
   (into {} (map year-structure (.listFiles country)))})

(defn load-all []
  (let [files (.listFiles (file (resource "data")))]
    (into {} (map country-structure files))))

(def vacations (load-all))

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
          :state :bw})

)
