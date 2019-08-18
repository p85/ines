(ns inez.app-state
  (:require [reagent.core :as reagent :refer [atom]])
  (:require [inez.item-list :as item-list]))

;; the initial app state
(defonce app-state (reagent/atom {:searchText nil
                                  :showPreview false
                                  :preview-pagination {:current-page 1 :page-size 5 :total-pages 0}
                                  :list []
                                  :items item-list/item-list}))