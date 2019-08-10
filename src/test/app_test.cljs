(ns test.app-test
  (:require [cljs.test :refer (deftest is)])
  (:require [ines.app :as app]))

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
  (is (= 3 (app/calculate-max-pages [1 2 3 4 5 6])))
  (is (= 3 (app/calculate-max-pages [1 2 3 4 5])))
  (is (= 2 (app/calculate-max-pages [1 2 3])))
  (is (= 1 (app/calculate-max-pages [1 2])))
  (is (= 1 (app/calculate-max-pages [1]))))

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