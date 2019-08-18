(ns test.pagination-component-test
  (:require [cljs.test :refer (deftest is use-fixtures)])
  (:require [inez.app-state :as app-state])
  (:require [test.fixture :as fixture])
  (:require [inez.pagination-component :as pagination-component]))

(use-fixtures :each (fixture/before-each app-state/app-state))

(deftest test-method-calculate-max-pages
  (let [page-size (:page-size (get @app-state/app-state :preview-pagination))
        test-values [[1 2 3 4 5 6] [1 2 3 4 5] [1 2 3] [1 2] [1]]]
    (doseq [items test-values]
      (let [expect-pages (Math/ceil (/ (count items) page-size))]
        (is (= expect-pages (pagination-component/calculate-max-pages items)))))))

(deftest test-method-go-to-page
  (pagination-component/go-to-page 2)
  (is (= 2 (:current-page (get @app-state/app-state :preview-pagination))))
  (pagination-component/go-to-page 1)
  (is (= 1 (:current-page (get @app-state/app-state :preview-pagination)))))