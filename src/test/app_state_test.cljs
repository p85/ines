(ns test.app-state-test
  (:require [cljs.test :refer (deftest is use-fixtures)])
  (:require [inez.app-state :as app-state])
  (:require [test.fixture :as fixture]))

(use-fixtures :each (fixture/before-each app-state/app-state))

(deftest test-the-initial-app-state
  (let [as @app-state/app-state]
    (is (contains? as :searchText))
    (is (contains? as :showPreview))
    (is (= false (:showPreview as)))
    (is (map? (:preview-pagination as)))
    (is (pos? (:current-page (:preview-pagination as))))
    (is (pos? (:page-size (:preview-pagination as))))
    (is (>= (:total-pages (:preview-pagination as)) 0))
    (is (seq? (:list as)))
    ;; item-list is being tested in item_list_test.cljs
    ))