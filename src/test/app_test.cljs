(ns test.app-test
  (:require [cljs.test :refer (deftest is)])
  (:require [inez.app :as app]))

;; populate the state with example items
(swap! app/app-state assoc :items [{:name ["Milch"] :units "Liter"}
                                   {:name ["Jogurt" "Danone"] :units "Kg"}
                                   {:name ["Brot" "Bread" "Vollkorn"] :units "Stück"}
                                   {:name ["Reis" "Rice" "Uncle" "Bens"] :units "Beutel"}
                                   {:name ["Schokolade" "Milka" "Alpia"] :units "Stück"}])
(swap! app/app-state assoc :preview-pagination {:current-page 1 :page-size 2 :total-pages 0})

(deftest test-the-initial-app-state
  (let [as @app/app-state]
    (is (contains? as :searchText))
    (is (contains? as :showPreview))
    (is (= false (:showPreview as)))
    (is (map? (:preview-pagination as)))
    (is (pos? (:current-page (:preview-pagination as))))
    (is (pos? (:page-size (:preview-pagination as))))
    (is (>= (:total-pages (:preview-pagination as)) 0))
    (is (vector? (:list as)))
    ;; item-list is being tested in item_list_test.cljs
    ))

(deftest test-method-calculate-max-pages
  (let [page-size (:page-size (get @app/app-state :preview-pagination))
        test-values [[1 2 3 4 5 6] [1 2 3 4 5] [1 2 3] [1 2] [1]]]
    (doseq [items test-values]
      (let [expect-pages (Math/ceil (/ (count items) page-size))]
        (is (= expect-pages (app/calculate-max-pages items)))))))

(deftest test-method-go-to-page
  (app/go-to-page 2)
  (is (= 2 (:current-page (get @app/app-state :preview-pagination))))
  (app/go-to-page 1)
  (is (= 1 (:current-page (get @app/app-state :preview-pagination)))))

(deftest test-method-show-preview-results
  (swap! app/app-state assoc-in [:preview-pagination :total-pages] 2)
  (let [all-items [{:name "A" :amount 1 :units "Kg"} {:name "B" :amount 2 :units "Stück"} {:name "C" :amount 3 :units "T"} {:name "D" :amount 4 :units "Stückerl"}]
        expect [{:name "A" :amount 1 :units "Kg"} {:name "B" :amount 2 :units "Stück"}]]
    (is (= expect (app/get-preview-items all-items)))))

(deftest test-method-text-input
  (app/text-input "foobar")
  (is (= "foobar" (get @app/app-state :searchText)))
  (is (= 1 (:current-page (get @app/app-state :preview-pagination)))))

(deftest test-method-show-preview
  (is (= true (:showPreview (app/show-preview true))))
  (is (= false (:showPreview (app/show-preview false)))))

(deftest test-method-add-item
  (app/add-item "foobar" "kg" 1) ;; add one item
  (is (= [{:name "foobar" :units "kg" :amount 1}] (get @app/app-state :list)))
  (app/add-item "bazfoo" "tons" 3) ;; append one item to the list
  (is (= [{:name "foobar" :units "kg" :amount 1} {:name "bazfoo" :units "tons" :amount 3}] (get @app/app-state :list)))
  (app/add-item "bazfoo" "tons" -1) ;; reduce bazfoo by one
  (is (= [{:name "foobar" :units "kg" :amount 1} {:name "bazfoo" :units "tons" :amount 2}] (get @app/app-state :list)))
  (app/add-item "foobar" "kg" -1) ;; should delete foobar
  (is (= [{:name "bazfoo" :units "tons" :amount 2}] (get @app/app-state :list))))

(deftest test-method-delete-item
  (app/delete-item "bazfoo")
  (is (= '() (get @app/app-state :list))))

(deftest test-method-input-parser
  (is (= [{:name "Milch", :amount 1, :units "Liter"}] (app/input-parser "Milch"))) ;; find a exact match
  (is (= [{:name "Milch", :amount 1, :units "Liter"} {:name "Schokolade", :amount 1, :units "Stück"} {:name "Milka", :amount 1, :units "Stück"} {:name "Alpia", :amount 1, :units "Stück"}] (app/input-parser "M"))) ;; find a appropiate match
  (is (= [] (app/input-parser "this_should_not_match"))) ;; should find no match
  (is (= [{:name "Schokolade", :amount 3, :units "Stück"} {:name "Milka", :amount 3, :units "Stück"} {:name "Alpia", :amount 3, :units "Stück"}] (app/input-parser "3 alpia")))) ;; should find alpia, despite it is an alias with quantity of 3
