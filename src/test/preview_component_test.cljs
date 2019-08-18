(ns test.preview-component-test
  (:require [cljs.test :refer (deftest is use-fixtures)])
  (:require [inez.app-state :as app-state])
  (:require [test.fixture :as fixture])
  (:require [inez.preview-component :as preview-component]))

(use-fixtures :each (fixture/before-each app-state/app-state))

(deftest test-method-show-preview-results
  (let [all-items [{:name "A" :amount 1 :units "Kg"} {:name "B" :amount 2 :units "Stück"} {:name "C" :amount 3 :units "T"} {:name "D" :amount 4 :units "Stückerl"}]
        expect [{:name "A" :amount 1 :units "Kg"} {:name "B" :amount 2 :units "Stück"}]]
    (is (= expect (preview-component/get-preview-items all-items)))))

(deftest test-method-text-input
  (preview-component/text-input "foobar")
  (is (= "foobar" (get @app-state/app-state :searchText)))
  (is (= 1 (:current-page (get @app-state/app-state :preview-pagination)))))

(deftest test-method-add-item
  (preview-component/add-item "foobar" "kg" 1) ;; add one item
  (is (= [{:name "foobar" :units "kg" :amount 1}] (get @app-state/app-state :list)))
  (preview-component/add-item "bazfoo" "tons" 3) ;; append one item to the list
  (is (= [{:name "foobar" :units "kg" :amount 1} {:name "bazfoo" :units "tons" :amount 3}] (get @app-state/app-state :list)))
  (preview-component/add-item "bazfoo" "tons" -1) ;; reduce bazfoo by one
  (is (= [{:name "foobar" :units "kg" :amount 1} {:name "bazfoo" :units "tons" :amount 2}] (get @app-state/app-state :list)))
  (preview-component/add-item "foobar" "kg" -1) ;; should delete foobar
  (is (= [{:name "bazfoo" :units "tons" :amount 2}] (get @app-state/app-state :list))))

(deftest test-method-delete-item
  (preview-component/delete-item "bazfoo")
  (is (= '() (get @app-state/app-state :list))))

(deftest test-method-input-parser
  (is (= [{:name "Milch", :amount 1, :units "Liter"}] (preview-component/input-parser "Milch"))) ;; find a exact match
  (is (= [{:name "Milch", :amount 1, :units "Liter"} {:name "Schokolade", :amount 1, :units "Stück"} {:name "Milka", :amount 1, :units "Stück"} {:name "Alpia", :amount 1, :units "Stück"}] (preview-component/input-parser "M"))) ;; find a appropiate match
  (is (= [] (preview-component/input-parser "this_should_not_match"))) ;; should find no match
  (is (= [{:name "Schokolade", :amount 3, :units "Stück"} {:name "Milka", :amount 3, :units "Stück"} {:name "Alpia", :amount 3, :units "Stück"}] (preview-component/input-parser "3 alpia")))) ;; should find alpia, despite it is an alias with quantity of 3
