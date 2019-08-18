(ns inez.pagination-component
  (:require [inez.app-state :as app-state]))

(defn calculate-max-pages [all-items]
  "calculates the total-pages for the preview-list"
  (Math/ceil (/ (count (into [] all-items)) (:page-size (get @app-state/app-state :preview-pagination)))))

(defn go-to-page [page]
  "changes the actual preview list page"
  (swap! app-state/app-state assoc-in [:preview-pagination :current-page] page))

(defn pagination-component []
  "pagination component, for navigating between preview-list items"
  (let [pagination-config (get @app-state/app-state :preview-pagination)
        current-page (:current-page pagination-config)
        page-size (:page-size pagination-config)
        total-pages (:total-pages pagination-config)]
    (for [page (range 1 (inc total-pages))]
      [:div {:class "pagination-page-element" :key page}
       (if (= page current-page)
         [:div {:class "pagination-page-element-button-active" :on-click #(go-to-page page)} page]
         [:div {:class "pagination-page-element-button-inactive" :on-click #(go-to-page page)} page])])))