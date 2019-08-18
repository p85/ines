(ns inez.list-component
  (:require [inez.app-state :as app-state])
  (:require [inez.preview-component :as preview-component]))

(defn list-component []
  "list component"
  (when-let [currentList (get @app-state/app-state :list)]
    (when-not (empty? currentList)
      [:div {:class "container alert alert-info list-label"} "Dein Einkaufszettel:" [:br] [:br]
       (for [item currentList]
         [:div {:key (:name item) :class "row list-group-item"}
          [:div {:class "col-md-2 list-badge badge badge-primary badge-pill amount-badge"} (:amount item) " " (:units item)]
          [:div {:class "col-md-10 list-group-item-name"} (:name item)]
          [:img {:src "img/plus.svg" :alt "+1" :class "list-action-button add-item-button" :on-click #(preview-component/add-item (:name item) (:units item) 1)}]
          [:img {:src "img/minus.svg" :alt "-1" :class "list-action-button minus-item-button" :on-click #(preview-component/add-item (:name item) (:units item) -1)}]
          [:img {:src "img/trash.svg" :alt "Löschen" :class "list-action-button delete-item-button" :on-click #(preview-component/delete-item (:name item))}]])])))