(ns freitag.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [freitag.core :refer [query]]))

(deftest query-test
  (testing "No filter only shows nation-wide holidays"
    (let [result (query {:country :de, :year 2021, :month 1})]
      (is (= 1 (count result)))
      (is (= "Neujahr" (:name (first result))))))

  (testing "Filters show nation-wide and regional holidays"
    (let [result (query {:country :de, :year 2021, :month 1, :state :bw})]
      (is (= 2 (count result)))
      (is (= '("Neujahr" "Heilige drei Könige") (map :name result))))))
