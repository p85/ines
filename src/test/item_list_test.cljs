(ns test.item-list-test
  (:require [cljs.test :refer (deftest is)])
  (:require [ines.item-list :as item-list]))

(deftest test-the-list-structure
  (is (vector? item-list/item-list)))
(deftest test-for-required-fields
  (is (every? #(= % true) (map #(and (contains? % :name) (contains? % :units)) item-list/item-list))))
(deftest name-field-should-be-a-vector
  (is (every? #(= % true) (map #(vector? (:name %)) item-list/item-list))))
(deftest units-field-should-be-a-string-and-not-empty
  (is (every? #(= % true) (map #(and (string? (:units %)) (not (empty? (:units %)))) item-list/item-list))))