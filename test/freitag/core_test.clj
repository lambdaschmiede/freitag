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
      (is (= '("Neujahr" "Heilige drei Könige") (map :name result)))))

  (testing "No month filter returns the whole year"
    (is (= 9 (count (query {:country :de, :year 2021}))))
    (is (= 12 (count (query {:country :de, :year 2021, :state :bw})))))

  (testing "Multiple years are possible"
    (let [result (query {:country :de, :year [2021, 2022], :month 1})]
      (is (= 2 (count result)))
      (is (= '({:name "Neujahr", :month 1, :day 1, :year 2021}
               {:name "Neujahr", :month 1, :day 1, :year 2022})
             result)))))
